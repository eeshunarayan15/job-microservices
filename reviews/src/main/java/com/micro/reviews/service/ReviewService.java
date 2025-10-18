package com.micro.reviews.service;


import com.micro.reviews.clients.CompanyClients;
import com.micro.reviews.dto.ReviewRequest;
import com.micro.reviews.dto.ReviewResponseDto;
import com.micro.reviews.dto.external.CompanyDto;
import com.micro.reviews.exception.ResourceNotFoundException;
import com.micro.reviews.messaging.ReviewMessageProducer;
import com.micro.reviews.model.Review;
import com.micro.reviews.repository.ReviewRepository;
import com.micro.reviews.response.Apiresponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CompanyClients companyClients;
    private final RestTemplate restTemplate;
    private  final ReviewMessageProducer reviewMessageProducer;


    public List<ReviewResponseDto> getAllReviews(Long companyId) {

            long startTime = System.currentTimeMillis();
            Apiresponse<CompanyDto> companyById = companyClients.getCompanyById(companyId);

            System.out.println("Response from company service: " + companyById);
            if (companyById != null) {
                System.out.println("Data field: " + companyById.getData());
                System.out.println("Message field: " + companyById.getMessage());
            } else {
                System.out.println("companyById is null (Feign returned null)");
            }

            if (companyById.getData() == null) {
                throw new ResourceNotFoundException("Company not found with id: " + companyId);
            }

            System.out.println(companyById.getData());



        List<Review> reviewList = reviewRepository.findByCompanyId(companyId);
//
        // Check if reviews exist for this company
        if (reviewList.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found for company id: " + companyId);
        }
//        // Convert entities to DTOs
        List<ReviewResponseDto> list = reviewList.stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getTitle(),
                        review.getDescription(),
                        review.getRating(),
                        review.getCompanyId()
                ))
                .toList();// or .collect(Collectors.toList()) for older Java versions
        long endTime = System.currentTimeMillis();  // ← End timer
        log.info("getAllReviews took {} ms", (endTime - startTime));  // ← Log it
return  list;


    }
    public Review createReview(Long companyId, ReviewRequest reviewRequest) {
        Apiresponse<CompanyDto> companyById = companyClients.getCompanyById(companyId);
        CompanyDto data = companyById.getData();
        if (companyById.getData() == null) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }

        Review review = Review.builder()
                .title(reviewRequest.getTitle())
                .description(reviewRequest.getDescription())
                .companyId(data.getId())
                .rating(reviewRequest
                .getRating()).
                build();
        Review savedReview = reviewRepository.save(review);

        return savedReview;


    }
    public void deleteReview(Long companyId, Long reviewId) {
        // Step 1: Validate company
        Apiresponse<CompanyDto> companyById = companyClients.getCompanyById(companyId);
        if (companyById == null || companyById.getData() == null) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }

        // Step 2: Fetch review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Step 3: Check company ownership
        if (!review.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("Review does not belong to company with id: " + companyId);
        }

        // Step 4: Delete
        reviewRepository.delete(review);
    }

}
