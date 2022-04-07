package com.backend.moamoa.global.s3;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class S3Controller {

    private final S3Uploader s3Uploader;

    @PostMapping("/images")
    @ApiOperation(value = "이미지 업로드",
            notes = "사용자가 이미지를 업로드하고 요청하면 imageUrl을 반환합니다.")
    public String upload(@RequestParam("images") MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "static");
    }
}
