package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.dto.request.BudgetRequest;
import com.backend.moamoa.domain.asset.dto.request.ExpenditureRequest;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.backend.moamoa.domain.asset.entity.Budget;
import com.backend.moamoa.domain.asset.entity.ExpenditureRatio;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.asset.repository.BudgetRepository;
import com.backend.moamoa.domain.asset.repository.ExpenditureRatioRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private UserUtil userUtil;

    @Mock
    private AssetCategoryRepository assetCategoryRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenditureRatioRepository expenditureRatioRepository;

    @InjectMocks
    private AssetService assetService;


    @Test
    @DisplayName("가계부 설정 카테고리 생성 - 성공")
    void addCategory() {
        //given
        User user = UserBuilder.dummyUser();
        AssetCategory assetCategory = AssetCategory.builder().id(1L).assetCategoryType(AssetCategoryType.FIXED).categoryName("월급").user(user).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.save(any(AssetCategory.class))).willReturn(assetCategory);

        //when
        Long categoryId = assetService.addCategory(new AssetCategoryRequest(AssetCategoryType.FIXED, "월급"));

        //then
        assertThat(categoryId).isEqualTo(assetCategory.getId());
        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).save(any(AssetCategory.class));
    }

    @Test
    @DisplayName("가계부 예산 설정 - 성공")
    void addBudget() {
        //given
        User user = UserBuilder.dummyUser();
        Budget budget = Budget.builder().id(1L).budgetAmount(1000000).user(user).build();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(budgetRepository.findBudgetAmountByUser(any(User.class))).willReturn(Optional.of(budget));

        //when
        Long budgetId = assetService.addBudget(new BudgetRequest(100000));

        //then
        assertThat(budgetId).isEqualTo(budget.getId());
        verify(userUtil).findCurrentUser();
        verify(budgetRepository).findBudgetAmountByUser(any(User.class));
    }

    @Test
    @DisplayName("가계부 예산 설정 - 예산 금액을 찾지 못한 경우 새로 만들어서 반환")
    void addBudgetIsEmpty() {
        //given
        User user = UserBuilder.dummyUser();
        Budget budget = Budget.builder().id(1L).budgetAmount(1000000).user(user).build();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(budgetRepository.findBudgetAmountByUser(any(User.class)))
                .willReturn(Optional.empty());
        given(budgetRepository.save(any(Budget.class))).willReturn(budget);

        //when
        Long budgetId = assetService.addBudget(new BudgetRequest(100000));

        //then
        assertThat(budgetId).isEqualTo(budget.getId());

        verify(userUtil).findCurrentUser();
        verify(budgetRepository).findBudgetAmountByUser(any(User.class));
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    @DisplayName("가계부 지출 비율 설정 - 성공")
    void addExpenditure() {
        //given
        User user = UserBuilder.dummyUser();
        ExpenditureRatio expenditureRatio = ExpenditureRatio.builder().id(1L).fixed(40).variable(60).user(user).build();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(expenditureRatioRepository.findByUser(any(User.class)))
                .willReturn(Optional.empty());
        given(expenditureRatioRepository.save(any(ExpenditureRatio.class)))
                .willReturn(expenditureRatio);

        //when
        Long expenditureRatioId = assetService.addExpenditure(new ExpenditureRequest(40, 60));

        //then
        assertThat(expenditureRatioId).isEqualTo(expenditureRatio.getId());
        verify(userUtil).findCurrentUser();
        verify(expenditureRatioRepository).findByUser(any(User.class));
        verify(expenditureRatioRepository).save(any(ExpenditureRatio.class));
    }

    @Test
    @DisplayName("가계부 지출 비율 설정 - 합이 100% 가 아닌 경우 실패")
    void addExpenditureFail() {
        //given
        int fixed = 40;
        int variable = 50;
        ExpenditureRequest expenditureRequest = new ExpenditureRequest(fixed, variable);

        //when
        //then
        assertThatThrownBy(() -> assetService.addExpenditure(expenditureRequest))
                .isInstanceOf(CustomException.class);
    }


}