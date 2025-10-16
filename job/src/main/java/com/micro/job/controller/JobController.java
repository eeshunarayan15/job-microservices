package com.micro.job.controller;


import com.micro.job.dto.JobDto;
import com.micro.job.dto.JobRequest;
import com.micro.job.model.Job;
import com.micro.job.response.Apiresponse;
import com.micro.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping("/jobs")
    public ResponseEntity<Apiresponse<Object>> getAllJob() {
        try {
            List<Job> allJob = jobService.getAllJob();
            Apiresponse<Object> apiresponse = new Apiresponse<>("SUCCESS", "JOB FOUND SUCCESSFULLY", allJob);
            return status(HttpStatus.OK).body(apiresponse);
        } catch (Exception e) {

            Apiresponse<Object> apiresponse = new Apiresponse<>("ERROR", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiresponse);
        }

    }
//
    @PostMapping("/jobs")
    public ResponseEntity<Apiresponse<Job>> createJob(@RequestBody JobRequest jobRequest) {

            Job job = jobService.createJob(jobRequest);


        Apiresponse<Job> jobApiresponse = new Apiresponse<>("ERROR", "Job Created SucessFully", job);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jobApiresponse);

    }
//
        @GetMapping("/job/{id}")
    public ResponseEntity<Apiresponse<Object>> getJobById(@PathVariable Long id) {
            JobDto jobById = jobService.getJobById(id);
            return  ResponseEntity.status(HttpStatus.OK).body(new Apiresponse<>("Sucess","Found SucessFully",jobById));
        }
//    @DeleteMapping("/jobs/{id}")
//    public ResponseEntity<Apiresponse<Object>> deleteJob(@PathVariable Long id) {
//        try {
//            jobService.deleteJob(id);
//            Apiresponse<Object> apiresponse = new Apiresponse<>("SUCCESS", "JOB DELETED SUCCESSFULLY", null);
//            return ResponseEntity.ok(apiresponse);
//        } catch (RuntimeException e) {
//            Apiresponse<Object> apiresponse = new Apiresponse<>("ERROR", e.getMessage(), null);
//            return status(HttpStatus.NOT_FOUND).body(apiresponse);
//        }
//    }
}
