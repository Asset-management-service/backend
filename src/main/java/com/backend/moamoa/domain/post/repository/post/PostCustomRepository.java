package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.dto.response.RecentPostResponse;
import com.backend.moamoa.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostCustomRepository {

    Optional<PostOneResponse> findOnePostById(Long postId, Long userId);

    Page<RecentPostResponse> findRecentPosts(Pageable pageable, String categoryName);

    Optional<Post> findByIdAndUser(Long postId, Long userId);

}
