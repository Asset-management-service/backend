package com.backend.moamoa.builder;

import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.entity.enums.Gender;
import com.backend.moamoa.domain.user.oauth.entity.enums.ProviderType;
import com.backend.moamoa.global.audit.TimeEntity;

public class UserBuilder {

    private UserBuilder() {
    }

    public static User dummyUser() {
        return User.builder()
                .id(1L)
                .providerType(ProviderType.GOOGLE)
                .userId("123456L")
                .email("ehgns5668@naver.com")
                .nickname("Test")
                .phoneNum("01033333333")
                .birthday("01-03")
                .birthYear("2001")
                .gender(Gender.MAN)
                .timeEntity(new TimeEntity())
                .build();
    }
}
