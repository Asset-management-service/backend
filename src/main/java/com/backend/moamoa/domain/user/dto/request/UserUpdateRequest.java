package com.backend.moamoa.domain.user.dto.request;

import com.backend.moamoa.domain.user.entity.enums.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="개인정보 수정 요청 정보")
public class UserUpdateRequest {

    @ApiModelProperty(value = "닉네임", required = true, example = "모아모아")
    private String nickname;

    @ApiModelProperty(value = "전화번호", required = true, example = "01055555555")
    private String phoneNum;

    @ApiModelProperty(value = "성별", required = true, example = "MAN")
    private Gender gender;

}
