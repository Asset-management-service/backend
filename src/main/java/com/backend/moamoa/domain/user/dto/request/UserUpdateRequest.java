package com.backend.moamoa.domain.user.dto.request;

import com.backend.moamoa.domain.user.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String nickname;

    private String phoneNum;

    private Gender gender;

}
