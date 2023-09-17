package com.tommeijer.javalib.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
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
        secret = "verySecret";
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
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(jwt)
                .getBody();
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
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    @Test
    void validate_invalidToken_throwException() {
        var username = "user1";
        var jwt = create(username, "invalidSecret");
        assertThrows(SignatureException.class, () -> jwtService.validate(jwt));
    }
}
