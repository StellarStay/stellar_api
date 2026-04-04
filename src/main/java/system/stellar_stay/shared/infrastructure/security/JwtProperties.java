package system.stellar_stay.shared.infrastructure.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secretKey;

    private long accessTokenExpiration = 900;
    private long refreshTokenExpiration = 2592000;
}
