package com.tommeijer.javalib.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public class JwtService implements TokenService {
	private final String secret;
	private final long expirationMillis;

	@Override
	public String create(String subject, Map<String, Object> claims) {
		var now = new Date();
		return Jwts.builder()
				.subject(subject)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMillis))
				.claims(claims)
				.signWith(Keys.hmacShaKeyFor(secret.getBytes()))
				.compact();
	}

	@Override
	public String create(String subject) {
		return create(subject, Collections.emptyMap());
	}

	@Override
	public Map<String, Object> validate(String token) {
		return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
				.getPayload();
	}
}
