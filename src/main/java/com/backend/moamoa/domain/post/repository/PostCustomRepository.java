package com.backend.moamoa.domain.post.repository;

import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostCustomRepository {
    List<Post> findMyPostsById(Long id);

    PostOneResponse findOnePostById(Long postId);
}
