package com.backend.moamoa.domain.post.repository;

import com.backend.moamoa.domain.post.entity.Post;

import java.util.List;

public interface PostCustomRepository {
    List<Post> findMyPostsById(Long id);
}
