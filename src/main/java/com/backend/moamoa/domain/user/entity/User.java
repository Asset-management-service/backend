package com.backend.moamoa.domain.user.entity;

import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.dto.response.UserResponse;
import com.backend.moamoa.domain.user.enums.Gender;
import com.backend.moamoa.domain.user.oauth.entity.ProviderType;
import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditListener.class)
@AllArgsConstructor
@Entity
@Builder
public class User implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(name = "user_userId")
    private String userId;

    @Enumerated(STRING)
    @NotNull
    private ProviderType providerType;

    private String email;

    @NotNull
    private String nickname;

    @Builder.Default
    private String phoneNum = "";

    private String birthday;

    private String birthYear;

    @Enumerated(STRING)
    private Gender gender;

    @Embedded
    private TimeEntity timeEntity;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void update(UserUpdateRequest userUpdateRequest) {
        this.nickname = userUpdateRequest.getNickname();
        this.phoneNum = userUpdateRequest.getPhoneNum();
        this.gender = userUpdateRequest.getGender();
    }

    public UserResponse toUserResponse() {
        return UserResponse.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .phoneNum(phoneNum)
                .gender(gender)
                .birthday(birthday)
                .birthYear(birthYear)
                .createdDate(timeEntity.getCreatedDate())
                .updatedDate(timeEntity.getUpdatedDate())
                .build();
    }

}
