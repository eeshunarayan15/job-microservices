package com.micro.company.repository;


import com.micro.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    List<Company> getCompaniesById(long id);


//    boolean getCompanyByName(String name);
//
//    boolean existsByName(String name);
}
