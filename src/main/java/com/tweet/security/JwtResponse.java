package com.tweet.security;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String role;
}
