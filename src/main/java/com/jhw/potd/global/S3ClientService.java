package com.jhw.potd.global;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ClientService {

	private static final String ORIGIN_PREFIX = "origin/";
	private static final String RESIZE_PREFIX = "resize/";

	private final S3Client s3Client;
	private final ImageResizer resizer;

	@Value("${cloud.aws.S3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;


	public String improvedUploadImage(MultipartFile file) {
		validateImage(file);

		String extension = extractExtension(file.getOriginalFilename());
		String uuid = UUID.randomUUID().toString();
		String originKey = ORIGIN_PREFIX + uuid + extension;
		String resizeKey = RESIZE_PREFIX + uuid + extension;

		byte[] originBytes;
		try {
			originBytes = file.getBytes();
		} catch (IOException e) {
			throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
		}

		putBytes(originKey, originBytes, file.getContentType());
		byte[] resizedBytes = resizeToBytes(originBytes, extension);
		putBytes(resizeKey, resizedBytes, file.getContentType());

		return toObjectUrl(resizeKey);
	}

	// resize/ 폴더의 리사이즈본 바이트를 반환 — 피드 리스트 등 일반 조회용
	// 주의: byte[] 를 메모리에 통째로 올리므로 대용량/고동시성 환경에서는 힙 압박 가능
	public byte[] getResizeImage(String filename) {
		return getImage(RESIZE_PREFIX + filename);
	}

	// origin/ 폴더의 원본 바이트를 반환 — 다운로드/고화질 요청용
	// 주의: 원본은 리사이즈본보다 훨씬 크므로 getResizeImage 보다 메모리 비용이 큼
	public byte[] getOriginImage(String filename) {
		return getImage(ORIGIN_PREFIX + filename);
	}

	// S3 객체를 바이트 배열로 가져오는 공통 처리
	// 문제점:
	//  1) 전체 바이트를 메모리에 적재 — 스트리밍이 아니므로 큰 파일/동시 요청에 취약
	//  2) NoSuchKeyException(없는 객체) 도 500 으로 떨어짐 — 404 로 구분하려면 별도 catch 필요
	//  3) S3 를 매 요청마다 거침 — CDN/Presigned URL 대비 레이턴시·비용 불리
	private byte[] getImage(String key) {
		GetObjectRequest request = GetObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.build();
		try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)) {
			return response.readAllBytes();
		} catch (IOException e) {
			throw new CustomException(ErrorCode.FILE_DOWNLOAD_FAILED);
		}
	}

	// 바이트 배열을 지정 키로 S3 에 업로드
	// 기존 putOrigin / putResized 가 각각 다른 소스(스트림/바이트) 로 분리돼 있던 걸
	// byte[] 하나로 통일해, Content-Length 선언값과 실제 송신 바이트가 항상 일치하도록 함
	private void putBytes(String key, byte[] bytes, String contentType) {
		PutObjectRequest request = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(contentType)
			.contentLength((long)bytes.length)
			.build();
		s3Client.putObject(request, RequestBody.fromBytes(bytes));
	}

	// 원본 바이트를 ImageResizer 로 축소 후 다시 바이트 배열로 재인코딩
	// 이전 putResized 는 MultipartFile 에서 매번 getInputStream() 을 꺼내 썼지만,
	// 이제 improvedUploadImage 가 미리 읽어둔 originBytes 를 재활용하므로 스트림 중복 소비 없음
	private byte[] resizeToBytes(byte[] originBytes, String extension) {
		try {
			BufferedImage source = ImageIO.read(new ByteArrayInputStream(originBytes));
			if (source == null) {
				throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
			}
			BufferedImage resized = resizer.resize(source);

			// 확장자(.jpg) 에서 앞의 점을 떼서 ImageIO 포맷명(jpg) 으로 사용
			String format = extension.isEmpty() ? "jpg" : extension.substring(1).toLowerCase();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if ("jpg".equals(format) || "jpeg".equals(format)) {
				writeJpeg(resized, baos, 0.9f);
			} else {
				ImageIO.write(resized, format, baos);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	private void validateImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
		}
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
		}
	}

	private String generateKey(String originalFilename) {
		return ORIGIN_PREFIX + UUID.randomUUID() + extractExtension(originalFilename);
	}

	// 파일명에서 점 포함 확장자(.jpg, .png 등) 추출, 없으면 빈 문자열
	private String extractExtension(String originalFilename) {
		if (originalFilename != null && originalFilename.contains(".")) {
			return originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		return "";
	}

	private String toObjectUrl(String key) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
	}

	private void writeJpeg(BufferedImage image, ByteArrayOutputStream baos, float quality) throws IOException {
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(image, null, null), param);
		} finally {
			writer.dispose();  // 네이티브 리소스 — 반드시 호출
		}
	}
}