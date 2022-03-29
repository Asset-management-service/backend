package com.backend.moamoa.domain.asset.salary.service;

import com.backend.moamoa.domain.asset.salary.dto.SalaryRequest;
import com.backend.moamoa.domain.asset.salary.dto.SalaryResponse;
import com.backend.moamoa.domain.asset.salary.entity.Category;
import com.backend.moamoa.domain.asset.salary.repository.CategoryRepository;
import com.backend.moamoa.domain.asset.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public SalaryResponse addSalary(SalaryRequest salaryRequest) {


        salaryRequest.getFixed().stream()
                .forEach(fixed -> {
                    List<Category> collect = fixed.getCategory()
                            .entrySet()
                            .stream()
                            .map(category -> Category.createCategory(
                                    category.getKey(),
                                    category.getValue()))
                            .collect(Collectors.toList());
                });

//                    salaryRepository.save(Salary.createSalary(collect, salaryRequest.getExpenditure()

        return new SalaryResponse("월급 관리 저장이 완료 되었습니다.");
    }
}
