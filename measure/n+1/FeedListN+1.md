# Feed List 조회 N+1 측정 (개선 전)

- **엔드포인트**: `GET /feeds/v1/list` (page=0, size=20)
- **도구**: hey
- **조건**: `-n 1000 -c 50`
- **JVM 웜업**: 30초
- **테스트 간격**: 5분, 3회
- **데이터**: 시드 110만 row (`seed.sql` + `seed_social.sql`, 피드당 like 10~50 / comment 3~6)
- **인덱스 상태**: `created_at` 인덱스 적용 완료 (V1 after-index 와 동일)

## 측정 대상 코드 상태

| 위치 | 상태 |
|------|------|
| `FeedResponse.java:24-32` 생성자 | `feed.getUser().getId/getNickname()`, `feed.getComments().size()`, `feed.getLikes().size()` 를 피드마다 호출 |
| `Feed.java` user/comments/likes 매핑 | 전부 `FetchType.LAZY`, fetch join 미적용 |
| `FeedService.getFeeds()` (`FeedService.java:45-50`) | `findAllByOrderByCreatedAtDesc(pageable)` — 작성자/댓글/좋아요 결합 없음 |

응답에 `likeCount` / `commentCount` / `nickname` 이 포함된 상태 — 커밋 `9449513` 에서 FeedResponse 가 확장되며 N+1 이 자연 발생.

## 요청당 쿼리 수 (size=20)

### 실측 — **55 쿼리** (`QueryCounterInspector` 로 측정)

| 소스 | 쿼리 수 | 비고 |
|------|--------|------|
| 피드 목록 SELECT | 1 | `ORDER BY created_at DESC LIMIT 20` |
| COUNT(\*) | 1 | `Page<Feed>` 가 `totalElements` 위해 자동 실행 |
| `likes.size()` × 20 피드 | 20 | LAZY 컬렉션 — **각 피드의 like 행 전부 메모리 로드 후 size** |
| `comments.size()` × 20 피드 | 20 | 동일 |
| User SELECT (유니크 user 수) | 13 | top-20 feed 의 user_id 유니크 수. `getNickname()` 호출 시 proxy 초기화 |
| **합계** | **55** | |

### `.size()` 가 비싼 이유
LAZY 컬렉션의 `.size()` 는 **count 쿼리가 아니라 전체 행 로드**. 시드 평균 like 30건 / comment 4건 → 20 피드당 **like ~600행 + comment ~80행** 을 매 요청마다 긁는다.

### User 쿼리가 20 이 아닌 이유
- `feed.getUser().getId()` 는 proxy 초기화 안 함 (FK 가 부모 row 에 이미 있음) → 쿼리 X.
- `feed.getUser().getNickname()` 만 proxy 초기화 → 1 SELECT.
- Hibernate 1차 캐시 (persistence context, 요청 단위) 가 중복 User 재조회 차단.
- top-20 feed 의 user_id 유니크 수에 따라 변동 — 현재 realization 은 13.

## 결과

| 지표  | 시행 1 (s) | 시행 2 (s) | 시행 3 (s) | 평균 (s) | 중앙값 (s) |
|-------|-----------|-----------|-----------|---------|-----------|
| p50   | 1.2241    | 1.1576    | 1.2365    | 1.2061  | 1.2241    |
| p95   | 1.5925    | 1.4494    | 5.1139    | 2.7186  | 1.5925    |
| p99   | 2.1907    | 2.1320    | 5.4429    | 3.2552  | 2.1907    |
| RPS   | 38.69     | 41.31     | 31.18     | 37.06   | 38.69     |

**Status**: 전 시행 1000 요청 전부 HTTP 200 (에러율 0%).

시행 3 은 **5~6s 구간에 48건** (전체의 4.8%) 이 몰린 롱테일 outlier — GC pause 또는 쿼리 플랜 캐시 미스로 추정. 평균과 중앙값을 함께 제시한다.

## V1 (인덱스 적용 후) 와의 비교

V1 after-index 측정 (`measure/index/latency-comparison.md`) 은 **FeedResponse 가 확장되기 전** 상태라 N+1 이 없다. 두 측정의 차이는 **N+1 오버헤드** 로 해석 가능.

