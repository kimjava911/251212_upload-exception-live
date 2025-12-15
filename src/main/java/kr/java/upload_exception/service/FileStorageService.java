package kr.java.upload_exception.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    // 1. store(MultifileFile file)
    String store(MultipartFile file); // -> uploads나 s3 상에서 호출할 수 있는 key
    // 2. delete(String key)
    void delete(String key);
    // 3. getUrl(String key)
    String getUrl(String key);
    // key -> 실제 접속할 수 있는 경로
}
