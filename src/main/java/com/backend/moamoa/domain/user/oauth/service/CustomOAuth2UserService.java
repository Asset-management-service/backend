package com.backend.moamoa.domain.user.oauth.service;

import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.user.oauth.entity.CustomUserDetails;
import com.backend.moamoa.domain.user.oauth.entity.enums.ProviderType;
import com.backend.moamoa.domain.user.entity.User;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AssetCategoryRepository assetCategoryRepository;

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
        Optional<User> savedUser = userRepository.findByUserId(userInfo.getId());

        if (savedUser.isPresent()) {
            updateUser(savedUser.get(), userInfo);
        } else {
            savedUser = Optional.of(createUser(userInfo, providerType));
        }

        return CustomUserDetails.create(savedUser.get(), user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        User user = userRepository.saveAndFlush(
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
        List<AssetCategory> defaultCategories = createDefaultCategories(user);
        defaultCategories
                .forEach(category -> assetCategoryRepository.saveAndFlush(category));

        return user;
    }
    private List<AssetCategory> createDefaultCategories(User user) {
        return Arrays.asList(AssetCategory.createCategory(AssetCategoryType.FIXED, "주거비", user),
                AssetCategory.createCategory(AssetCategoryType.FIXED, "보험료", user),
                AssetCategory.createCategory(AssetCategoryType.FIXED, "통신비", user),
                AssetCategory.createCategory(AssetCategoryType.FIXED, "유료구독서비스", user),
                AssetCategory.createCategory(AssetCategoryType.FIXED, "예적금", user),
                AssetCategory.createCategory(AssetCategoryType.FIXED, "기타", user),
                AssetCategory.createCategory(AssetCategoryType.REVENUE, "월급", user),
                AssetCategory.createCategory(AssetCategoryType.REVENUE, "주식", user),
                AssetCategory.createCategory(AssetCategoryType.REVENUE, "기타", user),
                AssetCategory.createCategory(AssetCategoryType.VARIABLE, "교통비", user),
                AssetCategory.createCategory(AssetCategoryType.VARIABLE, "식비", user),
                AssetCategory.createCategory(AssetCategoryType.VARIABLE, "생활비", user),
                AssetCategory.createCategory(AssetCategoryType.VARIABLE, "쇼핑", user),
                AssetCategory.createCategory(AssetCategoryType.VARIABLE, "문화생활", user),
                AssetCategory.createCategory(AssetCategoryType.VARIABLE, "경조사", user),
                AssetCategory.createCategory(AssetCategoryType.VARIABLE, "기타", user),
                AssetCategory.createCategory(AssetCategoryType.PAYMENT, "현금", user),
                AssetCategory.createCategory(AssetCategoryType.PAYMENT, "신용카드", user),
                AssetCategory.createCategory(AssetCategoryType.PAYMENT, "체크카드", user));
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getNickname().equals(userInfo.getName())) {
            user.setNickname(userInfo.getName());
        }

        return user;
    }

}
