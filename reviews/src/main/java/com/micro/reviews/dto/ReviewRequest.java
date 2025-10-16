package com.micro.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ReviewRequest {
    private String title;
    private String description;
    private double rating;
}
