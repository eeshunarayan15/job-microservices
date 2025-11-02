package com.micro.job.clients;

import com.micro.job.external.CompanyDto;
import com.micro.job.response.Apiresponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "COMPANY")
public interface CompanyClients {
    @GetMapping("/public/{id}")
   Apiresponse<CompanyDto> getCompanyById(@PathVariable("id") Long id) ;
}
