package com.tommeijer.javalib.security;

import java.util.Map;

public interface TokenService {

    String create(String subject, Map<String, Object> claims);

    String create(String subject);

    Map<String, Object> validate(String token);
}
