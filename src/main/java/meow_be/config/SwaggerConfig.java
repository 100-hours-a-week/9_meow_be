package meow_be.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "meowng API 명세서",
                description = "meowng api 명세서 실시간 최신 버전",
                version = "v1"
        )
)
public class SwaggerConfig {
    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    public OpenAPI openAPI() {
        String securityJwtName = "JWT";

        Server localServer = new Server()
                .url("http://3.39.3.208/api")
                .description("개발 서버");

        Server prodServer = new Server()
                .url("https://www.meowng.com/api")
                .description("운영 서버");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);
        Components components = new Components()
                .addSecuritySchemes(securityJwtName, new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX)
                        .bearerFormat("JWT")
                );

        return new OpenAPI()
                .addServersItem(localServer)
                .addServersItem(prodServer)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
