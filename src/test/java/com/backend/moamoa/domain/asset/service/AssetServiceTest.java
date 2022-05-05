package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.dto.request.BudgetRequest;
import com.backend.moamoa.domain.asset.dto.request.CreateRevenueExpenditureRequest;
import com.backend.moamoa.domain.asset.dto.request.ExpenditureRequest;
import com.backend.moamoa.domain.asset.dto.response.AssetCategoryDtoResponse;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureSumResponse;
import com.backend.moamoa.domain.asset.entity.*;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.asset.repository.BudgetRepository;
import com.backend.moamoa.domain.asset.repository.ExpenditureRatioRepository;
import com.backend.moamoa.domain.asset.repository.RevenueExpenditureRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    @Mock
    private RevenueExpenditureRepository revenueExpenditureRepository;

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

    @Test
    @DisplayName("가계부 설정 카테고리 조회 - 성공")
    void getCategories() {
        //given
        User user = UserBuilder.dummyUser();

        List<AssetCategory> assetCategories = new ArrayList<>();
        assetCategories.add(dummyAssetCategory(1L, "월급", AssetCategoryType.FIXED, user));
        assetCategories.add(dummyAssetCategory(2L, "월세", AssetCategoryType.FIXED, user));
        assetCategories.add(dummyAssetCategory(3L, "통신비", AssetCategoryType.FIXED, user));

        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.findByAssetCategoryTypeAndUserId(anyString(), anyLong()))
                .willReturn(assetCategories);

        //when
        AssetCategoryDtoResponse response = assetService.getCategories("fixed");

        //then
        assertThat(response.getCategories()).hasSize(3);
        assertThat(response.getCategories()).extracting("categoryId")
                .containsExactly(1L, 2L, 3L);
        assertThat(response.getCategories()).extracting("categoryName")
                .containsExactly("월급", "월세", "통신비");

        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).findByAssetCategoryTypeAndUserId(anyString(), anyLong());
    }

    @Test
    @DisplayName("가계부 설정 카테고리 삭제 - 성공")
    void deleteCategoryName() {
        //given
        Long categoryId = 1L;
        User user = UserBuilder.dummyUser();
        AssetCategory assetCategory = dummyAssetCategory(1L, "월급", AssetCategoryType.FIXED, user);

        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(assetCategory));

        //when
        assetService.deleteCategoryName(categoryId);

        //then
        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("가계부 설정 카테고리 삭제 - 카테고리 OR 회원 PK를 찾지 못한 경우 실패")
    void deleteCategoryNameFail() {
        //given
        Long categoryId = 1L;
        User user = UserBuilder.dummyUser();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.findByIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> assetService.deleteCategoryName(categoryId))
                .isInstanceOf(CustomException.class);

        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("수익 지출 설정 - 성공")
    void addRevenueExpenditure() {
        //given
        User user = UserBuilder.dummyUser();

        CreateRevenueExpenditureRequest request = new CreateRevenueExpenditureRequest(
                RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED, LocalDate.parse("2022-05-05"),
                "월급", null, 3000000, "월급날!!");

        RevenueExpenditure revenueExpenditure = dummyRevenueExpenditure(1L, AssetCategoryType.FIXED,
                RevenueExpenditureType.REVENUE, user, "월급날!!", "월급", 10000000, LocalDate.parse("2022-05-05"));

        given(userUtil.findCurrentUser()).willReturn(user);
        given(revenueExpenditureRepository.save(any(RevenueExpenditure.class))).willReturn(revenueExpenditure);

        //when
        Long revenueExpenditureId = assetService.addRevenueExpenditure(request);

        //then
        assertThat(revenueExpenditureId).isEqualTo(revenueExpenditure.getId());

        verify(userUtil).findCurrentUser();
        verify(revenueExpenditureRepository).save(any(RevenueExpenditure.class));
    }


    @Test
    @DisplayName("가계부 수익 지출 조회 - 성공")
    void findRevenueExpenditureByMonth() {
        //given
        User user = UserBuilder.dummyUser();
        List<RevenueExpenditureResponse> revenueExpenditureResponses = new ArrayList<>();
        Budget budget = Budget.builder().id(1L).user(user).budgetAmount(1000000).build();

        List<RevenueExpenditure> revenueExpenditures = List.of(dummyRevenueExpenditure(1L, AssetCategoryType.FIXED,
                        RevenueExpenditureType.REVENUE, user, "월급날!!", "월급", 10000000, LocalDate.parse("2022-05-05")),
                dummyRevenueExpenditure(2L, AssetCategoryType.VARIABLE,
                        RevenueExpenditureType.EXPENDITURE, user, "식비!!", "식비", 10000, LocalDate.parse("2022-05-05")),
                dummyRevenueExpenditure(3L, AssetCategoryType.VARIABLE,
                        RevenueExpenditureType.REVENUE, user, "적금이자", "적금", 10000, LocalDate.parse("2022-05-05")),
                dummyRevenueExpenditure(4L, AssetCategoryType.VARIABLE,
                        RevenueExpenditureType.EXPENDITURE, user, "교통비!!", "교통비", 30000, LocalDate.parse("2022-05-05")));

        int revenue = getRevenueExpenditure(revenueExpenditures, "REVENUE");
        int expenditure = getRevenueExpenditure(revenueExpenditures, "EXPENDITURE");
        int remainingBudget = budget.getBudgetAmount() - expenditure;

        given(userUtil.findCurrentUser()).willReturn(user);
        given(revenueExpenditureRepository.findRevenueAndExpenditureByMonth(any(LocalDate.class), any(Pageable.class), anyLong()))
                .will(invocation -> {
                    Pageable pageable = invocation.getArgument(1);
                    revenueExpenditureResponses.add(new RevenueExpenditureResponse(1L, RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED,
                            LocalDate.parse("2022-05-05"), "월급", "월급날!", null, 3000000));
                    revenueExpenditureResponses.add(new RevenueExpenditureResponse(2L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.FIXED,
                            LocalDate.parse("2022-05-05"), "통신비", "통신비!!", "자동 이체", 100000));
                    revenueExpenditureResponses.add(new RevenueExpenditureResponse(3L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.VARIABLE,
                            LocalDate.parse("2022-05-05"), "식비", "치킨", "신용 카드", 30000));
                    return new PageImpl<>(revenueExpenditureResponses, pageable, revenueExpenditureResponses.size());
                });
        given(budgetRepository.findBudgetAmountByUser(any(User.class))).willReturn(Optional.of(budget));
        given(revenueExpenditureRepository.findRevenueExpenditure(any(LocalDate.class), anyLong()))
                .willReturn(revenueExpenditures);

        //when
        RevenueExpenditureSumResponse response = assetService.findRevenueExpenditureByMonth("2022-05", Pageable.ofSize(1));

        //then
        assertThat(response.getTotalRevenue()).isEqualTo(revenue);
        assertThat(response.getTotalExpenditure()).isEqualTo(expenditure);
        assertThat(response.getRemainingBudget()).isEqualTo(remainingBudget);
        assertThat(response.getRevenueExpenditureResponses().getTotalElements()).isEqualTo(3);
        assertThat(response.getRevenueExpenditureResponses()).extracting("categoryName")
                .containsExactly("월급", "통신비", "식비");
        assertThat(response.getRevenueExpenditureResponses()).extracting("revenueExpenditureId")
                .containsExactly(1L, 2L, 3L);

        verify(userUtil).findCurrentUser();
        verify(revenueExpenditureRepository).findRevenueAndExpenditureByMonth(any(LocalDate.class), any(Pageable.class), anyLong());
        verify(budgetRepository).findBudgetAmountByUser(any(User.class));
        verify(revenueExpenditureRepository).findRevenueExpenditure(any(LocalDate.class), anyLong());
    }

    /**
     * 수익 지출 타입을 받아서 합을 반환해주는 메소드
     */
    private int getRevenueExpenditure(List<RevenueExpenditure> revenueExpenditureList, String type) {
        return revenueExpenditureList
                .stream()
                .filter(r -> r.getRevenueExpenditureType().toString().equals(type))
                .mapToInt(RevenueExpenditure::getCost)
                .sum();
    }

    /**
     * 더미 데이터 - 카테고리
     */
    private AssetCategory dummyAssetCategory(Long id, String categoryName, AssetCategoryType assetCategoryType, User user) {
        return AssetCategory.builder()
                .id(id)
                .categoryName(categoryName)
                .assetCategoryType(assetCategoryType)
                .user(user)
                .build();
    }

    /**
     * 더미 데이터 - 수익 지출
     */
    private RevenueExpenditure dummyRevenueExpenditure(Long id, AssetCategoryType assetCategoryType, RevenueExpenditureType revenueExpenditureType,  User user,
                                                       String content, String categoryName, int cost, LocalDate date) {
        return RevenueExpenditure.builder()
                .id(id)
                .assetCategoryType(assetCategoryType)
                .revenueExpenditureType(revenueExpenditureType)
                .user(user)
                .content(content)
                .categoryName(categoryName)
                .cost(cost)
                .date(date)
                .build();
    }



}