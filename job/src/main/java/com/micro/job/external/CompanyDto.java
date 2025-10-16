package com.micro.job.external;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
    private long id;
    private String name;
    private  String description;

}
