package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.dto.request.CommentRequest;
import com.backend.moamoa.domain.post.dto.request.CommentUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.CommentResponse;
import com.backend.moamoa.domain.post.dto.response.UpdateCommentResponse;
import com.backend.moamoa.domain.post.service.CommentService;
import com.backend.moamoa.domain.user.oauth.filter.JwtFilter;
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
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CommentController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class)})
class CommentControllerTest {

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 댓글 작성 - 성공")
    void createComment() throws Exception {
        //given
        CommentResponse response = new CommentResponse(1L, "ehgns5668", LocalDateTime.now());
        given(commentService.createComment(any(CommentRequest.class))).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/comments")
                .content(objectMapper.writeValueAsString(new CommentRequest(1L, 1L, "대댓글")))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(commentService, timeout(1)).createComment(any(CommentRequest.class));
    }

    @Test
    @DisplayName("게시글 댓글 작성 - Post PK를 찾지 못한 경우 실패")
    void createCommentFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_POST))
                .when(commentService).createComment(any(CommentRequest.class));

        //when
        ResultActions result = mockMvc.perform(post("/comments")
                .content(objectMapper.writeValueAsString(new CommentRequest(1L, 1L, "대댓글")))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNotFound());

        verify(commentService, times(1)).createComment(any(CommentRequest.class));
    }

    @Test
    @DisplayName("게시글 댓글 삭제 - 성공")
    void deleteComment() throws Exception {
        //when
        ResultActions result = mockMvc.perform(delete("/comments/1"));

        //then
        result.andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(anyLong());
    }

    @Test
    @DisplayName("게시글 댓글 삭제 - 해당 유저가 권한이 없는 경우 실패")
    void deleteCommentFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.FORBIDDEN_USER))
                .when(commentService).deleteComment(anyLong());
        //when
        ResultActions result = mockMvc.perform(delete("/comments/1"));

        //then
        result.andExpect(status().isForbidden())
                .andDo(print());

        verify(commentService, times(1)).deleteComment(anyLong());
    }

    @Test
    @DisplayName("게시글 댓글 수정 - 성공")
    void updateComment() throws Exception {
        //given
        given(commentService.updateComment(any(CommentUpdateRequest.class))).willReturn(1L);

        //when
        ResultActions result = mockMvc.perform(patch("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CommentUpdateRequest(1L, "test")))
                .characterEncoding(StandardCharsets.UTF_8));

        //then
        result.andExpect(status().isOk())
                .andExpectAll(content().json(objectMapper.writeValueAsString(new UpdateCommentResponse(1L))))
                .andDo(print());

        verify(commentService, times(1)).updateComment(any(CommentUpdateRequest.class));
    }

    @Test
    @DisplayName("게시글 댓글 수정 - 해당 유저가 권한이 없는 경우 실패")
    void updateCommentFail() throws Exception{
        //given
        doThrow(new CustomException(ErrorCode.FORBIDDEN_USER))
                .when(commentService).updateComment(any(CommentUpdateRequest.class));

        //when
        ResultActions result = mockMvc.perform(patch("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CommentUpdateRequest(1L, "test")))
                .characterEncoding(StandardCharsets.UTF_8));

        //then
        result.andExpect(status().isForbidden());

        verify(commentService, times(1)).updateComment(any(CommentUpdateRequest.class));
    }
}