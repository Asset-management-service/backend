package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.PostResponse;
import com.backend.moamoa.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;


    @PostMapping
    public PostResponse createPost(@RequestBody PostRequest request) {
        return postService.createPost(request);
    }

    @PatchMapping
    public PostResponse updatePost(@RequestBody PostUpdateRequest request) {
        return postService.updatePost(request);
    }

    @DeleteMapping("/{postId}")
    public PostResponse deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/my")
    public void myPosts() {
        postService.findMyPosts();
    }


}
