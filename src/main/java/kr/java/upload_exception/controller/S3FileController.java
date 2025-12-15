package kr.java.upload_exception.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/images")
@Slf4j
public class S3FileController {
    private final S3Client s3Client;
    // org.springframework.beans.factory.annotation.Value;
    @Value("${aws.s3.bucket}")
    private String bucket;

    // org.springframework.core.io.Resource;
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();
        try {

        } catch (Exception e) {
            // 해당 파일이 없거나 다른 오류 발생
            log.error("파일 다운로드 실패 : {}", e.getMessage());
//            return ResponseEntity.notFound().build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
