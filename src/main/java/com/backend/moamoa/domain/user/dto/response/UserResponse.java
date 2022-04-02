package com.backend.moamoa.domain.user.dto.response;

import com.backend.moamoa.domain.user.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String nickname;

    private String email;

    private String phoneNum;

    private Gender gender;

    private String birthday;

    private String birthYear;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}
