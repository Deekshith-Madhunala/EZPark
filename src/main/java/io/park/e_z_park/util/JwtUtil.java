package io.park.e_z_park.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "9a133910c7d92e878d1cf276a6e55b75445d3a10e9e15fbf99cdb61cf7eb18423c8675041d05a402c414daaae2d7d99cd35bb610a127aac9"; // keep secret!

    public static String generateToken(
            String id,
            String username,
            String email,
            String phoneNumber,
            String role) {

        long expirationTimeMs = 1000 * 60 * 60; // 1 hour

        return Jwts.builder()
                .claim("id", id)
                .claim("username", username)
                .claim("email", email)
                .claim("phoneNumber", phoneNumber)
                .claim("role", role)
//                .claim("vehicles", vehicles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
