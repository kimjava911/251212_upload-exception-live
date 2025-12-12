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
public class FileStorageService {
    // java.nio.file.Path
    private final Path uploadPath;

    // value -> 생성자 주입, 필드 주입
    // org.springframework.beans.factory.annotation.Value
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
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
            "image/jpeg", // jpg, jpeg
            "image/png",
            "image/gif",
            "image/webp"
    );

    // MultipartFile -> 저장된 파일명
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            // 빈 파일 체크
            throw new FileStorageException("빈 파일은 업로드할 수 없습니다");
        }

        // 파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException("허용되지 않는 파일 형식입니다. (허용: jpg, png, gif, webp)");
        }

        // 원본 파일명에서 확장자 추출 (filename.extension)
        String originalFilename = file.getOriginalFilename();
//        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String extension = extractExtension(originalFilename);
        // image.jpg -> image[.]jpg -> .jpg

        // UUID - 웬만하면 정말 극한의 확률로 겹치는 값. (웬만하면 안 겹침)
        String storedFilename = UUID.randomUUID() + extension;

        // ../../../etc/password -> 보안 공격 (Path Traversal)
        if (storedFilename.contains("..")) {
            throw new FileStorageException("파일명에 허용되지 않은 문자가 포함되어 있습니다.");
        }

        try {
            // 최종 저장 경로
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
//            e.printStackTrace();
//            System.err.println("파일 삭제 실패 : " + filename);
            // 삭제 실패 (에러 나는 경우)
            log.error(e.getMessage());
//            log.error("파일 삭제 실패 : " + filename);
            log.error("파일 삭제 실패 : {}", filename);
        }
    }
}
