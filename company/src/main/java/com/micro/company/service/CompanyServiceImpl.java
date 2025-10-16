package com.micro.company.service;

import com.micro.company.dto.CompanyDto;
import com.micro.company.exception.ResourceNotFoundException;
import com.micro.company.model.Company;
import com.micro.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    @Override
    public List<CompanyDto> getAllCompany() {
        List<Company> all = companyRepository.findAll();
        List<CompanyDto> list = all.stream().map(company ->
                new CompanyDto(
                        company.getId(),
                        company.getName(),
                        company.getDescription()
                )).toList();
return  list;
    }

    @Override
    public Company createCompany(CompanyDto companyDto) {
        Company company = Company.builder()
                .name(companyDto.getName())
                .description(companyDto.getDescription())
                .build();
        Company save = companyRepository.save(company);

        log.info("Company {} is saved", company.getId());

        return company;
    }

    @Override
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
}
