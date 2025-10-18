package com.micro.company.model;

//import com.job.application.job.model.Job;
//import com.job.application.reviews.model.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private  String description;
    private  Double averagerating=0.0;

}
