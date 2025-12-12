package kr.java.upload_exception.controller;

import jakarta.validation.Valid;
import kr.java.upload_exception.model.entity.Review;
import kr.java.upload_exception.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // 리뷰 생성 폼
    // GET -> form
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("review", new Review());
        model.addAttribute("pageName", "리뷰 작성");
        return "review/form";
    }

    // POST -> data
    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute Review review,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes
            ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageName", "리뷰 작성");
            model.addAttribute("bindingResult", bindingResult);
            return "review/form";
        }
        reviewService.create(review, imageFile);

        redirectAttributes.addFlashAttribute("message", "리뷰가 등록되었습니다.");

        return "redirect:/reviews";
    }


    // 리뷰 개별 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("review", reviewService.findById(id));
        model.addAttribute("pageName", "리뷰 상세");
        return "review/detail";
    }
}
