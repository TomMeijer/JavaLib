package com.tommeijer.javalib.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
				.setSubject(subject)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + expirationMillis))
				.addClaims(claims)
				.signWith(SignatureAlgorithm.HS256, secret.getBytes())
				.compact();
	}

	@Override
	public String create(String subject) {
		return create(subject, Collections.emptyMap());
	}

	@Override
	public Map<String, Object> validate(String token) {
		return Jwts.parser()
				.setSigningKey(secret.getBytes())
				.parseClaimsJws(token)
				.getBody();
	}
}
