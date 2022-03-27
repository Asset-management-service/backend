package com.backend.moamoa.domain.user.oauth.service;

import com.backend.moamoa.domain.user.oauth.entity.ProviderType;
import com.backend.moamoa.domain.user.oauth.entity.RoleType;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.oauth.entity.UserPrincipal;
import com.backend.moamoa.domain.user.oauth.exception.OAuthProviderMissMatchException;
import com.backend.moamoa.domain.user.oauth.info.OAuth2UserInfo;
import com.backend.moamoa.domain.user.oauth.info.OAuth2UserInfoFactory;
import com.backend.moamoa.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        User savedUser = userRepository.findByUserId(userInfo.getId());

        if (savedUser != null) {
            if (providerType != savedUser.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + savedUser.getProviderType() + " account to login."
                );
            }
            updateUser(savedUser, userInfo);
        } else {
            savedUser = createUser(userInfo, providerType);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        return userRepository.saveAndFlush(
                User.builder()
                        .userId(userInfo.getId())
                        .nickname(userInfo.getName())
                        .email(userInfo.getEmail())
                        .birthday(userInfo.getBirthday())
                        .birthYear(userInfo.getBirthYear())
                        .gender(userInfo.getGender())
                        .providerType(providerType)
                        .build()
        );
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getNickname().equals(userInfo.getName())) {
            user.setNickname(userInfo.getName());
        }

        return user;
    }
}
