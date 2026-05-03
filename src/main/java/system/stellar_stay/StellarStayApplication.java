package system.stellar_stay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import system.stellar_stay.shared.infrastructure.security.JwtProperties;
import system.stellar_stay.shared.infrastructure.storage.S3Properties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({JwtProperties.class, S3Properties.class})
public class StellarStayApplication {

    public static void main(String[] args) {
        SpringApplication.run(StellarStayApplication.class, args);
    }

}
