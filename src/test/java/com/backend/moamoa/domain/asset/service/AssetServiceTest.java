package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private UserUtil userUtil;

    @Mock
    private AssetCategoryRepository assetCategoryRepository;

    @InjectMocks
    private AssetService assetService;


    @Test
    @DisplayName("가계부 설정 카테고리 생성")
    void addCategory() {
        //given
        User user = UserBuilder.dummyUser();
        AssetCategory assetCategory = AssetCategory.builder().id(1L).assetCategoryType(AssetCategoryType.FIXED).categoryName("월급").user(user).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(assetCategoryRepository.save(any(AssetCategory.class))).willReturn(assetCategory);

        //when
        Long categoryId = assetService.addCategory(new AssetCategoryRequest(AssetCategoryType.FIXED, "월급"));

        //then
        assertThat(categoryId).isEqualTo(1L);
        verify(userUtil).findCurrentUser();
        verify(assetCategoryRepository).save(any(AssetCategory.class));
    }

}