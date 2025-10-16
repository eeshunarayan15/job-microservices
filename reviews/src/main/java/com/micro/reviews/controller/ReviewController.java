package com.micro.reviews.controller;
import com.micro.reviews.dto.ReviewRequest;
import com.micro.reviews.dto.ReviewResponseDto;
import com.micro.reviews.response.Apiresponse;
import com.micro.reviews.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
//
    @GetMapping
    public ResponseEntity<Apiresponse<List<ReviewResponseDto>>> getAllReviews(@RequestParam Long companyId) {
        List<ReviewResponseDto> allReviews = reviewService.getAllReviews(companyId);
        Apiresponse<List<ReviewResponseDto>> listApiresponse = new Apiresponse<>("Sucess", "All Reviews", allReviews);
        return ResponseEntity.status(HttpStatus.OK).body(listApiresponse);
    }

    @PostMapping
    public ResponseEntity<Apiresponse<ReviewResponseDto>> createReview(@RequestParam Long companyId,@RequestBody ReviewRequest reviewRequest) {
        ReviewResponseDto review = reviewService.createReview(companyId,reviewRequest);
        Apiresponse<ReviewResponseDto> apiresponse = new Apiresponse<>("Sucess", "Review Created", review);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiresponse);


    }
    @PutMapping
    public  void  updateReview(@RequestParam Long reviewId, @RequestBody ReviewRequest reviewRequest) {

    }
    @DeleteMapping
    public ResponseEntity<Apiresponse<Long>> deleteReview(@RequestParam Long reviewId, @RequestParam Long companyId) {
        reviewService.deleteReview(companyId,reviewId);
        Apiresponse<Long> deleteApiResponse = new Apiresponse<>("Sucess", "Review Deleted Successfully", reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(deleteApiResponse);

    }

}

