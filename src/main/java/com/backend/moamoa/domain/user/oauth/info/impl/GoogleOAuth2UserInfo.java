package com.backend.moamoa.domain.user.oauth.info.impl;

import com.backend.moamoa.domain.user.oauth.info.OAuth2UserInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getBirthday() {
        return null;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public String getBirthYear() {
        return null;
    }

}
