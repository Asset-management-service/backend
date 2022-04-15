package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.dto.request.BudgetRequest;
import com.backend.moamoa.domain.asset.dto.request.ExpenditureRequest;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.Budget;
import com.backend.moamoa.domain.asset.entity.ExpenditureRatio;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.asset.repository.BudgetRepository;
import com.backend.moamoa.domain.asset.repository.ExpenditureRatioRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private final UserRepository userRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenditureRatioRepository expenditureRatioRepository;

    @Transactional
    public Long addCategory(AssetCategoryRequest request) {
        User user = userRepository.findById(1L).get();
        return assetCategoryRepository.save(AssetCategory.createCategory(request.getCategoryType(), request.getCategoryName(), user)).getId();
    }

    @Transactional
    public Long addBudget(BudgetRequest request) {
        User user = userRepository.findById(1L).get();
        return budgetRepository.save(Budget.createBudget(request.getBudgetAmount(), user)).getId();
    }

    @Transactional
    public Long addExpenditure(ExpenditureRequest request) {
        User user = userRepository.findById(1L).get();
        if (request.getFixed() + request.getVariable() != 100) {
            throw new IllegalArgumentException();
        }
        return expenditureRatioRepository.save(ExpenditureRatio.createExpenditureRatio(request.getFixed(), request.getVariable(), user)).getId();
    }

    public ResponseEntity getCategories(String categoryName) {
        User user = userRepository.findById(1L).get();
        assetCategoryRepository.findByCategoryNameAndUserId(categoryName, user.getId());
        return ResponseEntity.ok().body(null);
    }
}