| 지표 | V1 after-index (N+1 없음) | 현재 (N+1 포함) 평균 | 현재 중앙값 | N+1 오버헤드 (중앙값 기준) |
|------|---------------------------|---------------------|-----------|---------------------------|
| p50  | 0.9638                    | 1.2061              | 1.2241    | +0.26s (약 27%)           |
| p95  | 1.5191                    | 2.7186              | 1.5925    | +0.07s (약 5%)            |
| p99  | 1.7220                    | 3.2552              | 2.1907    | +0.47s (약 27%)           |
| RPS  | —                         | 37.06               | 38.69     | — (별도 비교)             |

**해석**
- 응답 필드 3개(nickname / likeCount / commentCount) 추가로 **p50/p99 모두 ~27% 악화**.
- 평균(2.7186 / 3.2552) 과 중앙값(1.5925 / 2.1907) 격차가 큰 건 outlier trial 3 때문. 보수적으로 **중앙값 기준** 해석 권장.
- `likes.size()` 가 메모리로 로드하는 600행이 매 요청마다 발생 → GC 압박 상승. trial 3 롱테일이 이 영향으로 추정.

## Phase 2 SLO 대비

`docs/measurement-standard.md` 기준:

| 지표 | SLO | 현재 (중앙값) | Gap |
|------|-----|-------------|-----|
| 피드 첫 페이지 p99 | ≤ 200ms | **2.19s** | **약 11배 초과** |
| 요청당 쿼리 수 (통합 응답) | ≤ 5 | **55 (실측)** | **약 11배 초과** |
| RPS | ≥ 500 | 38.69 | **약 13배 미달** |

세 축 모두 **1 자릿수 배 수준 초과**. Phase 2 의 before 상태로 적합.

## 개선 계획

1. **fetch join** — `@Query("select f from Feed f join fetch f.user ...")` 로 user 1회만. 다만 `OneToMany` fetch join 은 cartesian product 를 만들어 size=20 요청에 대해 20 × (like수 + comment수) 행이 리턴 → **적용하면 안 됨**.
2. **집계 쿼리 분리** — like/comment 는 `COUNT(*) GROUP BY feed_id` 로 한 번에. 피드 리스트 1 + user fetch join (0, 이미 묶음) + like count 1 + comment count 1 = **총 3 쿼리** 로 수렴.
3. **DTO projection** — `JPQL new com.jhw.potd...FeedResponse(f.id, u.nickname, (select count...), ...)` 로 1 쿼리에 몰기. 가독성은 떨어지지만 쿼리 1개로 가능.

**예상 선택**: (2) 집계 쿼리 분리. 피드 조회 결과를 Map<feedId, count> 로 받아 애플리케이션 레벨에서 매핑. 쿼리 3개로 떨어지면서 복합 인덱스 효과도 명확.

Phase 2 리포트에서 after 측정 시 (2) / (3) 중 선택한 방식으로 재측정.

## 한계

- **쿼리 수는 데이터 realization 의존**: User 쿼리 수 = top-20 feed 의 유니크 user_id 수. 현재 13 (실측 55 - 42) 이지만, 시드 재생성 또는 데이터 추가로 유니크 수가 바뀌면 ±쿼리. 상한은 42 (list+count+likes+comments) + 20 = **62**, 하한은 42 + 1 = **43**.
- **trial 3 outlier**: p95/p99 평균 왜곡. 중앙값을 기준으로 하거나, 재측정 1회 추가가 더 신뢰도 높음.
- **데이터 규모**: 시드 110만 (목표 1,000만의 1/9). 큰 규모에서는 N+1 당 쿼리별 I/O 가 더 커지므로 오버헤드가 비례 이상 증가 가능.
- **분포**: 균등 분포라 핫 피드 (좋아요 10만+) 상황 재현 불가. 핫 피드에서는 `likes.size()` 가 수만 행 로드로 **단일 요청이 수십 초** 걸릴 가능성 — 이 영역은 Phase 3 에서 별도 측정.

## 참고

- `measure/index/latency-comparison.md` — V1 인덱스 적용 전후 비교 (현재 측정의 베이스라인)
- `src/main/java/com/jhw/potd/controller/dto/response/FeedResponse.java` — 측정 대상 응답 DTO
- `src/main/java/com/jhw/potd/service/FeedService.java` — 피드 조회 서비스
- `measure/n+1/trial-1.txt`, `trial-2.txt`, `trial-3.txt` — hey 원본 출력