package com.tommeijer.javalib.security;

import com.tommeijer.javalib.security.model.RefreshAccessTokenDto;
import com.tommeijer.javalib.security.model.AuthParams;
import com.tommeijer.javalib.security.model.AuthenticatedDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService accessTokenService;
    private final TokenService refreshTokenService;

    public AuthenticatedDto authenticate(AuthParams params) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                params.getEmail(),
                params.getPassword()
        ));
        var principal = (UserDetails) auth.getPrincipal();
        String accessToken = accessTokenService.create(principal.getUsername());
        String refreshToken = refreshTokenService.create(principal.getUsername());
        return new AuthenticatedDto(accessToken, refreshToken);
    }

    public RefreshAccessTokenDto refreshAccessToken(String refreshToken) {
        Map<String, Object> claims = refreshTokenService.validate(refreshToken);
        var subject = (String) claims.get(Claims.SUBJECT);
        String accessToken = accessTokenService.create(subject);
        String newRefreshToken = refreshTokenService.create(subject);
        return new RefreshAccessTokenDto(accessToken, newRefreshToken);
    }
}
