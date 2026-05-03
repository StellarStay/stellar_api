package system.stellar_stay.shared.infrastructure.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.aws.s3")
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private long presignedUrlExpiry; // seconds
}
