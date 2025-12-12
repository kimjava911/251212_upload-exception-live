package kr.java.upload_exception.service;

import kr.java.upload_exception.model.entity.Review;
import kr.java.upload_exception.model.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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

    // 수정
    @Transactional
    public Review update(Long id, Review updatedData, MultipartFile newImageFile) {
        Review review = findById(id);

        // JPA - 더티체킹 -> save.
        review.setTitle(updatedData.getTitle());
        review.setContent(updatedData.getContent());
        review.setRating(updatedData.getRating());

        if (newImageFile != null && !newImageFile.isEmpty()) {
            // 기존 이미지 삭제
            deleteOldImage(review.getImageUrl());
            // 새 이미지 저장
            String storedFilename = fileStorageService.store(newImageFile);
            review.setImageUrl("/images/" + storedFilename);
        }
        return review;
    }

    // 기존 이미지 삭제
    private void deleteOldImage(String imageUrl) {
        if (StringUtils.hasText(imageUrl)) {
            String filename = imageUrl.replace("/images/", "");
            fileStorageService.delete(filename);
        }
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        Review review = findById(id);
        deleteOldImage(review.getImageUrl());
//        reviewRepository.delete(review);
        reviewRepository.deleteById(id);
    }

}
