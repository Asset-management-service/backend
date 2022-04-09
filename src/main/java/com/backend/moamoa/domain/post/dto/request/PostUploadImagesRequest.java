package com.backend.moamoa.domain.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostUploadImagesRequest {

    private Long postId;

    private List<MultipartFile> images = new ArrayList<>();

}
