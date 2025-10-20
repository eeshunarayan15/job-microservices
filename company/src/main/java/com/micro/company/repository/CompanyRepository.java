package com.micro.company.repository;


import com.micro.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    List<Company> getCompaniesById(long id);

    // Add these methods
    boolean existsByName(String name);
    Optional<Company> findByName(String name);
}
