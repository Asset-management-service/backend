package com.backend.moamoa.domain.user.oauth.info.impl;

import com.backend.moamoa.domain.user.entity.enums.Gender;
import com.backend.moamoa.domain.user.oauth.info.OAuth2UserInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return (String) getProfile().get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) getKakaoAccount().get("email");
    }

    @Override
    public String getBirthday() {
        return (String) getKakaoAccount().get("birthday");
    }

    @Override
    public Gender getGender() {
        String gender = (String) getKakaoAccount().get("gender");

        if (gender != null) {
            if (gender.equals("male")) {
                return Gender.MAN;
            }
            return Gender.WOMAN;
        }
        return null;
    }

    @Override
    public String getBirthYear() {
        return (String) getKakaoAccount().get("birthyear");
    }

    public Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    public Map<String, Object> getProfile() {
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }

}
