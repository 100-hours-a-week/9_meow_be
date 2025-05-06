package meow_be.login.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 15;  // 15분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;  // 7일
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

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
                .signWith(secretKey)
                .compact();
    }

    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject());
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
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
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
