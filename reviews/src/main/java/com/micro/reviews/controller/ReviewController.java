package com.micro.reviews.controller;
import com.micro.reviews.dto.ReviewRequest;
import com.micro.reviews.dto.ReviewResponseDto;
import com.micro.reviews.messaging.ReviewMessageProducer;
import com.micro.reviews.model.Review;
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
    private final ReviewMessageProducer reviewMessageProducer;
//
    @GetMapping
    public ResponseEntity<Apiresponse<List<ReviewResponseDto>>> getAllReviews(@RequestParam Long companyId) {
        List<ReviewResponseDto> allReviews = reviewService.getAllReviews(companyId);
        Apiresponse<List<ReviewResponseDto>> listApiresponse = new Apiresponse<>("Sucess", "All Reviews", allReviews);
        return ResponseEntity.status(HttpStatus.OK).body(listApiresponse);
    }

    @PostMapping
    public ResponseEntity<Apiresponse<Review>> createReview(@RequestParam Long companyId, @RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.createReview(companyId,reviewRequest);

        reviewMessageProducer.sendMessage(review);

        Apiresponse<Review> apiresponse = new Apiresponse<Review>("Sucess", "Review Created", review);
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
    @GetMapping("/averageRating")
    public ResponseEntity<Apiresponse<Double>> getAverageRating(@RequestParam Long companyId) {
        List<ReviewResponseDto> allReviews = reviewService.getAllReviews(companyId);
        double avarageReview = allReviews.stream().mapToDouble(ReviewResponseDto::getRating).average()
                .orElse(0.0);
        Apiresponse<Double> doubleApiresponse = new Apiresponse<>("Sucess", "Average Review", avarageReview);
        return  ResponseEntity.status(HttpStatus.OK).body(doubleApiresponse);

    }

}

