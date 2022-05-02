package com.backend.moamoa.domain.user.service;

import com.backend.moamoa.domain.post.repository.comment.CommentRepository;
import com.backend.moamoa.domain.post.repository.post.PostRepository;
import com.backend.moamoa.domain.post.repository.post.ScrapRepository;
import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.entity.enums.Gender;
import com.backend.moamoa.domain.user.oauth.entity.enums.ProviderType;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.audit.TimeEntity;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserUtil userUtil;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        User user = User.builder()
                .id(1L)
                .providerType(ProviderType.GOOGLE)
                .userId("123456L")
                .email("kmw106933@naver.com")
                .nickname("asd")
                .phoneNum("01022222222")
                .birthday("01-03")
                .birthYear("2001")
                .gender(Gender.WOMAN)
                .timeEntity(new TimeEntity())
                .build();

        given(userUtil.findCurrentUser()).willReturn(user);
    }

    @Test
    @DisplayName("유저 불러오기")
    void getUser() throws Exception {
        User testUser = userService.getUser();

        Assertions.assertEquals(testUser.getId(), 1L);
        Assertions.assertEquals(testUser.getUserId(), "123456L");
        Assertions.assertEquals(testUser.getEmail(), "kmw106933@naver.com");
        Assertions.assertEquals(testUser.getNickname(), "asd");
        Assertions.assertEquals(testUser.getPhoneNum(), "01022222222");

        verify(userUtil).findCurrentUser();
    }

    @Test
    @DisplayName("유저 수정하기")
    void update() throws Exception {
        //given
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(userRepository.existsByPhoneNum(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .nickname("test")
                .phoneNum("01033333333")
                .email("test@test.com")
                .gender(Gender.MAN)
                .build();

        User user = userService.update(userUpdateRequest);

        Assertions.assertEquals(user.getEmail(), "test@test.com");
        Assertions.assertEquals(user.getNickname(), "test");
        Assertions.assertEquals(user.getPhoneNum(), "01033333333");
        Assertions.assertEquals(user.getGender(), Gender.MAN);

        verify(userRepository).existsByNickname(anyString());
        verify(userRepository).existsByPhoneNum(anyString());
        verify(userRepository).existsByEmail(anyString());
    }

    @Test
    @DisplayName("중복된 정보로 유저 수정하기")
    void invalidUpdate() throws Exception {
        //given
        given(userRepository.existsByNickname("test")).willThrow(new CustomException(ErrorCode.ALREADY_NICKNAME_EXISTS));
        given(userRepository.existsByPhoneNum("010")).willThrow(new CustomException(ErrorCode.ALREADY_PHONE_NUM_EXISTS));
        given(userRepository.existsByEmail("moa@moa.com")).willThrow(new CustomException(ErrorCode.ALREADY_EMAIL_EXISTS));

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .nickname("test")
                .phoneNum("01033333333")
                .email("test@test.com")
                .gender(Gender.MAN)
                .build();

        assertThatThrownBy(() -> userService.update(userUpdateRequest))
                .isInstanceOf(CustomException.class);

        verify(userRepository).existsByNickname("test");

        userUpdateRequest.setNickname("test11");
        userUpdateRequest.setPhoneNum("010");

        assertThatThrownBy(() -> userService.update(userUpdateRequest))
                .isInstanceOf(CustomException.class);

        verify(userRepository).existsByPhoneNum("010");

        userUpdateRequest.setPhoneNum("0101");
        userUpdateRequest.setEmail("moa@moa.com");

        assertThatThrownBy(() -> userService.update(userUpdateRequest))
                .isInstanceOf(CustomException.class);

        verify(userRepository).existsByEmail("moa@moa.com");
    }

}
