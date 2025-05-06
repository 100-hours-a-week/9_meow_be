package meow_be.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private String secretKey;
    private final long expirationTime = 86400000L; // 1 day in milliseconds

    // JWT 생성
    public String generateToken(Integer userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT에서 userId 추출
    public Integer extractUserId(String token) {
        return Integer.parseInt(
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    // JWT가 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expirationDate.before(new Date());
    }

    // JWT가 유효한지 검사
    public boolean validateToken(String token, Integer userId) {
        return (userId.toString().equals(extractUserId(token).toString()) && !isTokenExpired(token));
    }
}
