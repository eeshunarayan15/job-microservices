package com.micro.job.service;


import com.micro.job.clients.CompanyClients;
import com.micro.job.dto.JobDto;
import com.micro.job.dto.JobRequest;
import com.micro.job.exception.ResourceNotFoundException;
import com.micro.job.external.CompanyDto;
import com.micro.job.model.Job;
import com.micro.job.repository.JobRepository;
import com.micro.job.response.Apiresponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    private final JobRepository jobRepository;
    private final RestTemplate restTemplate;
    private  final CompanyClients companyClients;

    public List<Job> getAllJob() {
        List<Job> jobs = jobRepository.findAll();
        return jobs;
    }

    public JobDto getJobById(Long id) {
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


    public Job createJob(JobRequest jobRequest) {
        try {
            CompanyDto company = restTemplate.getForObject(
                    "http://COMPANY/api/v1/public/" + jobRequest.getCompanyId(),
                    CompanyDto.class
            );

            if (company == null) {
                throw new ResourceNotFoundException("Company not found with id: " + jobRequest.getCompanyId());
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Company not found with id: " + jobRequest.getCompanyId());
        }

        Job job = Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .minSalary(jobRequest.getMinSalary())
                .location(jobRequest.getLocation())
                .companyId(jobRequest.getCompanyId())
                .build();

        return jobRepository.save(job);


    }
//
//    public void deleteJob(Long id) {
//        if (!jobRepository.existsById(id)) {
//            throw new RuntimeException("Job with id " + id + " not found");
//        }
//        jobRepository.deleteById(id);
//
//    }
}
