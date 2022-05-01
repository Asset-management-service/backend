package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.post.entity.Comment;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.entity.PostCategory;
import com.backend.moamoa.domain.post.entity.Scrap;
import com.backend.moamoa.domain.user.dto.request.UserEmailRequest;
import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.entity.UserMailAuth;
import com.backend.moamoa.domain.user.entity.enums.Gender;
import com.backend.moamoa.domain.user.entity.enums.RoleType;
import com.backend.moamoa.domain.user.oauth.entity.CustomUserDetails;
import com.backend.moamoa.domain.user.oauth.entity.enums.ProviderType;
import com.backend.moamoa.domain.user.oauth.filter.JwtFilter;
import com.backend.moamoa.domain.user.oauth.service.CustomUserDetailsService;
import com.backend.moamoa.domain.user.oauth.token.JwtProvider;
import com.backend.moamoa.domain.user.service.MailSendService;
import com.backend.moamoa.domain.user.service.UserService;
import com.backend.moamoa.global.audit.TimeEntity;
import com.backend.moamoa.global.bean.security.SecurityConfig;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.in;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
class UserControllerTest {

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private MailSendService mailSendService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setup() {
        User user = User.builder()
                .id(1L)
                .providerType(ProviderType.GOOGLE)
                .userId("123456L")
                .email("kmw106933@naver.com")
                .nickname("Test")
                .phoneNum("01033333333")
                .birthday("01-03")
                .birthYear("2001")
                .gender(Gender.MAN)
                .timeEntity(new TimeEntity())
                .build();

        given(customUserDetailsService.loadUserByUsername(anyString())).willReturn(CustomUserDetails.create(user));

        given(userService.getUser()).willReturn(user);

        given(userService.update(any(UserUpdateRequest.class)))
                .will(invocation -> {
                    UserUpdateRequest userUpdateRequest = invocation.getArgument(0);
                    return User.builder()
                            .id(1L)
                            .providerType(ProviderType.GOOGLE)
                            .userId("123456L")
                            .email(userUpdateRequest.getEmail())
                            .nickname(userUpdateRequest.getNickname())
                            .phoneNum(userUpdateRequest.getPhoneNum())
                            .birthday("01-03")
                            .birthYear("2001")
                            .gender(userUpdateRequest.getGender())
                            .timeEntity(new TimeEntity())
                            .build();
                });

        given(userService.isDuplicateEmail("kmw106933@naver.com"))
                .willThrow(new CustomException(ErrorCode.ALREADY_EMAIL_EXISTS));

        given(userService.isDuplicateEmail("khj10693@naver.com"))
                .willReturn(false);

        given(mailSendService.sendAuthMail(any(String.class))).willReturn("123456");

        given(userService.findMyPosts(any(Pageable.class))).will(
                invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    Post post = new Post("test", "test", 0, user, null, new PostCategory("test", null));
                    List<Post> posts = new ArrayList<>();
                    posts.add(post);
                    return new PageImpl<>(posts, pageable, posts.size());

                }
        );

        given(userService.findMyComments(any(Pageable.class))).will(
                invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    Comment comment = new Comment("test", null, user, null);
                    List<Comment> comments = new ArrayList<>();
                    comments.add(comment);
                    return new PageImpl<>(comments, pageable, comments.size());
                }
        );

        given(userService.findMyScraps(any(Pageable.class))).will(
                invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    Scrap scrap = new Scrap(null, user);
                    List<Scrap> scraps = new ArrayList<>();
                    scraps.add(scrap);
                    return new PageImpl<>(scraps, pageable, scraps.size());
                }
        );

        given(mailSendService.getAuthToken(anyMap())).will(
                invocation -> {
                    Map<String, String> map = invocation.getArgument(0);
                    String email = map.get("email");
                    String authKey = map.get("authKey");

                    return new UserMailAuth(1L, 1L, email, authKey, LocalDateTime.now(), false);
                }
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode())));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);
    }

    @Test
    void 존재하는_사용자를_조회하는_경우() throws Exception {

        mockMvc.perform(
                        get("/users").
                                header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"id\":1")
                ))
                .andExpect(content().string(
                        containsString("\"nickname\":\"Test\"")
                ));

        verify(userService).getUser();
    }

    @Test
    void 올바른_정보로_수정하려는_경우() throws Exception {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .email("kmw106933@naver.com")
                .nickname("test12")
                .phoneNum("01033333333")
                .gender(Gender.MAN)
                .build();

        String json = new ObjectMapper().writeValueAsString(userUpdateRequest);

        mockMvc.perform(
                        patch("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"id\":1")
                ))
                .andExpect(content().string(
                        containsString("\"email\":\"kmw106933@naver.com\"")
                ))
                .andExpect(content().string(
                        containsString("\"nickname\":\"test12\"")
                ))
                .andExpect(content().string(
                        containsString("\"phoneNum\":\"01033333333\"")
                ))
                .andExpect(content().string(
                        containsString("\"gender\":\"MAN\"")
                ))
                .andDo(print());

        verify(userService).update(any(UserUpdateRequest.class));
    }

    @Test
    void 올바르지_않은_정보로_수정하려는_경우() throws Exception {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .email("")
                .nickname("")
                .phoneNum("")
                .gender(Gender.MAN)
                .build();

        String json = new ObjectMapper().writeValueAsString(userUpdateRequest);

        mockMvc.perform(
                        patch("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void 올바른_정보로_이메일을_인증하는_경우() throws Exception {
        UserEmailRequest userEmailRequest = UserEmailRequest.builder()
                .email("khj10693@naver.com")
                .build();

        String json = new ObjectMapper().writeValueAsString(userEmailRequest);

        mockMvc.perform(
                        post("/users/registerEmail")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isOk());
        verify(mailSendService).save("khj10693@naver.com", "123456");
    }

    @Test
    void 존재하는_이메일을_확인하려는_경우() throws Exception {
        mockMvc.perform(
                        get("/users/emailCheck?email=kmw106933@naver.com")
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isAlreadyReported())
                .andDo(print());
        verify(userService).isDuplicateEmail("kmw106933@naver.com");
    }

    @Test
    void 존재하지_않은_이메일을_확인하려는_경우() throws Exception {
        mockMvc.perform(
                        get("/users/emailCheck?email=khj10693@naver.com")
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("false")
                ))
                .andDo(print());
        verify(userService).isDuplicateEmail("khj10693@naver.com");
    }

    @Test
    void 내가_쓴글을_정상적으로_조회한_경우() throws Exception {
        mockMvc.perform(
                        get("/users/mypage/posts")
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"title\":\"test\"")
                ))
                .andExpect(content().string(
                        containsString("\"content\":\"test\"")
                ))
                .andDo(print());

        verify(userService).findMyPosts(any(Pageable.class));
    }

    @Test
    void 내가_쓴_댓글을_정상적으로_조회한_경우() throws Exception {
        mockMvc.perform(
                        get("/users/mypage/comments")
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"content\":\"test\"")
                ))
                .andDo(print());

        verify(userService).findMyComments(any(Pageable.class));
    }

    @Test
    void 내가_한_스크랩을_정상적으로_조회한_경우() throws Exception {
        mockMvc.perform(
                        get("/users/mypage/scraps")
                                .header("Authorization", "Bearer (accessToken)")
                )
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findMyScraps(any(Pageable.class));
    }

    @Test
    void 정상적인_방법으로_이메일_인증_버튼을_누른_경우() throws Exception {
        mockMvc.perform(
                        get("/users/confirm?email=test.com&authkey=123456")
                )
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).confirmEmail(any(UserMailAuth.class));
    }

}

