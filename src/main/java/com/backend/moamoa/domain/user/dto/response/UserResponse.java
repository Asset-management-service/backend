package com.backend.moamoa.domain.user.dto.response;

import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("유저 조회 정보")
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
public class UserResponse {

    @ApiModelProperty(value = "유저 식별자", example = "1")
    private Long id;

    @ApiModelProperty(value = "유저 닉네임", example = "모아모아")
    private String nickname;

    @ApiModelProperty(value = "유저 이메일", example = "kmw106933@naver.com")
    private String email;

    @ApiModelProperty(value = "유저 전화번호", example = "01055555555")
    private String phoneNum;

    @ApiModelProperty(value = "유저 성별", example = "MAN")
    private Gender gender;

    @ApiModelProperty(value = "유저 생일", example = "03-20")
    private String birthday;

    @ApiModelProperty(value = "유저 탄생년도", example = "2001")
    private String birthYear;

    @ApiModelProperty(value = "생성 시각", example = "")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "수정 시각", example = "")
    private LocalDateTime updatedDate;

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNum(user.getPhoneNum())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .birthYear(user.getBirthYear())
                .createdDate(user.getTimeEntity().getCreatedDate())
                .updatedDate(user.getTimeEntity().getUpdatedDate())
                .build();
    }

}
