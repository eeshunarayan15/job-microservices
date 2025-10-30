package com.micro.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be ≤ 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be ≤ 1000 characters")
    private String description;

    @NotBlank(message = "Min Salary  is required")
    private String  minSalary;

    @NotBlank(message = "Location is required")
    @Size(max = 100, message = "Location must be ≤ 100 characters")
    private String location;

    @Positive(message = "Company ID must be positive")
    private Long companyId;

}
