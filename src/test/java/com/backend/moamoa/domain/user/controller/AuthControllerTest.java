package com.backend.moamoa.domain.user.controller;

import com.backend.moamoa.domain.user.dto.response.ReissueResponse;
import com.backend.moamoa.domain.user.dto.response.TokenResponse;
import com.backend.moamoa.domain.user.oauth.filter.JwtFilter;
import com.backend.moamoa.domain.user.oauth.service.CustomUserDetailsService;
import com.backend.moamoa.domain.user.oauth.token.JwtProvider;
import com.backend.moamoa.domain.user.service.AuthService;
import com.backend.moamoa.global.bean.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
class AuthControllerTest {

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.KiNUK70RDCTWeRMqfN6YY_SAkkb8opFsAh_fwAntt4";
    private final String REFRESH_TOKEN = "eyJA45NAKSLFj9.eyJ1c2VySWQiOjF9.KiNUK70RDCTWeRMqfN6YY_SAkkb8opFsAh_fwAntt4";

    private final String VALID_ACCESS_TOKEN = "kyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.KiNUK70RDCTWeRMqfN6YY_SAkkb8opFsAh_fwAntt4";
    private final String VALID_REFRESH_TOKEN = "asyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.KiNUK70RDCTWeRMqfN6YY_SAkkb8opFsAh_fwAntt4";

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Test
    @DisplayName("올바른_정보로_엑세스,리프레쉬_토큰_재발급")
    void reissue() throws Exception {
        final Cookie refreshToken = new Cookie("refresh_token", REFRESH_TOKEN);

        given(authService.reissue(any(), any())).will(
                invocation -> {
                    HttpServletRequest request = invocation.getArgument(0);

                    String substring = request.getHeader(HEADER_AUTHORIZATION).substring(TOKEN_PREFIX.length());
                    assertThat(substring, is(equalTo(ACCESS_TOKEN)));

                    Cookie[] cookies = request.getCookies();
                    for (Cookie cookie : cookies) {
                        assertThat(cookie.getValue(), is(equalTo(REFRESH_TOKEN)));
                    }
                    return TokenResponse.builder()
                            .grantType("Bearer")
                            .accessToken(VALID_ACCESS_TOKEN)
                            .accessTokenExpireDate(30 * 60 * 1000L)
                            .refreshTokenExpireDate(7 * 30 * 60 * 1000L)
                            .build();
                }
        );
        mockMvc.perform(
                        get("/auth/reissue")
                                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                                .cookie(refreshToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value(VALID_ACCESS_TOKEN))
                .andExpect(jsonPath("$.accessTokenExpireDate").value(30 * 60 * 1000L))
                .andExpect(jsonPath("$.refreshTokenExpireDate").value(7 * 30 * 60 * 1000L))
                .andDo(print());

        verify(authService).reissue(any(), any());
    }

    @Test
    @DisplayName("정상적으로_로그아웃_한_경우")
    void logout() throws Exception {
        final Cookie refreshToken = new Cookie("refresh_token", REFRESH_TOKEN);

        given(authService.logout(any())).will(
                invocation -> {
                    HttpServletRequest request = invocation.getArgument(0);
                    String substring = request.getHeader(HEADER_AUTHORIZATION).substring(TOKEN_PREFIX.length());
                    assertThat(substring, is(equalTo(ACCESS_TOKEN)));

                    Cookie[] cookies = request.getCookies();
                    for (Cookie cookie : cookies) {
                        assertThat(cookie.getValue(), is(equalTo(REFRESH_TOKEN)));
                    }
                    return true;
                }
        );
        mockMvc.perform(
                        post("/auth/logout")
                                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                                .cookie(refreshToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("true"))
                .andDo(print());

        verify(authService).logout(any());
    }

}
