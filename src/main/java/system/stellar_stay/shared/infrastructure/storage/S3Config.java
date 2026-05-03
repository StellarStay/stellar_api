package system.stellar_stay.shared.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        // S3 Client là đối tượng chính để tương tác với dịch vụ S3,
        // cho phép bạn thực hiện các thao tác như tải lên, tải xuống, xóa và quản lý các đối tượng trong bucket của bạn.
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion())) // set region cho S3 Client
                .credentialsProvider(
        // .credentialsProvider() được sử dụng để cung cấp thông tin xác thực (credentials) cho S3 Client,
                        StaticCredentialsProvider.create(
        // StaticCredentialsProvider là một lớp cung cấp thông tin xác thực tĩnh, nghĩa là bạn sẽ cung cấp trực tiếp access key và secret key của mình.
                                AwsBasicCredentials.create(
        // AwsBasicCredentials.create() được sử dụng để tạo một đối tượng AwsBasicCredentials chứa access key và secret key của bạn,
        // và nó dùng để cung cấp thông tin xác thực cho S3 Client thông qua StaticCredentialsProvider.
                                        s3Properties.getAccessKey(),
                                        s3Properties.getSecretKey()
                                )
                        )
                )
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        // Thằng S3Presigner là một đối tượng được sử dụng để tạo các URL có chữ ký (presigned URLs)
        // cho các đối tượng client có thể sử dụng để truy cập vào các đối tượng trong bucket của bạn
        // mà không cần phải cung cấp thông tin xác thực trực tiếp.
        return S3Presigner.builder()
                // tương tự như S3 Client, bạn cũng cần chỉ định region cho S3Presigner
                // để đảm bảo rằng nó hoạt động trong cùng một khu vực với bucket của bạn.
                // Và xuống dưới cũng set những thông tin liên quan
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Properties.getAccessKey(),
                                        s3Properties.getSecretKey()
                                )
                        )
                )
                .build();
    }
}
