package com.micro.reviews.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ReviewMessage {
    private  long id;
    private  String title;
    private String description;
    private double rating;
    private  Long companyId;
}
