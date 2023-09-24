package com.tommeijer.javalib.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AccessTokenDto {
    private final String accessToken;
}
