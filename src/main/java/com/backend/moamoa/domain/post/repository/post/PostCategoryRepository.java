package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    Optional<PostCategory> findByCategoryName(String categoryName);
}
