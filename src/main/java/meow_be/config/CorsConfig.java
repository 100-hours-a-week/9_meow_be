package meow_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:",
                "http://localhost:5173",
                "https://localhost:",
                "https://localhost:5173",
                "http://127.0.0.1:",
                "https://127.0.0.1:",
                "http://www.meowng.com",
                "https://www.meowng.com",
                "https://ds36vr51hmfa7.cloudfront.net",
                "http://3.39.3.208"
        ));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(Boolean.TRUE);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
