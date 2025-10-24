/*
package ma.ensa.apigw.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret = "01234567890123456789012345678901"; // 32 chars
    private String token;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Utilisation de Reflection pour injecter le secret
        try {
            var field = JwtUtil.class.getDeclaredField("jwtSecret");
            field.setAccessible(true);
            field.set(jwtUtil, secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        token = Jwts.builder()
                .setSubject("123")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void testValidateToken_Valid() {
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_Invalid() {
        assertFalse(jwtUtil.validateToken(token + "invalid"));
    }

    @Test
    void testGetUserIdFromToken() {
        assertEquals("123", jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testGetRoleFromToken() {
        assertEquals("USER", jwtUtil.getRoleFromToken(token));
    }
}



 */