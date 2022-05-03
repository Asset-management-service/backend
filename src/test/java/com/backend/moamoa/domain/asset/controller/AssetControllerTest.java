package com.backend.moamoa.domain.asset.controller;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.asset.dto.response.AssetCategoryDtoResponse;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.backend.moamoa.domain.asset.service.AssetService;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.entity.enums.Gender;
import com.backend.moamoa.domain.user.oauth.entity.enums.ProviderType;
import com.backend.moamoa.domain.user.oauth.filter.JwtFilter;
import com.backend.moamoa.domain.user.oauth.service.CustomUserDetailsService;
import com.backend.moamoa.domain.user.oauth.token.JwtProvider;
import com.backend.moamoa.domain.user.service.UserService;
import com.backend.moamoa.global.audit.TimeEntity;
import com.backend.moamoa.global.bean.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void getCategory() throws Exception {
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

        mockMvc.perform(
                        get("/assets/category?categoryType=fixed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").exists())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(assetService).getCategories("fixed");
    }

}