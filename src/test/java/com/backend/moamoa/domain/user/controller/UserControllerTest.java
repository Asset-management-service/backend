package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.entity.User;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.mockito.BDDMockito.given;
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

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test", "1234", Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode())));
        given(jwtProvider.getAuthentication(anyString())).willReturn(authentication);

    }

    @Test
    @WithMockUser(roles = "USER")
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

}
