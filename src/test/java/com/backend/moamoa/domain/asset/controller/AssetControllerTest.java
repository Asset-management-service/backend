package com.backend.moamoa.domain.asset.controller;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.dto.request.BudgetRequest;
import com.backend.moamoa.domain.asset.dto.response.AssetCategoryDtoResponse;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
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
    public void addCategory() throws Exception {
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
    public void deleteCategoryName() throws Exception {
        //when
        ResultActions result = mockMvc.perform(delete("/assets/category/1"));

        //then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("가계부 설정 카테고리 삭제 카테고리 ID를 찾지 못한 경우 - 실패")
    public void deleteCategoryNameNotFountCategory() throws Exception {
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

}