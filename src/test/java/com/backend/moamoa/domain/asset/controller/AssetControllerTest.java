package com.backend.moamoa.domain.asset.controller;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.asset.dto.request.*;
import com.backend.moamoa.domain.asset.dto.response.AssetCategoryDtoResponse;
import com.backend.moamoa.domain.asset.dto.response.CreateMoneyLogResponse;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureSumResponse;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.backend.moamoa.domain.asset.entity.RevenueExpenditureType;
import com.backend.moamoa.domain.asset.service.AssetService;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.oauth.filter.JwtFilter;
import com.backend.moamoa.domain.user.oauth.token.JwtProvider;
import com.backend.moamoa.domain.user.service.UserService;
import com.backend.moamoa.global.bean.security.SecurityConfig;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AssetController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class)})
class AssetControllerTest {

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private UserService userService;

    @MockBean
    private AssetService assetService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("가계부 설정 카테고리 조회 테스트 - 성공")
    void getCategorySuccess() throws Exception {
        //given
        User user = UserBuilder.dummyUser();

        List<AssetCategory> assetCategories = new ArrayList<>();
        AssetCategory assetCategory1 = AssetCategory.createCategory(AssetCategoryType.FIXED, "통신비", user);
        AssetCategory assetCategory2 = AssetCategory.createCategory(AssetCategoryType.FIXED, "인터넷비", user);
        AssetCategory assetCategory3 = AssetCategory.createCategory(AssetCategoryType.FIXED, "보험비", user);

        assetCategories.add(assetCategory1);
        assetCategories.add(assetCategory2);
        assetCategories.add(assetCategory3);

        AssetCategoryDtoResponse response = AssetCategoryDtoResponse.of(assetCategories);

        given(assetService.getCategories("fixed")).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(
                get("/assets/category?categoryType=fixed"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").exists())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(assetService).getCategories("fixed");
    }

    @Test
    @DisplayName("한달 예산 금액 설정 - 성공")
    void addBudget() throws Exception {
        //given
        given(assetService.addBudget(any())).willReturn(1L);
        String json = objectMapper.writeValueAsString(new BudgetRequest(1000000));

        //when
        ResultActions result = mockMvc.perform(put("/assets/budget")
                .content(json)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"budgetId\":1")
                ))
                .andExpect(jsonPath("$.budgetId").exists())
                .andDo(print());
        verify(assetService).addBudget(any(BudgetRequest.class));
    }

    @Test
    @DisplayName("가계부 설정 카테고리 생성 - 성공")
    void addCategory() throws Exception {
        //given
        given(assetService.addCategory(any())).willReturn(1L);
        String json = objectMapper.writeValueAsString(new AssetCategoryRequest(AssetCategoryType.REVENUE, "월급"));

        //when
        ResultActions result = mockMvc.perform(post("/assets/category")
                .content(json)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().string(
                        containsString("\"categoryId\":1")))
                .andExpect(jsonPath("$.categoryId").exists())
                .andExpect(jsonPath("$.categoryId").isNotEmpty())
                .andDo(print());

        verify(assetService).addCategory(any(AssetCategoryRequest.class));
    }

