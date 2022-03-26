package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.entity.dto.PostDetailResponseDto;
import com.backend.moamoa.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class PostApiController {

    private final PostService postService;

    // post 객체 id 한건 조회
    @GetMapping("/{postId}")
    public ResponseEntity retrievePostDetail(@PathVariable("postId")Long postId) {

        PostDetailResponseDto responseDto = postService.findPostDetailDto(postId);
        log.info("dto {}",responseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


}

