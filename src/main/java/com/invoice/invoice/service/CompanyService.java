package com.invoice.invoice.service;

import com.invoice.invoice.entity.Company;
import com.invoice.invoice.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
    
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }
    
    @Transactional
    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }
    
    @Transactional
    public Company updateCompany(Long id, Company company) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        existing.setName(company.getName());
        existing.setEmail(company.getEmail());
        existing.setPhone(company.getPhone());
        existing.setAddress(company.getAddress());
        existing.setCity(company.getCity());
        existing.setState(company.getState());
        existing.setZipCode(company.getZipCode());
        existing.setCountry(company.getCountry());
        existing.setTaxNumber(company.getTaxNumber());
        existing.setLogoUrl(company.getLogoUrl());
        existing.setGstin(company.getGstin());
        existing.setPlaceOfSupply(company.getPlaceOfSupply());
        return companyRepository.save(existing);
    }
    
    @Transactional
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}
