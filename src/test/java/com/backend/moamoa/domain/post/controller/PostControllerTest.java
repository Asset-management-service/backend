package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.PostCreateResponse;
import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.dto.response.PostUpdateResponse;
import com.backend.moamoa.domain.post.dto.response.RecentPostResponse;
import com.backend.moamoa.domain.post.service.PostService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    @DisplayName("카테고리별 최근 게시글 조회")
    void getAllPosts() throws Exception {
        //given
        List<RecentPostResponse> recentPostResponses = List.of(
                new RecentPostResponse(1L, "자유게시판", "모아모아", "test", 5, "ehgns5668", LocalDateTime.now(), 60),
                new RecentPostResponse(2L, "자유게시판", "test1", "test1", 10, "ehgns", LocalDateTime.now(), 61));

        PageImpl<RecentPostResponse> response = new PageImpl<>(recentPostResponses, PageRequest.of(1, 3), 2);

        given(postService.getRecentPost(any(Pageable.class), anyString())).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/posts/recent?categoryName=자유게시판"));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 생성  - 성공")
    void createPost() throws Exception {
        //given
        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "모아모아1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "모아모아2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));

        List<String> imageUrl = List.of("https://s3uploader.Moamoa1/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc",
                "https://s3uploader.moamoa2/ezzzyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as");

        PostCreateResponse response = new PostCreateResponse(1L, imageUrl);
        given(postService.createPost(any(PostRequest.class))).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(multipart("/posts")
                .file("imageFiles", imageFiles.get(0).getBytes())
                .file("imageFiles", imageFiles.get(1).getBytes())
                .param("title", "test1")
                .param("content", "test1")
                .param("categoryName", "자유게시판")
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("POST");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(postService, times(1)).createPost(any(PostRequest.class));
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updatePost() throws Exception {
        //given
        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "모아모아1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "모아모아2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));

        List<String> imageUrl = List.of("https://s3uploader.Moamoa1/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc",
                "https://s3uploader.moamoa2/ezzzyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as");

        PostUpdateResponse response = new PostUpdateResponse(1L, imageUrl);
        given(postService.updatePost(any(PostUpdateRequest.class))).willReturn(response);

        //when
        ResultActions result = mockMvc.perform(multipart("/posts")
                .file("imageFiles", imageFiles.get(0).getBytes())
                .file("imageFiles", imageFiles.get(1).getBytes())
                .param("postId", "1")
                .param("title", "test1")
                .param("content", "test1")
                .param("saveImageUrl", imageUrl.get(0), imageUrl.get(1))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PATCH");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(print());

        verify(postService, times(1)).updatePost(any(PostUpdateRequest.class));
    }

    @Test
    @DisplayName("게시글 수정 - Post PK를 찾지 못한 경우 실패")
    void updatePostFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_POST))
                .when(postService).updatePost(any(PostUpdateRequest.class));

        //when
        ResultActions result = mockMvc.perform(multipart("/posts")
                .param("postId", "1")
                .param("title", "test1")
                .param("content", "test1")
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PATCH");
                    return requestPostProcessor;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        result.andExpect(status().isNotFound());

        verify(postService, times(1)).updatePost(any(PostUpdateRequest.class));
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost() throws Exception {
        //when
        ResultActions result = mockMvc.perform(delete("/posts/1"));

        //then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("게시글 삭제 - Post PK를 찾지 못한 경우 실패")
    void deletePostFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_POST))
                .when(postService).deletePost(anyLong());

        //when
        ResultActions result = mockMvc.perform(delete("/posts/1"));

        //then
        result.andExpect(status().isNotFound());

        verify(postService, times(1)).deletePost(anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 - 성공")
    void likePost() throws Exception {
        //given
        given(postService.likePost(anyLong())).willReturn(true);

        //when
        ResultActions result = mockMvc.perform(post("/posts/1/likes"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.likeStatus").isBoolean())
                .andDo(print());

        verify(postService, times(1)).likePost(anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 - Post PK를 찾지 못한 경우 실패")
    void likePostFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_POST))
                .when(postService).likePost(anyLong());

        //when
        ResultActions result = mockMvc.perform(post("/posts/1/likes"));

        //then
        result.andExpect(status().isNotFound());

        verify(postService, times(1)).likePost(anyLong());
    }

    @Test
    @DisplayName("게시글 스크랩 - 성공")
    void scrapPost() throws Exception {
        //given
        given(postService.scrapPost(anyLong())).willReturn(true);

        //when
        ResultActions result = mockMvc.perform(post("/posts/1/scrap"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.scrapStatus").isBoolean())
                .andDo(print());

        verify(postService, times(1)).scrapPost(anyLong());
    }

    @Test
    @DisplayName("게시글 스크랩 - Post PK를 찾지 못한 경우 실패")
    void scrapPostFail() throws Exception {
        //given
        doThrow(new CustomException(ErrorCode.NOT_FOUND_POST))
                .when(postService).scrapPost(anyLong());

        //when
        ResultActions result = mockMvc.perform(post("/posts/1/scrap"));

        //then
        result.andExpect(status().isNotFound());

        verify(postService, times(1)).scrapPost(anyLong());
    }

}