package com.tommeijer.javalib.security;

import com.tommeijer.javalib.security.model.AuthParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService accessTokenService;
    @Mock
    private TokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, accessTokenService, refreshTokenService);
    }

    @Test
    void authenticate_ValidUser_ShouldGenerateTokens() {
        var params = new AuthParams("email", "password");
        var user = new User("username", "password", List.of());
        var auth = new TestingAuthenticationToken(user, null);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(params.getEmail(), params.getPassword())))
                .thenReturn(auth);
        var accessToken = "accessToken";
        when(accessTokenService.create(user.getUsername())).thenReturn(accessToken);
        var refreshToken = "refreshToken";
        when(refreshTokenService.create(user.getUsername())).thenReturn(refreshToken);
        var result = authService.authenticate(params);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
    }

    @Test
    void refreshAccessToken_ValidToken_ShouldReturnNewTokens() {
        var refreshToken = "refreshToken";
        Map<String, Object> claims = Map.of("sub", "user1");
        when(refreshTokenService.validate(refreshToken)).thenReturn(claims);
        var accessToken = "accessToken";
        when(accessTokenService.create((String) claims.get("sub"))).thenReturn(accessToken);
        var newRefreshToken = "newRefreshToken";
        when(refreshTokenService.create((String) claims.get("sub"))).thenReturn(newRefreshToken);
        var result = authService.refreshAccessToken(refreshToken);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(newRefreshToken, result.getRefreshToken());
    }
}
