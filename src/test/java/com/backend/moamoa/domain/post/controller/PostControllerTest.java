package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.asset.controller.AssetController;
import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.service.PostService;
import com.backend.moamoa.domain.user.oauth.filter.JwtFilter;
import com.backend.moamoa.global.bean.security.SecurityConfig;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PostController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class)})
class PostControllerTest {

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("게시글 단건 조회 - 성공")
    void getOnePost() throws Exception {
        //given
        PostOneResponse response = new PostOneResponse(1L, "모아모아!!", "test", 3, 5, 5, LocalDateTime.now(), null, 60,
                "ehgns5668", true, true, true);
        given(postService.getOnePost(anyLong())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/posts/1"));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(postService, times(1)).getOnePost(anyLong());
    }

    @Test
    @DisplayName("게시글 단건 조회 - Post PK를 찾지 못한 경우 실패")
    void getOnePostFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_POST))
                .when(postService).getOnePost(anyLong());

        //when
        ResultActions result = mockMvc.perform(get("/posts/1"));

        //then
        result.andExpect(status().isNotFound());

        verify(postService, times(1)).getOnePost(anyLong());
    }

}