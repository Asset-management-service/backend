package com.backend.moamoa.domain.post.repository;

import com.backend.moamoa.domain.post.entity.dto.PostDetailResponseDto;

import java.util.Optional;

public interface PostCustomRepository {

    Optional<PostDetailResponseDto> findPostDetailDto(Long postId);
}
