package com.micro.company.client;

import com.micro.company.respone.Apiresponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "REVIEW")
public interface ReviewClient {
    @GetMapping("/api/v1/reviews/averageRating")
    public Apiresponse<Double> getAverageRating(@RequestParam Long companyId) ;

}

