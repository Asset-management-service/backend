package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.asset.dto.request.*;
import com.backend.moamoa.domain.asset.dto.response.*;
import com.backend.moamoa.domain.asset.entity.*;
import com.backend.moamoa.domain.asset.repository.*;
import com.backend.moamoa.domain.post.entity.PostImage;
import com.backend.moamoa.domain.post.repository.post.PostImageRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.s3.S3Uploader;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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

    @Mock
    private AssetGoalRepository assetGoalRepository;

    @Mock
    private MoneyLogRepository moneyLogRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private PostImageRepository postImageRepository;

    @InjectMocks
    private AssetService assetService;


    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ??????")
    void addCategory() {
        //given
        User user = UserBuilder.dummyUser();
        AssetCategory assetCategory = AssetCategory.builder().id(1L).assetCategoryType(AssetCategoryType.FIXED).categoryName("??????").user(user).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.save(any(AssetCategory.class))).willReturn(assetCategory);

        //when
        Long categoryId = assetService.addCategory(new AssetCategoryRequest(AssetCategoryType.FIXED, "??????"));

        //then
        assertThat(categoryId).isEqualTo(assetCategory.getId());
        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).save(any(AssetCategory.class));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
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
    @DisplayName("????????? ?????? ?????? - ?????? ????????? ?????? ?????? ?????? ?????? ???????????? ??????")
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
    @DisplayName("????????? ?????? ?????? ?????? - ??????")
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
        verify(userUtil, times(1)).findCurrentUser();
        verify(expenditureRatioRepository, times(1)).findByUser(any(User.class));
        verify(expenditureRatioRepository, times(1)).save(any(ExpenditureRatio.class));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? - ?????? ?????? ?????? ????????? ???????????? ??????")
    void addExpenditureUpdate() {
        //given
        User user = UserBuilder.dummyUser();
        ExpenditureRatio expenditureRatio = ExpenditureRatio.builder().id(1L).fixed(40).variable(60).user(user).build();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(expenditureRatioRepository.findByUser(any(User.class)))
                .willReturn(Optional.of(expenditureRatio));

        //when
        Long expenditureRatioId = assetService.addExpenditure(new ExpenditureRequest(50, 50));

        //then
        assertThat(expenditureRatioId).isEqualTo(expenditureRatio.getId());
        assertThat(expenditureRatio.getFixed()).isEqualTo(50);
        assertThat(expenditureRatio.getVariable()).isEqualTo(50);
        verify(userUtil, times(1)).findCurrentUser();
        verify(expenditureRatioRepository, times(1)).findByUser(any(User.class));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? - ?????? 100% ??? ?????? ?????? ??????")
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
    @DisplayName("????????? ?????? ???????????? ?????? - ??????")
    void getCategories() {
        //given
        User user = UserBuilder.dummyUser();

        List<AssetCategory> assetCategories = new ArrayList<>();
        assetCategories.add(dummyAssetCategory(1L, "??????", AssetCategoryType.FIXED, user));
        assetCategories.add(dummyAssetCategory(2L, "??????", AssetCategoryType.FIXED, user));
        assetCategories.add(dummyAssetCategory(3L, "?????????", AssetCategoryType.FIXED, user));

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
                .containsExactly("??????", "??????", "?????????");

        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).findByAssetCategoryTypeAndUserId(anyString(), anyLong());
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ??????")
    void updateCategory() {
        //given
        User user = UserBuilder.dummyUser();
        AssetCategory assetCategory = AssetCategory.builder().id(1L).assetCategoryType(AssetCategoryType.VARIABLE).categoryName("??????").user(user).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(assetCategory));

        //when
        assetService.updateCategory(new UpdateAssetCategoryRequest(1L, AssetCategoryType.FIXED, "?????????"));

        //then
        assertThat(assetCategory.getAssetCategoryType()).isEqualTo(AssetCategoryType.FIXED);
        assertThat(assetCategory.getCategoryName()).isEqualTo("?????????");

        verify(userUtil, times(1)).findCurrentUser();
        verify(assetCategoryRepository, times(1)).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ??????")
    void deleteCategoryName() {
        //given
        Long categoryId = 1L;
        User user = UserBuilder.dummyUser();
        AssetCategory assetCategory = dummyAssetCategory(1L, "??????", AssetCategoryType.FIXED, user);

        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(assetCategory));

        //when
        assetService.deleteCategoryName(categoryId);

        //then
        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ???????????? OR ?????? PK??? ?????? ?????? ?????? ??????")
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
    @DisplayName("?????? ?????? ?????? - ??????")
    void addRevenueExpenditure() {
        //given
        User user = UserBuilder.dummyUser();

        CreateRevenueExpenditureRequest request = new CreateRevenueExpenditureRequest(
                RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED, LocalDate.parse("2022-05-05"),
                "??????", null, 3000000, "?????????!!");

        RevenueExpenditure revenueExpenditure = dummyRevenueExpenditure(1L, AssetCategoryType.FIXED,
                RevenueExpenditureType.REVENUE, user, "?????????!!", "??????", 10000000, LocalDate.parse("2022-05-05"));

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
    @DisplayName("????????? ?????? ?????? ?????? - ??????")
    void findRevenueExpenditureByMonth() {
        //given
        User user = UserBuilder.dummyUser();
        List<RevenueExpenditureResponse> revenueExpenditureResponses = new ArrayList<>();
        Budget budget = Budget.builder().id(1L).user(user).budgetAmount(1000000).build();

        List<RevenueExpenditure> revenueExpenditures = getDummyRevenueExpenditures(user);

        int revenue = getRevenueExpenditure(revenueExpenditures, "REVENUE");
        int expenditure = getRevenueExpenditure(revenueExpenditures, "EXPENDITURE");
        int remainingBudget = budget.getBudgetAmount() - expenditure;

        given(userUtil.findCurrentUser()).willReturn(user);
        given(revenueExpenditureRepository.findRevenueAndExpenditureByMonth(any(LocalDate.class), any(Pageable.class), anyLong()))
                .will(invocation -> {
                    Pageable pageable = invocation.getArgument(1);
                    dummyRevenueExpenditureResponses(revenueExpenditureResponses);
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
                .containsExactly("??????", "?????????", "??????");
        assertThat(response.getRevenueExpenditureResponses()).extracting("revenueExpenditureId")
                .containsExactly(1L, 2L, 3L);

        verify(userUtil).findCurrentUser();
        verify(revenueExpenditureRepository).findRevenueAndExpenditureByMonth(any(LocalDate.class), any(Pageable.class), anyLong());
        verify(budgetRepository).findBudgetAmountByUser(any(User.class));
        verify(revenueExpenditureRepository).findRevenueExpenditure(any(LocalDate.class), anyLong());
    }



    @Test
    @DisplayName("????????? ?????? ?????? ?????? - ?????? ????????? ???????????? ?????? ?????? ??????")
    void findRevenueExpenditureByMonthFail() {
        //given
        User user = UserBuilder.dummyUser();
        List<RevenueExpenditureResponse> revenueExpenditureResponses = new ArrayList<>();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(revenueExpenditureRepository.findRevenueAndExpenditureByMonth(any(LocalDate.class), any(Pageable.class), anyLong()))
                .will(invocation -> {
                    Pageable pageable = invocation.getArgument(1);
                    dummyRevenueExpenditureResponses(revenueExpenditureResponses);
                    return new PageImpl<>(revenueExpenditureResponses, pageable, revenueExpenditureResponses.size());
                });

        //when
        //then
        assertThatThrownBy(() -> assetService.findRevenueExpenditureByMonth("2022-05", PageRequest.of(0, 3)))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(revenueExpenditureRepository, times(1)).findRevenueAndExpenditureByMonth(any(LocalDate.class), any(Pageable.class), anyLong());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ?????? ?????? ????????? ??? ??????????????? ?????? ???????????? ?????? ??????")
    void addAssetGoal() {
        //given
        User user = UserBuilder.dummyUser();
        AssetGoal assetGoal = AssetGoal.builder().id(1L).user(user).date(LocalDate.parse("2022-05-01")).content("??? 100 ?????? ????????????").build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetGoalRepository.findByUserAndDate(any(User.class), any(LocalDate.class))).willReturn(Optional.empty());
        given(assetGoalRepository.save(any(AssetGoal.class))).willReturn(assetGoal);

        //when
        Long assetGoalId = assetService.addAssetGoal(new CreateAssetGoalRequest(LocalDate.parse("2022-05-01"), "??? 100 ?????? ????????????"));

        //then
        assertThat(assetGoalId).isEqualTo(1L);

        verify(userUtil, times(1)).findCurrentUser();
        verify(assetGoalRepository, times(1)).findByUserAndDate(any(User.class), any(LocalDate.class));
        verify(assetGoalRepository, times(1)).save(any(AssetGoal.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ????????? 2??? ????????? ?????? ??????")
    void createMoneyLog() {
        //given
        String imageUrl1 = "https://s3uploader.Moamoa/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc";
        String imageUrl2 = "https://s3uploader.moamoa/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as";

        User user = UserBuilder.dummyUser();

        MoneyLog moneyLog = MoneyLog.builder().id(1L).user(user).content("???????????? ??????!!").date(LocalDate.parse("2022-05-06")).build();

        PostImage postImage1 = PostImage.builder().imageUrl(imageUrl1).moneyLog(moneyLog).build();
        PostImage postImage2 = PostImage.builder().imageUrl(imageUrl2).moneyLog(moneyLog).build();

        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "????????????1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "????????????2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));

        given(userUtil.findCurrentUser()).willReturn(user);
        given(moneyLogRepository.save(any(MoneyLog.class))).willReturn(moneyLog);
        given(s3Uploader.upload(any(MultipartFile.class), anyString())).willReturn(imageUrl1, imageUrl2);
        given(postImageRepository.save(any(PostImage.class))).willReturn(postImage1, postImage2);

        //when
        CreateMoneyLogResponse response = assetService.createMoneyLog(new CreateMoneyLogRequest(LocalDate.parse("2022-05-06"), "????????? ?????? ?????????!..", imageFiles));

        //then
        assertThat(response.getMoneyLogId()).isEqualTo(1L);
        assertThat(response.getImageUrl()).isEqualTo(List.of(imageUrl1, imageUrl2));

        verify(userUtil, times(1)).findCurrentUser();
        verify(moneyLogRepository, times(1)).save(any(MoneyLog.class));
        verify(s3Uploader, times(2)).upload(any(MultipartFile.class), anyString());
        verify(postImageRepository, times(2)).save(any(PostImage.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ??????")
    void updateRevenueExpenditure() {
        //given
        User user = UserBuilder.dummyUser();
        RevenueExpenditure revenueExpenditure = dummyRevenueExpenditure(1L, AssetCategoryType.FIXED, RevenueExpenditureType.REVENUE, user, "?????????!!", "??????", 10000000, LocalDate.parse("2022-05-05"));

        given(userUtil.findCurrentUser()).willReturn(user);
        given(revenueExpenditureRepository.findByUserAndId(any(User.class), anyLong())).willReturn(Optional.of(revenueExpenditure));

        //when
        assetService.updateRevenueExpenditure(new UpdateRevenueExpenditure(
                1L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.VARIABLE,
                LocalDate.parse("2022-05-07"), "??????", "?????? ??????", 20000, "?????? ??????"));

        //then
        assertThat(revenueExpenditure.getId()).isEqualTo(1L);
        assertThat(revenueExpenditure.getRevenueExpenditureType()).isEqualTo(RevenueExpenditureType.EXPENDITURE);
        assertThat(revenueExpenditure.getPaymentMethod()).isEqualTo("?????? ??????");
        assertThat(revenueExpenditure.getAssetCategoryType()).isEqualTo(AssetCategoryType.VARIABLE);

        verify(userUtil, times(1)).findCurrentUser();
        verify(revenueExpenditureRepository, times(1)).findByUserAndId(any(User.class), anyLong());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - PK??? ?????? ?????? ?????? ??????")
    void updateRevenueExpenditureFail() {
        //given
        User user = UserBuilder.dummyUser();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(revenueExpenditureRepository.findByUserAndId(any(User.class), anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> assetService.updateRevenueExpenditure(new UpdateRevenueExpenditure(
                1L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.VARIABLE,
                LocalDate.parse("2022-05-07"), "??????", "?????? ??????", 20000, "?????? ??????")))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(revenueExpenditureRepository, times(1)).findByUserAndId(any(User.class), anyLong());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ??????")
    void deleteRevenueExpenditure() {
        //given
        User user = UserBuilder.dummyUser();
        RevenueExpenditure revenueExpenditure = dummyRevenueExpenditure(1L, AssetCategoryType.FIXED, RevenueExpenditureType.REVENUE, user, "?????????!!", "??????", 10000000, LocalDate.parse("2022-05-05"));

        given(userUtil.findCurrentUser()).willReturn(user);
        given(revenueExpenditureRepository.findByUserAndId(any(User.class), anyLong())).willReturn(Optional.of(revenueExpenditure));

        //when
        assetService.deleteRevenueExpenditure(1L);

        //then
        verify(userUtil, times(1)).findCurrentUser();
        verify(revenueExpenditureRepository, times(1)).findByUserAndId(any(User.class), anyLong());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    void updateMoneyLog() {
        //given
        String savedImage1 = "https://s3uploader.Moamoa1/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc";
        String savedImage2 = "https://s3uploader.moamoa2/ezzzyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as";
        String newImage1 = "https://s3uploader.moamoa3/exxxyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as";
        String newImage2 = "https://s3uploader.moamoa4/ecccyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as";

        User user = UserBuilder.dummyUser();
        MoneyLog moneyLog = MoneyLog.builder().id(1L).user(user).content("???????????? ??????!!").date(LocalDate.parse("2022-05-06")).build();

        PostImage postImage1 = PostImage.builder().imageUrl(savedImage1).moneyLog(moneyLog).build();
        PostImage postImage2 = PostImage.builder().imageUrl(savedImage2).moneyLog(moneyLog).build();
        PostImage postImage3 = PostImage.builder().imageUrl(newImage1).moneyLog(moneyLog).build();
        PostImage postImage4 = PostImage.builder().imageUrl(newImage2).moneyLog(moneyLog).build();

        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "????????????1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "????????????2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));

        given(userUtil.findCurrentUser()).willReturn(user);
        given(moneyLogRepository.findByUserAndId(any(User.class), anyLong())).willReturn(Optional.of(moneyLog));
        given(postImageRepository.findBySavedMoneyLogImageUrl(anyLong())).willReturn(List.of(postImage1, postImage2, postImage3, postImage4));
        given(s3Uploader.upload(any(MultipartFile.class), anyString())).willReturn(newImage1, newImage2);
        given(postImageRepository.save(any(PostImage.class))).willReturn(postImage3, postImage4);

        //when
        UpdateMoneyLogResponse response = assetService.updateMoneyLog(new UpdateMoneyLogRequest(1L, LocalDate.parse("2022-05-05"), "???????????? ??????!!",
                List.of(savedImage1, savedImage2), imageFiles));

        //then
        assertThat(response.getMoneyLogId()).isEqualTo(1L);
        assertThat(response.getImageUrl()).hasSize(4);
        assertThat(moneyLog.getContent()).isEqualTo("???????????? ??????!!");
        assertThat(moneyLog.getDate()).isEqualTo(LocalDate.parse("2022-05-05"));

        verify(userUtil, times(1)).findCurrentUser();
        verify(moneyLogRepository, times(1)).findByUserAndId(any(User.class), anyLong());
        verify(postImageRepository, times(2)).findBySavedMoneyLogImageUrl(anyLong());
        verify(s3Uploader, times(2)).upload(any(MultipartFile.class), anyString());
        verify(postImageRepository, times(2)).save(any(PostImage.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? PK??? ?????? ?????? ?????? ??????")
    void updateMoneyLogFail() {
        //given
        String savedImage1 = "https://s3uploader.Moamoa1/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc";
        String savedImage2 = "https://s3uploader.moamoa2/ezzzyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as";

        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "????????????1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "????????????2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(moneyLogRepository.findByUserAndId(any(User.class), anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> assetService.updateMoneyLog(new UpdateMoneyLogRequest(1L, LocalDate.parse("2022-05-05"), "???????????? ??????!!",
                List.of(savedImage1, savedImage2), imageFiles))).isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(moneyLogRepository, times(1)).findByUserAndId(any(User.class), anyLong());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? - ??????")
    void getRevenueExpenditure() {
        //given
        List<RevenueExpenditureResponse> revenueExpenditureResponses = new ArrayList<>();
        dummyRevenueExpenditureResponses(revenueExpenditureResponses);

        int revenue = revenueExpenditureExtractor(revenueExpenditureResponses, "REVENUE");
        int expenditure = revenueExpenditureExtractor(revenueExpenditureResponses, "EXPENDITURE");
        int remainingBudget = revenue - expenditure;

        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(revenueExpenditureRepository.findMoneyLogRevenueExpenditure(anyLong(), any(LocalDate.class))).willReturn(revenueExpenditureResponses);

        //when
        MoneyLogRevenueExpenditureResponse response = assetService.getRevenueExpenditure("2022-05-05");

        //then
        assertThat(response.getTotalRevenue()).isEqualTo(revenue);
        assertThat(response.getTotalExpenditure()).isEqualTo(expenditure);
        assertThat(response.getTotalRevenueExpenditure()).isEqualTo(remainingBudget);
        assertThat(response.getRevenueExpenditureResponses().size()).isEqualTo(3);
        assertThat(response.getRevenueExpenditureResponses()).extracting("categoryName")
                .containsExactly("??????", "?????????", "??????");

        verify(userUtil, times(1)).findCurrentUser();
        verify(revenueExpenditureRepository, times(1)).findMoneyLogRevenueExpenditure(anyLong(), any(LocalDate.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    void getMoneyLog() {
        //given
        String savedImage1 = "https://s3uploader.Moamoa1/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc";
        String savedImage2 = "https://s3uploader.moamoa2/ezzzyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as";

        User user = UserBuilder.dummyUser();

        MoneyLog moneyLog = MoneyLog.builder().id(1L).user(user).content("???????????? ??????!!").date(LocalDate.parse("2022-05-06")).build();

        PostImage postImage1 = PostImage.builder().imageUrl(savedImage1).moneyLog(moneyLog).build();
        PostImage postImage2 = PostImage.builder().imageUrl(savedImage2).moneyLog(moneyLog).build();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(moneyLogRepository.findByUserAndDate(any(User.class), any(LocalDate.class))).willReturn(Optional.of(moneyLog));
        given(postImageRepository.findBySavedMoneyLogImageUrl(anyLong())).willReturn(List.of(postImage1, postImage2));

        //when
        MoneyLogResponse response = assetService.getMoneyLog("2022-05-05");

        //then
        assertThat(response.getMoneyLogId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo(moneyLog.getContent());
        assertThat(response.getImageUrl()).hasSize(2);
        assertThat(response.getImageUrl()).isEqualTo(List.of(savedImage1, savedImage2));

        verify(userUtil, times(1)).findCurrentUser();
        verify(moneyLogRepository, times(1)).findByUserAndDate(any(User.class), any(LocalDate.class));
        verify(postImageRepository, times(1)).findBySavedMoneyLogImageUrl(anyLong());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? PK??? ?????? ?????? ?????? ??????")
    void getMoneyLogFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(moneyLogRepository.findByUserAndDate(any(User.class), any(LocalDate.class))).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> assetService.getMoneyLog("2022-05-05"))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(moneyLogRepository, times(1)).findByUserAndDate(any(User.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ??????")
    void getAssetGoal() {
        //given
        User user = UserBuilder.dummyUser();
        AssetGoal assetGoal = AssetGoal.builder().id(1L).user(user).date(LocalDate.parse("2022-05-01")).content("??? ?????? 100?????? ??????").build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetGoalRepository.findByUserAndDate(any(User.class), any(LocalDate.class))).willReturn(Optional.of(assetGoal));

        //when
        AssetGoalResponse response = assetService.getAssetGoal("2022-05-01");

        //then
        assertThat(response.getAssetGoalId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo(assetGoal.getContent());

        verify(userUtil, times(1)).findCurrentUser();
        verify(assetGoalRepository, times(1)).findByUserAndDate(any(User.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - AssetGoal PK??? ?????? ?????? ?????? ??????")
    void getAssetGoalFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(assetGoalRepository.findByUserAndDate(any(User.class), any(LocalDate.class))).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> assetService.getAssetGoal("2022-05-01"))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(assetGoalRepository, times(1)).findByUserAndDate(any(User.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ??????")
    void getBudget() {
        //given
        User user = UserBuilder.dummyUser();
        Budget budget = Budget.builder().id(1L).user(user).budgetAmount(10000000).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(budgetRepository.findBudgetAmountByUser(any(User.class))).willReturn(Optional.of(budget));

        //when
        BudgetResponse response = assetService.getBudget();

        //then
        assertThat(response.getBudgetId()).isEqualTo(budget.getId());
        assertThat(response.getBudgetAmount()).isEqualTo(budget.getBudgetAmount());

        verify(userUtil, times(1)).findCurrentUser();
        verify(budgetRepository, times(1)).findBudgetAmountByUser(any(User.class));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - Budget PK??? ?????? ?????? ?????? ??????")
    void getBudgetFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(budgetRepository.findBudgetAmountByUser(any(User.class))).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> assetService.getBudget())
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(budgetRepository, times(1)).findBudgetAmountByUser(any(User.class));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ????????? ?????? - ??????")
    void getExpenditure() {
        //given
        User user = UserBuilder.dummyUser();
        ExpenditureRatio expenditureRatio = ExpenditureRatio.builder().id(1L).user(user).fixed(40).variable(60).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(expenditureRatioRepository.findByUser(any(User.class))).willReturn(Optional.of(expenditureRatio));

        //when
        ExpenditureResponse response = assetService.getExpenditure();

        //then
        assertThat(response.getExpenditureRatioId()).isEqualTo(expenditureRatio.getId());
        assertThat(response.getFixed()).isEqualTo(expenditureRatio.getFixed());
        assertThat(response.getVariable()).isEqualTo(expenditureRatio.getVariable());

        verify(userUtil, times(1)).findCurrentUser();
        verify(expenditureRatioRepository, times(1)).findByUser(any(User.class));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ????????? ?????? - ExpenditureRatio PK??? ?????? ?????? ?????? ??????")
    void getExpenditureFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(expenditureRatioRepository.findByUser(any(User.class))).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> assetService.getExpenditure())
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(expenditureRatioRepository, times(1)).findByUser(any(User.class));
    }


    /**
     * ?????? ????????? - ?????? ?????? response ??????
     */
    private void dummyRevenueExpenditureResponses(List<RevenueExpenditureResponse> revenueExpenditureResponses) {
        revenueExpenditureResponses.add(new RevenueExpenditureResponse(1L, RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED,
                LocalDate.parse("2022-05-05"), "??????", "?????????!", null, 3000000));
        revenueExpenditureResponses.add(new RevenueExpenditureResponse(2L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.FIXED,
                LocalDate.parse("2022-05-05"), "?????????", "?????????!!", "?????? ??????", 100000));
        revenueExpenditureResponses.add(new RevenueExpenditureResponse(3L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.VARIABLE,
                LocalDate.parse("2022-05-05"), "??????", "??????", "?????? ??????", 30000));
    }

    /**
     * ?????? ?????? ????????? ????????? ?????? ??????????????? ?????????
     */
    private int getRevenueExpenditure(List<RevenueExpenditure> revenueExpenditureList, String type) {
        return revenueExpenditureList
                .stream()
                .filter(r -> r.getRevenueExpenditureType().toString().equals(type))
                .mapToInt(RevenueExpenditure::getCost)
                .sum();
    }

    /**
     * ?????? ????????? - ????????????
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
     * ?????? ????????? - ?????? ??????
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

    /**
     * ????????? ????????? ????????? return ????????? ?????????
     */
    private int revenueExpenditureExtractor(List<RevenueExpenditureResponse> moneyLogRevenueExpenditure, String type) {
        return moneyLogRevenueExpenditure
                .stream()
                .filter(revenueExpenditure -> revenueExpenditure.getRevenueExpenditureType().toString().equals(type))
                .mapToInt(RevenueExpenditureResponse::getCost)
                .sum();
    }

    private List<RevenueExpenditure> getDummyRevenueExpenditures(User user) {
        List<RevenueExpenditure> revenueExpenditures = List.of(dummyRevenueExpenditure(1L, AssetCategoryType.FIXED,
                        RevenueExpenditureType.REVENUE, user, "?????????!!", "??????", 10000000, LocalDate.parse("2022-05-05")),
                dummyRevenueExpenditure(2L, AssetCategoryType.VARIABLE,
                        RevenueExpenditureType.EXPENDITURE, user, "??????!!", "??????", 10000, LocalDate.parse("2022-05-05")),
                dummyRevenueExpenditure(3L, AssetCategoryType.VARIABLE,
                        RevenueExpenditureType.REVENUE, user, "????????????", "??????", 10000, LocalDate.parse("2022-05-05")),
                dummyRevenueExpenditure(4L, AssetCategoryType.VARIABLE,
                        RevenueExpenditureType.EXPENDITURE, user, "?????????!!", "?????????", 30000, LocalDate.parse("2022-05-05")));
        return revenueExpenditures;
    }
}