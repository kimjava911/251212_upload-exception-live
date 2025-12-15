package kr.java.upload_exception.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

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
            ResponseInputStream<GetObjectResponse> s3Object = s3Client
                    .getObject(getObjectRequest);
            GetObjectResponse s3ObjectResponse = s3Object.response(); // 객체 (메타데이터)

            InputStreamResource resource = new InputStreamResource(s3Object); // 파일 그 자체

            // 응답
            HttpHeaders headers = new HttpHeaders();
            // image/png, image/jpg ...
            headers.setContentType(MediaType.parseMediaType(s3ObjectResponse.contentType()));
            headers.setContentLength(s3ObjectResponse.contentLength());
            // (주소로 접속 시) 이미지를 다운로드가 아니라 '바로 표시'
//            headers.add(
//                    HttpHeaders.CONTENT_DISPOSITION,
//                    "inline; filename=\"" + filename + "\"");
            headers.add(
                    HttpHeaders.CONTENT_DISPOSITION,
                    """
                    inline; filename="%s"
                    """.formatted(filename));

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            // 해당 파일이 없거나 다른 오류 발생
            log.error("파일 다운로드 실패 : {}", e.getMessage());
//            return ResponseEntity.notFound().build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
