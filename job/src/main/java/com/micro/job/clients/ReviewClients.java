package com.micro.job.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "REVIEW",
        url="${company-service.url}")
public interface ReviewClients {
}
