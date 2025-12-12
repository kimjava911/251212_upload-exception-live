package kr.java.upload_exception.service;

import kr.java.upload_exception.model.entity.Review;
import kr.java.upload_exception.model.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
// org.springframework.transaction.annotation.Transactional
@Transactional(readOnly = false)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;

    public List<Review> findAll() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID : " + id)
                );
    }

    @Transactional // 쓰기 작업
    public Review create(Review review, MultipartFile imageFile) {
        // 이미지 파일이 있으면 저장
        if (imageFile != null && !imageFile.isEmpty()) {
            String storedFilename = fileStorageService.store(imageFile);
            review.setImageUrl("/images/" + storedFilename);
        }
        return reviewRepository.save(review);
    }
}
