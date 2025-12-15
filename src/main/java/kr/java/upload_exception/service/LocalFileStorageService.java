package kr.java.upload_exception.service;

import kr.java.upload_exception.exception.FileStorageException;
import kr.java.upload_exception.exception.InvalidFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j // 로그 출력을 위한 연결
public class LocalFileStorageService implements FileStorageService {
    // java.nio.file.Path
    private final Path uploadPath;

    // org.springframework.beans.factory.annotation.Value
    public LocalFileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath().normalize();
        // 업로드 디렉토리가 없을 경우 생성
        try {
            Files.createDirectories(uploadPath); // 생성 시도
        } catch (IOException e) {
            throw new FileStorageException("업로드 디렉토리를 생성할 수 없습니다.");
        }
    }

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            throw new FileStorageException("빈 파일은 업로드할 수 없습니다");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException("허용되지 않는 파일 형식입니다. (허용: jpg, png, gif, webp)");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);

        String storedFilename = UUID.randomUUID() + extension;

        if (storedFilename.contains("..")) {
            throw new FileStorageException("파일명에 허용되지 않은 문자가 포함되어 있습니다.");
        }

        try {
            Path targetPath = uploadPath.resolve(storedFilename);
            file.transferTo(targetPath);
            return storedFilename;
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public void delete(String filename) {
        if (!StringUtils.hasText(filename)) {
            return;
        }

        try {
            Path filePath = uploadPath.resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 삭제 실패 (에러 나는 경우)
            log.error(e.getMessage());
            log.error("파일 삭제 실패 : {}", filename);
        }
    }

    @Override
    public String getUrl(String key) {
        return "/images/" + key;
    }
}
