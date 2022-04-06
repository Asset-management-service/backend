package com.backend.moamoa.domain.post.repository;

import com.backend.moamoa.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
}