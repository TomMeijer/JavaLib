package com.tommeijer.javalib.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedDto {
    private final String accessToken;
    private final String refreshToken;
}
