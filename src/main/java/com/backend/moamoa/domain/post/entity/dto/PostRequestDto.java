package com.backend.moamoa.domain.post.entity.dto;

import lombok.Data;

@Data
public class PostRequestDto {

    private Long userId;
    private String title;
    private String context;

}
