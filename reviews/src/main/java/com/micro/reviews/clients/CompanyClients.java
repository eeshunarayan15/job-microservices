package com.micro.reviews.clients;
import com.micro.reviews.dto.external.CompanyDto;
import com.micro.reviews.response.Apiresponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "COMPANY")
public interface CompanyClients {
    @GetMapping("/api/v1/public/{id}")
    Apiresponse<CompanyDto> getCompanyById(@PathVariable(value = "id") Long id) ;
}
