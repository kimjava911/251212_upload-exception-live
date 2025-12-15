package kr.java.upload_exception.service;

import io.awspring.cloud.s3.S3Exception;
import kr.java.upload_exception.exception.FileStorageException;
import kr.java.upload_exception.exception.InvalidFileTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j // 로그 출력을 위한 연결
@ConditionalOnProperty(
        name = "file.storage.type",
//        havingValue = "local",
        havingValue = "s3"
//        matchIfMissing = true
)
@RequiredArgsConstructor // 생성자 주입
public class S3FileStorageService implements FileStorageService {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            throw new FileStorageException("빈 파일은 업로드할 수 없습니다");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException("허용되지 않는 파일 형식입니다. (허용: jpg, png, gif, webp)");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    // S3Client
    private final S3Client s3Client;

    // bucket name
    @Value("${aws.s3.bucket}")
    // import org.springframework.beans.factory.annotation.Value;
    private String bucketName;

    @Override
    public String store(MultipartFile file) {
        validateFile(file); // 파일을 검증
        String extension = extractExtension(file.getOriginalFilename());
        // 파일 확장자 추출
        String key = UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request,
                    // software.amazon.awssdk.core.sync.RequestBody;
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    ));

            return key;
        } catch (IOException | S3Exception e) {
            throw new FileStorageException("S3 업로드 실패: " + e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        if (!StringUtils.hasText(key)) {
            return;
        }
        try {
            // S3 SDK
            // Request -> S3Client
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            log.error("S3 삭제 실패: {}", e.getMessage());
        }
    }

    @Override
    public String getUrl(String key) {
        return "/images/" + key;
    }
}
