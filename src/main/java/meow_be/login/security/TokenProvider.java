package meow_be.login.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class TokenProvider {
    private final SecretKey key;
    public TokenProvider(@Value("${spring.jwt.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 30;  //10분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;  // 7일

    public String createAccessToken(Integer userId) {
        return generateToken(userId, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String createRefreshToken(Integer userId) {
        return generateToken(userId, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String generateToken(Integer userId, long expirationTime) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject());
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, Integer userId) {
        try {
            return userId.toString().equals(getUserIdFromToken(token).toString()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public String extractTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null; // 토큰이 없거나 "Bearer "로 시작하지 않으면 null 반환
        }
        return token.substring(7); // "Bearer " 부분 제거하고 반환
    }
}
