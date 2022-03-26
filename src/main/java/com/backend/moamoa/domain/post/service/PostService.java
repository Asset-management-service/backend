package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.entity.dto.PostDetailResponseDto;
import com.backend.moamoa.domain.post.repository.PostRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;


    public PostDetailResponseDto findPostDetailDto(Long postId) {
        return postRepository.findPostDetailDto(postId)
                .orElseThrow (() -> new CustomException (ErrorCode.NOT_FOUND_POST));
    }


}
