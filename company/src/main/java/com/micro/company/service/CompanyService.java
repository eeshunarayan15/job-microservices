package com.micro.company.service;
import com.micro.company.dto.CompanyDto;
import com.micro.company.model.Company;
import com.micro.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CompanyService {
    public List<CompanyDto> getAllCompany();
    public Company createCompany(CompanyDto companyDto);
    public Company getCompanyById(Long id);
    public Company updateCompany(Long id, CompanyDto companyDto);
    public void deleteCompany(Long id);

}
