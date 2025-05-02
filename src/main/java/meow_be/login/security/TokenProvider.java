package meow_be.login.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {



    private static final long TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24;  // 24시간
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);



    // JWT 토큰 생성
    public String createToken(Integer userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }
}
