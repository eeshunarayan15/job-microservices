package com.micro.job.service;


import com.micro.job.clients.CompanyClients;
import com.micro.job.dto.AllJOBDto;
import com.micro.job.dto.JobDto;
import com.micro.job.dto.JobRequest;
import com.micro.job.exception.ResourceNotFoundException;
import com.micro.job.exception.ServiceUnavailableException;
import com.micro.job.external.CompanyDto;
import com.micro.job.model.Job;
import com.micro.job.repository.JobRepository;
import com.micro.job.response.Apiresponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    private final JobRepository jobRepository;
    private final RestTemplate restTemplate;
    private final CompanyClients companyClients;
    int attempt=0;

    @CircuitBreaker(name = "companyBreaker")
    public List<AllJOBDto> getAllJob() {
        List<Job> jobs = jobRepository.findAll();
        List<AllJOBDto> list = jobs.stream()
                .map(job -> new AllJOBDto(
                        job.getId(),
                        job.getTitle(),
                        job.getDescription(),
                        job.getMinSalary(),
                        job.getLocation(),
                        job.getCompanyId()
                ))
                .toList();
        System.out.println(list);
        return list;
    }



    // IMPORTANT: Retry should be BEFORE CircuitBreaker in the execution chain
    // This means CircuitBreaker annotation comes first (outer), Retry second (inner)
    @Retry(name = "companyBreaker")
    @CircuitBreaker(name = "companyBreaker")
    public JobDto getJobById(Long id) {
        System.out.println("Attempt"+ ++attempt);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        CompanyDto company = restTemplate.getForObject(
                "http://COMPANY/api/v1/public/" + job.getCompanyId(),
                CompanyDto.class
        );

        Apiresponse<CompanyDto> companyById = companyClients.getCompanyById(job.getCompanyId());

        log.info("Company response: {}", companyById);
        log.info("Company data: {}", companyById.getData());
        JobDto jobDto = new JobDto();
        jobDto.setId(job.getId());
        jobDto.setTitle(job.getTitle());
        jobDto.setDescription(job.getDescription());
        jobDto.setMinSalary(job.getMinSalary());
        jobDto.setLocation(job.getLocation());

        jobDto.setName(company.getName());
        jobDto.setDescription(company.getDescription());
        return jobDto;

    }
    // Retry fallback - called after all retry attempts are exhausted
    public JobDto companyRetryFallback(Long id, Exception exception) {
        log.warn("All retry attempts exhausted for job id: {}. Error: {}", id, exception.getMessage());
        // Re-throw to trigger circuit breaker fallback
        throw new RuntimeException("Retries exhausted", exception);
    }
    // FIXED: Fallback method must return JobDto and accept (Long id, Exception exception)
    public JobDto companyBreakerFallback(Long id, Exception exception) {
        log.error("Circuit breaker fallback triggered for job id: {}. Error: {}", id, exception.getMessage());

        // Return job details without company information
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobDto jobDto = new JobDto();
        jobDto.setId(job.getId());
        jobDto.setTitle(job.getTitle());
        jobDto.setDescription(job.getDescription());
        jobDto.setMinSalary(job.getMinSalary());
        jobDto.setLocation(job.getLocation());

        // Set default values for company info since service is down
        jobDto.setName("Company service unavailable");
        jobDto.setDescription("Please try again later");

        return jobDto;
    }

//    public Job createJob(JobRequest jobRequest) {
//        try {
//            CompanyDto company = restTemplate.getForObject(
//                    "http://COMPANY/api/v1/public/" + jobRequest.getCompanyId(),
//                    CompanyDto.class
//            );
//
//            if (company == null) {
//                throw new ResourceNotFoundException("Company not found with id: " + jobRequest.getCompanyId());
//            }
//        } catch (Exception e) {
//            throw new ResourceNotFoundException("Company not found with id: " + jobRequest.getCompanyId());
//        }
//
//        Job job = Job.builder()
//                .title(jobRequest.getTitle())
//                .description(jobRequest.getDescription())
//                .minSalary(jobRequest.getMinSalary())
//                .location(jobRequest.getLocation())
//                .companyId(jobRequest.getCompanyId())
//                .build();
//
//        return jobRepository.save(job);
//
//
//    }
//





    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "createJobFallback")
    @Retry(name = "companyBreaker")
    public Job createJob(JobRequest req) {
        log.info("Validating company existence: {}", req.getCompanyId());

        // This call throws on 4xx/5xx → caught by CircuitBreaker
        CompanyDto company = restTemplate.getForObject(
                "http://COMPANY/api/v1/public/{id}",
                CompanyDto.class,
                req.getCompanyId());

        // Only proceed if company exists
        if (company == null) {
            throw new ResourceNotFoundException("Company not found: " + req.getCompanyId());
        }

        Job job = Job.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .minSalary(req.getMinSalary())
                .location(req.getLocation())
                .companyId(req.getCompanyId())
                .build();

        Job saved = jobRepository.save(job);
        log.info("Job saved with ID: {}", saved.getId());
        return saved;
    }

    // Fallback: COMPANY service is DOWN → fail, do NOT save
    public Job createJobFallback(JobRequest req, Exception ex) {
        log.error("Company service unavailable. Cannot validate companyId: {}. Aborting job creation.",
                req.getCompanyId(), ex);

        throw new ServiceUnavailableException("Company service is down. Please try again later.");
    }
//    public void deleteJob(Long id) {
//        if (!jobRepository.existsById(id)) {
//            throw new RuntimeException("Job with id " + id + " not found");
//        }
//        jobRepository.deleteById(id);
//
//    }
}
