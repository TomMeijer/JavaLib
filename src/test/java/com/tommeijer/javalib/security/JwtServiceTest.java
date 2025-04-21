package com.tommeijer.javalib.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {
    private String secret;
    private long expirationMillis;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        secret = "MFKvOO8qq24/njuH1XEtrIREa1KfW9HAHAtXUmOgP1LH2UQxSpS+EQMHs9wWTF02";
        expirationMillis = 3600_000;
        jwtService = new JwtService(secret, expirationMillis);
    }

    @Test
    void create_forUsername_returnToken() {
        var username = "user1";
        var jwt = jwtService.create(username);
        var claims = parse(jwt);
        assertEquals(username, claims.getSubject());
    }

    private Claims parse(String jwt) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    @Test
    void validate_validToken_returnClaims() {
        var username = "user1";
        var jwt = create(username, secret);
        var claims = (Claims) jwtService.validate(jwt);
        assertEquals(username, claims.getSubject());
    }

    public String create(String username, String secret) {
        return Jwts.builder()
                .subject(username)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    @Test
    void validate_invalidToken_throwException() {
        var username = "user1";
        var jwt = create(username, "kXFZvaXrMCY9N/dWb4N3FqrJK8jlEbkORWqQa2TzGbbPxV2zyp5/V9W3F0jmDHcQ");
        assertThrows(SignatureException.class, () -> jwtService.validate(jwt));
    }
}
