package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.dto.request.BudgetRequest;
import com.backend.moamoa.domain.asset.dto.request.CreateRevenueExpenditureRequest;
import com.backend.moamoa.domain.asset.dto.request.ExpenditureRequest;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureSumResponse;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.Budget;
import com.backend.moamoa.domain.asset.entity.ExpenditureRatio;
import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.asset.repository.BudgetRepository;
import com.backend.moamoa.domain.asset.repository.ExpenditureRatioRepository;
import com.backend.moamoa.domain.asset.repository.RevenueExpenditureRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private static final String TYPE_REVENUE = "REVENUE";
    private static final String TYPE_EXPENDITURE = "EXPENDITURE";

    private final UserRepository userRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenditureRatioRepository expenditureRatioRepository;
    private final RevenueExpenditureRepository revenueExpenditureRepository;

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
            throw new CustomException(ErrorCode.BAD_REQUEST_EXPENDITURE);
        }
        return expenditureRatioRepository.save(ExpenditureRatio.createExpenditureRatio(request.getFixed(), request.getVariable(), user)).getId();
    }

    public List<String> getCategories(String categoryType) {
        User user = userRepository.findById(1L).get();

        return assetCategoryRepository.findByAssetCategoryTypeAndUserId(categoryType, user.getId());
    }

    @Transactional
    public void deleteCategoryName(Long categoryId) {
        User user = userRepository.findById(1L).get();
        AssetCategory category = assetCategoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ASSET_CATEGORY));

        assetCategoryRepository.delete(category);
    }

    @Transactional
    public Long addRevenueExpenditure(CreateRevenueExpenditureRequest request) {
        User user = userRepository.findById(1L).get();

        return revenueExpenditureRepository.save(RevenueExpenditure.builder()
                .revenueExpenditureType(request.getRevenueExpenditureType())
                .content(request.getContent())
                .cost(request.getCost())
                .date(request.getDate())
                .categoryName(request.getCategoryName())
                .paymentMethod(request.getPaymentMethod())
                .user(user)
                .build()).getId();
    }

    public RevenueExpenditureSumResponse findRevenueExpenditureByMonth(String month, Pageable pageable) {
        User user = userRepository.findById(1L).get();

        Page<RevenueExpenditureResponse> revenueExpenditure = revenueExpenditureRepository.findRevenueAndExpenditureByMonth(LocalDate.parse(month + "-01"), pageable, user.getId());

        Budget budget = budgetRepository.findBudgetAmountByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BUDGET));

        List<RevenueExpenditure> revenueExpenditureList = revenueExpenditureRepository.findRevenueExpenditure(LocalDate.parse(month + "-01"), user.getId());

        int revenue = getRevenueExpenditure(revenueExpenditureList, TYPE_REVENUE);
        int expenditure = getRevenueExpenditure(revenueExpenditureList, TYPE_EXPENDITURE);
        int remainingBudget = budget.getBudgetAmount() - expenditure;

        return new RevenueExpenditureSumResponse(revenue, expenditure, remainingBudget, revenueExpenditure);

    }

    private int getRevenueExpenditure(List<RevenueExpenditure> revenueExpenditureList, String type) {
        return revenueExpenditureList
                    .stream()
                    .filter(r -> r.getRevenueExpenditureType().toString().equals(type))
                    .mapToInt(RevenueExpenditure::getCost)
                    .sum();
    }
}
