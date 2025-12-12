package kr.java.upload_exception.controller;

import jakarta.validation.Valid;
import kr.java.upload_exception.exception.FileStorageException;
import kr.java.upload_exception.exception.InvalidFileTypeException;
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

    // http://localhost:8080/reviews/100
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404"; // 포워딩.
    }

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
    @PostMapping
    public String create(
            @Valid @ModelAttribute Review review,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes
            ) {
        // 에러를 bindingResult 안에 담음 (검증) -> throw가 발생되지 않음
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageName", "리뷰 작성");
            model.addAttribute("bindingResult", bindingResult);
            return "review/form";
        }

        try {
            reviewService.create(review, imageFile);
            redirectAttributes.addFlashAttribute("message", "리뷰가 등록되었습니다.");
            return "redirect:/reviews";
        } catch (InvalidFileTypeException e) {
            // 파일 타입 오류 -> 입력 데이터 유지하면서 폼으로 복귀
            model.addAttribute("review", review);
            model.addAttribute("errorMessage", e.getMessage());
            return "review/form";
        } catch (FileStorageException e) {
            // 파일 저장 오류 -> 폼으로 복귀
            model.addAttribute("review", review);
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생하였습니다.");
            return "review/form";
        }
        // try-catch로 처리.
        // -> Handler, Advice...
    }


    // 리뷰 개별 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("review", reviewService.findById(id));
        model.addAttribute("pageName", "리뷰 상세");
        return "review/detail";
    }

    // 리뷰 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("review", reviewService.findById(id));
        model.addAttribute("pageName", "리뷰 수정");
        return "review/edit";
    }

    // 리뷰 수정 처리
    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute Review review,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageName", "리뷰 수정");
            model.addAttribute("bindingResult", bindingResult);
            return "review/edit";
        }
        reviewService.update(id, review, imageFile);

        redirectAttributes.addFlashAttribute("message", "리뷰가 수정되었습니다.");

//        return "redirect:/reviews/{id}";
        return "redirect:/reviews/" + id;
    }

    // 리뷰 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.delete(id);
        redirectAttributes.addFlashAttribute("message", "리뷰가 삭제되었습니다.");
        return "redirect:/reviews";
    }
}