    @Test
    @DisplayName("가계부 설정 카테고리 삭제 - 성공")
    void deleteCategoryName() throws Exception {
        //when
        ResultActions result = mockMvc.perform(delete("/assets/category/1"));

        //then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("가계부 설정 카테고리 삭제 - 카테고리 ID를 찾지 못한 경우 실패")
    void deleteCategoryNameNotFountCategory() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_ASSET_CATEGORY))
                .when(assetService).deleteCategoryName(anyLong());

        //when
        ResultActions result = mockMvc.perform(delete("/assets/category/1"));

        //then
        result.andExpect(status().isNotFound())
                .andDo(print());

        verify(assetService).deleteCategoryName(anyLong());
    }

    @Test
    @DisplayName("지출 비율 설정 - 성공")
    void addExpenditure() throws Exception {
        //given
        given(assetService.addExpenditure(any())).willReturn(1L);
        String json = objectMapper.writeValueAsString(new ExpenditureRequest(40, 60));

        //when
        ResultActions result = mockMvc.perform(put("/assets/expenditure")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"expenditureId\":1")))
                .andExpect(jsonPath("$.expenditureId").exists())
                .andExpect(jsonPath("$.expenditureId").hasJsonPath())
                .andExpect(jsonPath("$.expenditureId").isNumber())
                .andDo(print());

        verify(assetService).addExpenditure(any(ExpenditureRequest.class));
    }

    @Test
    @DisplayName("지출 비율 설정 - 합이 100% 가 아닌 경우 실패")
    void addExpenditureBadRequest() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.BAD_REQUEST_EXPENDITURE))
                .when(assetService).addExpenditure(any());

        String json = objectMapper.writeValueAsString(new ExpenditureRequest(40, 50));

        //when
        ResultActions result = mockMvc.perform(put("/assets/expenditure")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json));

        //then
        result.andExpect(status().isBadRequest())
                .andDo(print());

        verify(assetService).addExpenditure(any(ExpenditureRequest.class));
    }

    @Test
    @DisplayName("수익 지출 내역 추가 - 성공")
    void addRevenueExpenditure() throws Exception {
        //given
        given(assetService.addRevenueExpenditure(any())).willReturn(1L);

        String json = objectMapper.writeValueAsString(new CreateRevenueExpenditureRequest(
                        RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED, LocalDate.parse("2022-05-04"),
                        "월급", null, 1000000, "월급날!!"));

        //when
        ResultActions result = mockMvc.perform(post("/assets/revenueExpenditure")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().string(
                        containsString("\"revenueExpenditureId\":1")))
                .andExpect(jsonPath("$.revenueExpenditureId").isNumber())
                .andExpect(jsonPath("$.revenueExpenditureId").hasJsonPath())
                .andExpect(jsonPath("$.revenueExpenditureId").exists())
                .andDo(print());

        verify(assetService).addRevenueExpenditure(any(CreateRevenueExpenditureRequest.class));
    }

    @Test
    @DisplayName("수익 지출 내역 수정 - 성공")
    void updateRevenueExpenditure() throws Exception {
        //given
        UpdateRevenueExpenditure updateRevenueExpenditure = new UpdateRevenueExpenditure(
                1L, RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED, LocalDate.parse("2022-05-04"),
                "월급", null, 1000000, "월급날!!");
        //when
        ResultActions result = mockMvc.perform(patch("/assets/revenueExpenditure")
                .content(objectMapper.writeValueAsString(updateRevenueExpenditure))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNoContent())
                .andDo(print());

        verify(assetService).updateRevenueExpenditure(any(UpdateRevenueExpenditure.class));
    }

    @Test
    @DisplayName("수익 지출 내역 수정 - 수익 지출 내역 PK를 찾지 못한 경우 실패")
    void updateRevenueExpenditureFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_REVENUE_EXPENDITURE))
                .when(assetService).updateRevenueExpenditure(any());

        UpdateRevenueExpenditure updateRevenueExpenditure = new UpdateRevenueExpenditure(
                2L, RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED, LocalDate.parse("2022-05-04"),
                "월급", null, 1000000, "월급날!!");

        //when
        ResultActions result = mockMvc.perform(patch("/assets/revenueExpenditure")
                .content(objectMapper.writeValueAsString(updateRevenueExpenditure))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNotFound())
                .andDo(print());

        verify(assetService).updateRevenueExpenditure(any(UpdateRevenueExpenditure.class));
    }

    @Test
    @DisplayName("수익 지출 내역 삭제 - 성공")
    void deleteRevenueExpenditure() throws Exception {
        //when
        ResultActions result = mockMvc.perform(delete("/assets/revenueExpenditure/1"));

        //then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("수익 지출 내역 삭제 - 수익 지출 내역 PK를 찾지 못한 경우 실패")
    void deleteRevenueExpenditureFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_REVENUE_EXPENDITURE))
                .when(assetService).deleteRevenueExpenditure(anyLong());

        //when
        ResultActions result = mockMvc.perform(delete("/assets/revenueExpenditure/1"));

        //then
        result.andExpect(status().isNotFound())
                .andDo(print());

        verify(assetService).deleteRevenueExpenditure(anyLong());
    }

    @Test
    @DisplayName("수익 지출 내역 조회 - 성공")
    void getRevenueExpenditure() throws Exception {
        //given
        List<RevenueExpenditureResponse> revenueExpenditure = new ArrayList<>();

        RevenueExpenditureResponse revenue1 = new RevenueExpenditureResponse(1L, RevenueExpenditureType.REVENUE, AssetCategoryType.FIXED,
                LocalDate.parse("2022-04-04"), "월급", "월급날!!", null, 2000000);

        RevenueExpenditureResponse revenue2 = new RevenueExpenditureResponse(2L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.FIXED,
                LocalDate.parse("2022-04-01"), "통신비", "휴대폰 요금", "계좌 이체", 100000);

        RevenueExpenditureResponse revenue3 = new RevenueExpenditureResponse(3L, RevenueExpenditureType.EXPENDITURE, AssetCategoryType.VARIABLE,
                LocalDate.parse("2022-04-05"), "식비", "오마카세 먹었어요!", "신용 카드", 150000);

        revenueExpenditure.add(revenue1);
        revenueExpenditure.add(revenue2);
        revenueExpenditure.add(revenue3);

        RevenueExpenditureSumResponse response = RevenueExpenditureSumResponse.of(2000000, 250000, 100000, new PageImpl<>(revenueExpenditure, Pageable.ofSize(1), 3));

        given(assetService.findRevenueExpenditureByMonth(anyString(), any(Pageable.class))).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/assets/revenueExpenditure?month=2022-04&page=0&size=3"));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"totalRevenue\":2000000")))
                .andExpect(content().string(
                        containsString("\"totalExpenditure\":250000")))
                .andExpect(content().string(
                        containsString("\"remainingBudget\":100000")))
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(assetService).findRevenueExpenditureByMonth(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("수익 지출 내역 조회 - 예산 설정 PK를 찾지 못한 경우 실패")
    void getRevenueExpenditureFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_BUDGET))
                .when(assetService).findRevenueExpenditureByMonth(anyString(), any(Pageable.class));

        //when
        ResultActions result = mockMvc.perform(get("/assets/revenueExpenditure?month=2022-04&page=0&size=3"));

        //then
        result.andExpect(status().isNotFound())
                .andDo(print());

        verify(assetService).findRevenueExpenditureByMonth(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("자산 관리 목표 작성 - 성공")
    void addAssetGoal() throws Exception {
        //given
        given(assetService.addAssetGoal(any())).willReturn(1L);
        String json = objectMapper.writeValueAsString(new CreateAssetGoalRequest(LocalDate.parse("2022-05-04"), "이번달 목표 최대한 배달 지양하기"));

        //when
        ResultActions result = mockMvc.perform(put("/assets/asset-goal")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(json));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.assetGoalId").value(1))
                .andExpect(jsonPath("$.assetGoalId").hasJsonPath())
                .andExpect(jsonPath("$.assetGoalId").exists())
                .andExpect(jsonPath("$.assetGoalId").isNotEmpty())
                .andExpect(jsonPath("$.assetGoalId").isNumber())
                .andDo(print());

        verify(assetService).addAssetGoal(any(CreateAssetGoalRequest.class));
    }

    @Test
    @DisplayName("머니 로그 작성 - 성공")
    void createMoneyLog() throws Exception {
        //given
        List<String> imageUrl = new ArrayList<>();
        imageUrl.add("https://s3uploadImages.landom/alzkmzlmqwedsaklxzlncwepoamskzmx.mcsad;dkjsaljdklasdjlajsdklajdalsdjkla");
        imageUrl.add("https://s3uploadImages.landom/dkqieoopoamskzmx.mcsadaslkjqpad;dkjsaljdklasdjlajsdklajdalsdjkla");
        imageUrl.add("https://s3uploadImages.landom/doqpowiepepoamskzmx.mcsad;ddsaasddwqesaljdklasdjlajsdklajdalsdjkla");

        CreateMoneyLogResponse response = new CreateMoneyLogResponse(1L, imageUrl);

        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "모아모아1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "모아모아2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));

        given(assetService.createMoneyLog(any())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(multipart("/assets/money-log")
                .file("imageFiles", imageFiles.get(0).getBytes())
                .file("imageFiles", imageFiles.get(1).getBytes())
                .param("date", "2022-05-04")
                .param("content", "치킨 배달 -19000")
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("POST");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(assetService).createMoneyLog(any(CreateMoneyLogRequest.class));
    }

}