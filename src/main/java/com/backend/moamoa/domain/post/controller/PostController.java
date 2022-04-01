package com.backend.moamoa.domain.post.controller;

import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.LikeResponse;
import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.dto.response.PostResponse;
import com.backend.moamoa.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public PostOneResponse getPost(@PathVariable Long postId) {
        return postService.findById(postId);
    }

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

    @PostMapping("/{postId}/likes")
    public LikeResponse likePost(@PathVariable Long postId){
        return postService.likePost(postId);
    }

//    @GetMapping("/my")
//    public void myPosts() {
//        postService.findMyPosts();
//    }


}
