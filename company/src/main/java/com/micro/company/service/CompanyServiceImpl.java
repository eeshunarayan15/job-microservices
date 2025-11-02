package com.micro.company.service;

import com.micro.company.client.ReviewClient;
import com.micro.company.dto.CompanyDto;
import com.micro.company.dto.ReviewMessage;
import com.micro.company.exception.DublicateResourceException;
import com.micro.company.exception.ResourceNotFoundException;
import com.micro.company.model.Company;
import com.micro.company.repository.CompanyRepository;
import com.micro.company.respone.Apiresponse;
import com.micro.company.util.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private  final ReviewClient reviewClient;
    private final CacheService cacheService;

    @Override
    @Cacheable("companies_all")
    public List<CompanyDto> getAllCompany() {
        List<Company> all = companyRepository.findAll();
        List<CompanyDto> list = all.stream().map(company ->
                new CompanyDto(
                        company.getId(),
                        company.getName(),
                        company.getDescription(),
                        company.getAveragerating()
                )).toList();
        return list;
    }

    @Override
    public Company createCompany(CompanyDto companyDto) {
        log.info("Attempting to create company with name: {}", companyDto.getName());

        // Check if company with same name already exists
        if (companyRepository.existsByName(companyDto.getName())) {
            log.warn("Company creation failed - duplicate name: {}", companyDto.getName());
            throw new DublicateResourceException(
                    "Company with name '" + companyDto.getName() + "' already exists"
            );
        }

        // Build the company entity
        Company company = Company.builder()
                .name(companyDto.getName().trim())
                .description(companyDto.getDescription().trim())
                .averagerating(0.0) // Explicitly set default rating
                .build();

        // Save to database
        Company savedCompany = companyRepository.save(company);

        log.info("Company created successfully with ID: {} and name: {}",
                savedCompany.getId(), savedCompany.getName());
        // ✅ Clear cached list after a new company is added
        cacheService.clearAllCompaniesCache();
        return savedCompany;
    }

    @Override
    @Cacheable(value = "companies", key = "#id")
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Company does not exist"));

    }

    @Override
    public Company updateCompany(Long id, CompanyDto companyDto) {
        return null;
    }

    @Override
    public void deleteCompany(Long id) {

    }

    @Override
    public void updateCompanyRating(ReviewMessage reviewMessage) {

        Company company = companyRepository.findById(reviewMessage.getCompanyId()).orElseThrow(() -> new ResourceNotFoundException("Company does not exist : " + reviewMessage.getCompanyId()));
        Apiresponse<Double> averageRating = reviewClient.getAverageRating(reviewMessage.getCompanyId());
        Double rating = averageRating.getData();
        company.setAveragerating(rating);
        companyRepository.save(company);
        cacheService.clearAllCompaniesCache();
        log.info("Updated rating for company {} and evicted caches", company.getName());
    }
}
