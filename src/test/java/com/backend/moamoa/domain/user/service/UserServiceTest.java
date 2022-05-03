package com.backend.moamoa.domain.user.service;

import com.backend.moamoa.domain.post.entity.Comment;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.entity.PostCategory;
import com.backend.moamoa.domain.post.entity.Scrap;
import com.backend.moamoa.domain.post.repository.comment.CommentRepository;
import com.backend.moamoa.domain.post.repository.post.PostRepository;
import com.backend.moamoa.domain.post.repository.post.ScrapRepository;
import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.dto.response.MyCommentResponse;
import com.backend.moamoa.domain.user.dto.response.MyPostResponse;
import com.backend.moamoa.domain.user.dto.response.MyScrapResponse;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.entity.UserMailAuth;
import com.backend.moamoa.domain.user.entity.enums.Gender;
import com.backend.moamoa.domain.user.oauth.entity.enums.ProviderType;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.audit.TimeEntity;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserUtil userUtil;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 불러오기")
    void getUser() throws Exception {
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
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(userRepository.existsByPhoneNum(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .nickname("test")
                .phoneNum("01033333333")
                .email("test@test.com")
                .gender(Gender.MAN)
                .build();

        User updateUser = userService.update(userUpdateRequest);

        Assertions.assertEquals(updateUser.getEmail(), "test@test.com");
        Assertions.assertEquals(updateUser.getNickname(), "test");
        Assertions.assertEquals(updateUser.getPhoneNum(), "01033333333");
        Assertions.assertEquals(updateUser.getGender(), Gender.MAN);

        verify(userRepository).existsByNickname(anyString());
        verify(userRepository).existsByPhoneNum(anyString());
        verify(userRepository).existsByEmail(anyString());
    }

    @Test
    @DisplayName("중복된 정보로 유저 수정하기")
    void invalidUpdate() throws Exception {
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

    @Test
    @DisplayName("이메일 변경하기")
    void confirmEmail() throws Exception {
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

        given(userRepository.findByIdAndDeletedIsFalse(1L)).willReturn(Optional.ofNullable(user));

        UserMailAuth authToken = UserMailAuth.builder()
                .id(1L)
                .userId(1L)
                .mail("moa@moa.com")
                .authKey("123456")
                .expirationDate(LocalDateTime.now())
                .expired(false)
                .build();

        userService.confirmEmail(authToken);

        Assertions.assertEquals(user.getEmail(), "moa@moa.com");
        verify(userRepository).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    @DisplayName("중복된 이메일로 중복 확인을 할 경우")
    void isDuplicateEmail() throws Exception {
        final String EXISTED_EMAIL = "exist@email.com";
        given(userRepository.existsByEmailAndEmailCheckIsTrue(EXISTED_EMAIL))
                .willThrow(new CustomException(ErrorCode.ALREADY_EMAIL_EXISTS));

        assertThatThrownBy(() -> userService.isDuplicateEmail(EXISTED_EMAIL))
                .isInstanceOf(CustomException.class);

        verify(userRepository).existsByEmailAndEmailCheckIsTrue(EXISTED_EMAIL);
    }

    @Test
    @DisplayName("중복되지 않은 이메일로 중복 확인을 할 경우")
    void isDuplicateByValidEmail() throws Exception {
        final String VALID_EMAIL = "moamoa@email.com";
        given(userRepository.existsByEmailAndEmailCheckIsTrue(VALID_EMAIL))
                .willReturn(false);

        Boolean duplicateEmail = userService.isDuplicateEmail(VALID_EMAIL);

        Assertions.assertEquals(duplicateEmail, false);
        verify(userRepository).existsByEmailAndEmailCheckIsTrue(VALID_EMAIL);
    }

    @Test
    @DisplayName("마이페이지의 내가 쓴 글 정상적으로 조회")
    void findMyPosts() throws Exception {
        Pageable pageable = PageRequest.of(0, 8);
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
        given(postRepository.findByUserOrderByIdDesc(user, pageable)).will(
                invocation -> {
                    User testUser = invocation.getArgument(0);
                    Pageable page = invocation.getArgument(1);

                    Post post = new Post("test", "test", 0, testUser, new ArrayList<>(), new PostCategory("test", null));
                    Post secondPost = new Post("test2", "test2", 0, testUser, new ArrayList<>(), new PostCategory("test", null));

                    post.setTimeEntity(new TimeEntity());
                    secondPost.setTimeEntity(new TimeEntity());

                    List<Post> posts = new ArrayList<>();
                    posts.add(post);
                    posts.add(secondPost);

                    return new PageImpl<>(posts, page, posts.size());
                }
        );
        Page<MyPostResponse> posts = userService.findMyPosts(pageable);

        Assertions.assertEquals(posts.getContent().get(0).getTitle(), "test");
        Assertions.assertEquals(posts.getContent().get(0).getCategoryName(), "test");
        Assertions.assertEquals(posts.getContent().get(1).getTitle(), "test2");
        Assertions.assertEquals(posts.getContent().get(1).getCategoryName(), "test");

        verify(postRepository).findByUserOrderByIdDesc(user, pageable);
    }

    @Test
    @DisplayName("마이페이지의 내가 쓴 댓글 정상적으로 조회")
    void findMyComments() throws Exception {
        Pageable pageable = PageRequest.of(0, 8);
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
        given(commentRepository.findByUserOrderByIdDesc(user, pageable)).will(
                invocation -> {
                    User testUser = invocation.getArgument(0);
                    Pageable page = invocation.getArgument(1);
                    Post post = new Post("test", "test", 0, testUser, new ArrayList<>(), new PostCategory("test", null));

                    Comment comment = new Comment("test", null, user, post);
                    Comment secondComment = new Comment("test2", null, user, post);

                    comment.setTimeEntity(new TimeEntity());
                    secondComment.setTimeEntity(new TimeEntity());

                    List<Comment> comments = new ArrayList<>();
                    comments.add(comment);
                    comments.add(secondComment);

                    return new PageImpl<>(comments, pageable, comments.size());
                }
        );

        Page<MyCommentResponse> comments = userService.findMyComments(pageable);

        Assertions.assertEquals(comments.getContent().get(0).getContent(), "test");
        Assertions.assertEquals(comments.getContent().get(1).getContent(), "test2");

        verify(commentRepository).findByUserOrderByIdDesc(user, pageable);
    }

    @Test
    @DisplayName("마이페이지의 내 스크랩 정상적으로 조회")
    void findMyScraps() throws Exception {
        Pageable pageable = PageRequest.of(0, 8);
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
        given(scrapRepository.findByUserOrderByIdDesc(user, pageable)).will(
                invocation -> {
                    User testUser = invocation.getArgument(0);
                    Pageable page = invocation.getArgument(1);

                    Post post = new Post("test", "test", 0, testUser, new ArrayList<>(), new PostCategory("test", null));
                    post.setTimeEntity(new TimeEntity());

                    Scrap scrap = new Scrap(post, user);
                    Scrap secondScrap = new Scrap(post, user);

                    List<Scrap> scraps = new ArrayList<>();
                    scraps.add(scrap);
                    scraps.add(secondScrap);

                    return new PageImpl<>(scraps, pageable, scraps.size());
                }
        );
        Page<MyScrapResponse> myScraps = userService.findMyScraps(pageable);

        Assertions.assertEquals(myScraps.getContent().get(0).getTitle(), "test");
        Assertions.assertEquals(myScraps.getContent().get(0).getCategoryName(), "test");

        Assertions.assertEquals(myScraps.getContent().get(1).getTitle(), "test");
        Assertions.assertEquals(myScraps.getContent().get(1).getCategoryName(), "test");

        verify(scrapRepository).findByUserOrderByIdDesc(user, pageable);
    }

}
