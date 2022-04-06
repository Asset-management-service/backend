package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.dto.request.RecentPostRequest;
import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.dto.response.RecentPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostCustomRepository {

    Optional<PostOneResponse> findOnePostById(Long postId);

    Page<RecentPostResponse> findRecentPosts(Pageable pageable, RecentPostRequest request);

}
