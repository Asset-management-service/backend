package com.backend.moamoa.domain.asset.salary.service;

import com.backend.moamoa.domain.asset.salary.dto.SalaryRequest;
import com.backend.moamoa.domain.asset.salary.dto.SalaryResponse;
import com.backend.moamoa.domain.asset.salary.entity.FixedCost;
import com.backend.moamoa.domain.asset.salary.entity.Salary;
import com.backend.moamoa.domain.asset.salary.entity.VariableCost;
import com.backend.moamoa.domain.asset.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryService {

    private final SalaryRepository salaryRepository;

    @Transactional
    public SalaryResponse addSalary(SalaryRequest salaryRequest) {

        FixedCost fixedCost = FixedCost.createFixedCost(salaryRequest.getFixedCosts().getFixedCostCategories(),
                salaryRequest.getFixedCosts().getCost());

        VariableCost variableCost = VariableCost.createVariableCost(salaryRequest.getVariableCosts().getVariableCostCategories(),
                salaryRequest.getVariableCosts().getCost());

        Salary salary = Salary.builder()
                .fixedCost(fixedCost)
                .variableCost(variableCost)
                .setDate(salaryRequest.getDate())
                .totalExpenditure(salaryRequest.getTotalExpenditure())
                .build();

        salaryRepository.save(salary);

        return new SalaryResponse("월급 관리 저장이 완료 되었습니다.");
    }
}
