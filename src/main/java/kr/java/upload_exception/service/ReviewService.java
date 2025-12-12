package kr.java.upload_exception.service;

import kr.java.upload_exception.model.entity.Review;
import kr.java.upload_exception.model.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
// org.springframework.transaction.annotation.Transactional
@Transactional(readOnly = false)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;

    ///  전체 리뷰 목록 조회 !
    /**
     *   전체 리뷰 목록 조회 ?
     */
    public List<Review> findAll() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }
}
