package com.backend.moamoa.domain.post.repository;

import com.backend.moamoa.domain.post.entity.dto.PostDetailResponseDto;

public interface PostCustomRepository {

    PostDetailResponseDto findPostDetailDto(Long postId);
}
