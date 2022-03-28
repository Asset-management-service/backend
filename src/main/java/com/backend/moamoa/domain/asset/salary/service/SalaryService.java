package com.backend.moamoa.domain.asset.salary.service;

import com.backend.moamoa.domain.asset.salary.dto.FixedCostDto;
import com.backend.moamoa.domain.asset.salary.dto.SalaryRequest;
import com.backend.moamoa.domain.asset.salary.dto.SalaryResponse;
import com.backend.moamoa.domain.asset.salary.entity.Category;
import com.backend.moamoa.domain.asset.salary.entity.Salary;
import com.backend.moamoa.domain.asset.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryService {

    private final SalaryRepository salaryRepository;

    @Transactional
    public SalaryResponse addSalary(SalaryRequest salaryRequest) {
//        Salary.builder()
//                .category()
//                .totalExpenditure(salaryRequest.getExpenditure())
//                .costType(salaryRequest.)
//                .build();


        Map<String, Integer> map = new HashMap<>();

        salaryRequest.getFixed().stream()
                .forEach(fixed -> fixed.getCategory()
                        .stream()
//                        .map(category ->  map.put())
                        .collect(Collectors.toList()));



        return new SalaryResponse("월급 관리 저장이 완료 되었습니다.");
    }
}
