package system.stellar_stay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StellarStayApplication {

    public static void main(String[] args) {
        SpringApplication.run(StellarStayApplication.class, args);
    }

}
