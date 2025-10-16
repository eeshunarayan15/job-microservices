package com.micro.company.controller;

import com.micro.company.dto.CompanyDto;
import com.micro.company.model.Company;
import com.micro.company.respone.Apiresponse;
import com.micro.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
@Slf4j
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/all")
    public ResponseEntity<Apiresponse<List<CompanyDto>>> getAllCompany() {
        List<CompanyDto> allCompany = companyService.getAllCompany();
        Apiresponse<List<CompanyDto>> listApiresponse = new Apiresponse<>("Sucess", "Company Found", allCompany);
        return  ResponseEntity.status(HttpStatus.OK).body(listApiresponse);
    }
    @PostMapping("/company")
    public ResponseEntity<Apiresponse<Company>> createCompnay(@Valid  @RequestBody CompanyDto companyDto) {

           log.info("Create Company Called Controller");
           Company company = companyService.createCompany(companyDto);
           Apiresponse<Company> apiresponse = new Apiresponse<>("Success", "Company created successfully", company);
           return   ResponseEntity.status(HttpStatus.CREATED).body(apiresponse);

    }
    @GetMapping("/{id}")
    public ResponseEntity<Apiresponse<Company>> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(new Apiresponse<>("Success", "Company found", company));
    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Apiresponse<Company>> updateCompany(
//            @PathVariable Long id,
//            @Valid @RequestBody CompanyDto companyDto) {
//        Company company = companyService.updateCompany(id, companyDto);
//        return ResponseEntity.ok(new Apiresponse<>("Success", "Company updated successfully", company));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Apiresponse<Void>> deleteCompany(@PathVariable Long id) {
//        companyService.deleteCompany(id);
//        return ResponseEntity.ok(new Apiresponse<>("Success", "Company deleted successfully", null));
//    }

}
