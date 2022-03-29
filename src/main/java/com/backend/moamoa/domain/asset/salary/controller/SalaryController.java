package com.backend.moamoa.domain.asset.salary.controller;

import com.backend.moamoa.domain.asset.salary.dto.SalaryRequest;
import com.backend.moamoa.domain.asset.salary.dto.SalaryResponse;
import com.backend.moamoa.domain.asset.salary.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping
    public SalaryResponse addSalary(@RequestBody SalaryRequest salaryRequest){
        return salaryService.addSalary(salaryRequest);
    }
}
