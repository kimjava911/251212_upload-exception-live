package kr.java.upload_exception.controller;

import kr.java.upload_exception.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 목록 페이지
    // GET http://localhost:8080/reviews/
    @GetMapping
    // org.springframework.ui.Model
    public String list(Model model) {
        model.addAttribute("reviews",
                reviewService.findAll());
        model.addAttribute("pageName", "리뷰 목록");
        return "review/list";
    }
}
