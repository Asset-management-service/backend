package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.*;
import com.backend.moamoa.domain.post.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Api(tags = "커뮤니티 게시글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "게시글 단건 조회", notes = "게시글의 Id를 받아 해당 게시글과 댓글 목록을 조회하는 API")
    @ApiImplicitParam(name = "postId", value = "게시글 PK", example = "1", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당 게시글이 정상적으로 조회된 경우"),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾지 못한 경우")
    })
    @GetMapping("/{postId}")
    public PostOneResponse getOnePost(@PathVariable Long postId) {
        return postService.getOnePost(postId);
    }

    @ApiOperation(value = "카테고리별 최근 게시글 조회", notes = "Request Param 값을 받아 최근 게시글을 조회하는 API")
    @ApiResponse(responseCode = "200", description = "페이징된 게시글이 정상적으로 조회된 경우")
    @ApiImplicitParam(name = "categoryName", value = "해당 게시글 카테고리 이름", example = "모아모아", required = true)
    @GetMapping("/recent")
    public Page<RecentPostResponse> getAllPosts(Pageable pageable, @RequestParam String categoryName){
        return postService.getRecentPost(pageable, categoryName);
    }

    @ApiOperation(value = "게시글 생성", notes = "Form Data 값을 받아와서 글을 생성하는 API",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당 게시글이 정상적으로 생성된 경우"),
            @ApiResponse(responseCode = "404", description = "회원의 Id를 찾지 못한 경우")
    })
    @PostMapping
    public PostCreateResponse createPost(@ModelAttribute PostRequest request){
        return postService.createPost(request);
    }

    @ApiOperation(value = "게시글 수정", notes = "Request Body 값을 받아와서 글을 수정하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당 게시글이 정상적으로 수정된 경우"),
            @ApiResponse(responseCode = "404", description = "회원 OR 게시글의 Id를 찾지 못한 경우")
    })
    @PatchMapping
    public PostUpdateResponse updatePost(@ModelAttribute PostUpdateRequest request) {
        return postService.updatePost(request);
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글 PK를 받아와서 게시글을 삭제하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당 게시글이 정상적으로 삭제된 경우"),
            @ApiResponse(responseCode = "404", description = "회원 OR 게시글의 Id를 찾지 못한 경우")
    })
    @ApiImplicitParam(name = "postId", value = "게시글 PK", example = "1", required = true)
    @DeleteMapping("/{postId}")
    public PostResponse deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }

    @ApiOperation(value = "게시글 좋아요", notes = "게시글 PK를 받아와서 좋아요 상태를 boolean 으로 반환하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당 게시글의 좋아요 상태를 정상적으로 반환한 경우"),
            @ApiResponse(responseCode = "404", description = "회원 OR 게시글의 Id를 찾지 못한 경우")
    })
    @ApiImplicitParam(name = "postId", value = "게시글 PK", example = "1", required = true)
    @PostMapping("/{postId}/likes")
    public LikeResponse likePost(@PathVariable Long postId){
        return postService.likePost(postId);
    }

    @ApiOperation(value = "게시글 스크랩", notes = "게시글의 PK를 받아와서 스크랩 상태를 boolean 으로 반환하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해당 게시글의 스크랩 상태를 정상적으로 반환한 경우"),
            @ApiResponse(responseCode = "404", description = "회원 OR 게시글의 Id를 찾지 못한 경우")
    })
    @ApiImplicitParam(name = "postId", value = "게시글 PK", example = "1", required = true)
    @PostMapping("/{postId}/scrap")
    public ScrapResponse scrapPost(@PathVariable Long postId) {
        return postService.scrapPost(postId);
    }

}
