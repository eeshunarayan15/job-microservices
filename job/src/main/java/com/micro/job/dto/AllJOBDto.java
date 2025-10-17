package com.micro.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllJOBDto {
    private long id;
    private String title;
    private String description;
    private String minSalary;
    private String location;
    private Long  companyId;
}
