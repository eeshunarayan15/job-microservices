package com.micro.company.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {

    @CacheEvict(value = "companies_all", allEntries = true)
    public void clearAllCompaniesCache() {
        log.info("Evicting all companies cache...");
    }
}
