package kr.java.upload_exception.service;

import kr.java.upload_exception.exception.FileStorageException;
import kr.java.upload_exception.exception.InvalidFileTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

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
        return "";
    }

    @Override
    public void delete(String key) {

    }

    @Override
    public String getUrl(String key) {
        return "/images/" + key;
    }
}
