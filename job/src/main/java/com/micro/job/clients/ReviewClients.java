package com.micro.job.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "REVIEW")
public interface ReviewClients {
}
