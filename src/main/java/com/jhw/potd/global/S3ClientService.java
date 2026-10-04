package com.jhw.potd.global;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jhw.potd.global.dto.CustomException;
import com.jhw.potd.global.dto.ErrorCode;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ClientService {

	private final S3Client s3Client;

	@Value("${cloud.aws.S3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	public String uploadImage(MultipartFile file) {
		validateImage(file);

		String key = generateKey(file.getOriginalFilename());

		PutObjectRequest request = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(file.getContentType())
			.contentLength(file.getSize())
			.build();

		try {
			s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (IOException e) {
			throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
		}

		return toObjectUrl(key);
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
		String extension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		return "image/" + UUID.randomUUID() + extension;
	}

	private String toObjectUrl(String key) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
	}
}