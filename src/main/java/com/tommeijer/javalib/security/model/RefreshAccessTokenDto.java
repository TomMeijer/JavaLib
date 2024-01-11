package com.tommeijer.javalib.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RefreshAccessTokenDto {
    private final String accessToken;
    private final String refreshToken;
}
