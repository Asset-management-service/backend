package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long>, PostImageRepositoryCustom {
}
