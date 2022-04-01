package com.backend.moamoa.domain.post.repository;

import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.entity.PostLike;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);

}
