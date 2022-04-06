package com.backend.moamoa.domain.user.service;

import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.dto.response.UserResponse;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.entity.UserMailAuth;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 전달받은 유저와 유저의 개인정보를 통해 업데이트합니다.
     * @param user 유저 정보
     * @param userUpdateRequest 유저 업데이트
     * @return UserResponse
     */
    @Transactional
    public UserResponse update(User user, UserUpdateRequest userUpdateRequest) {
        user.update(userUpdateRequest);
        return UserResponse.toUserResponse(user);
    }

    /**
     * 전달받은 사용자의 식별자를 통해 유저를 DB에서 조회합니다.
     * @param id 식별자
     * @return User 유저
     */
    public User getUser(Long id) {
        return findUser(id);
    }

    /**
     * 전달받은 사용자의 식별자를 통해 유저를 DB에서 조회하고, 없으면 예외를 던집니다.
     * @param id 식별자
     * @return User 유저
     */
    private User findUser(Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 전달받은 이메일 인증 토큰을 통해 이메일을 변경합니다.
     * @param authToken 이메일 인증 토큰
     */
    @Transactional
    public void confirmEmail(UserMailAuth authToken) {
        User user = findUser(authToken.getUserId());
        authToken.useToken();
        user.updateEmail(authToken.getMail());
    }

    /**
     * 전달받은 사용자 email과 같은 email이 DB에 존재하는지 확인합니다.
     * @param userEmail 전달받은 사용자 email
     * @return DB에 존재 여부. 존재하면 예외, 존재하지 않으면 false를 반환합니다.
     */
    public Boolean isDuplicateEmail(String userEmail) {
        if(userRepository.existsByEmailAndEmailCheckIsTrue(userEmail)) {
            throw new CustomException(ErrorCode.ALREADY_EMAIL_EXISTS);
        } else {
            return false;
        }
    }

}
