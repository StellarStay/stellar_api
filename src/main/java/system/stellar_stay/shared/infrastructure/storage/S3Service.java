package system.stellar_stay.shared.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final S3Properties s3Properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // Thì flow upload ảnh:
    // User nhán upload ánh -> chọn ảnh -> Save -> FE sẽ gọi BE để xin presign key -> BE trả URL -> FE dùng put axios cùng url BE trả ra
    // để up ảnh lên S3 -> Upload thành công trả response -> FE gọi tiếp API BE kèm theo response vừa trả ra để lưu DB
    public String generatePresignedUrl(String fileKey, String contentType) {
        try{
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    // PutObjectPresignRequest.builder() được sử dụng để xây dựng một yêu cầu tạo URL có chữ ký (presigned URL)
                    // cho việc tải lên một đối tượng vào S3.
                    .signatureDuration(Duration.ofSeconds(s3Properties.getPresignedUrlExpiry()))
                    // signatureDuration() được sử dụng để chỉ định thời gian hết hạn của URL có chữ ký, tính bằng giây.
                    // Điều này xác định khoảng thời gian mà URL sẽ hợp lệ và có thể được sử dụng để tải lên đối tượng vào S3.
                    .putObjectRequest(
                            // Đoạn này là phần cấu hình chi tiết cho yêu cầu tải lên đối tượng vào S3,
                            //  bao gồm thông tin về bucket, key và content type của file.
                            request -> request
                            .bucket(s3Properties.getBucket())
                            .key(fileKey)
                            .contentType(contentType))
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        }
        catch (Exception e) {
            log.error("Error generating presigned URL for fileKey {}: {}", fileKey, e.getMessage());
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to generate presigned URL");
        }
    }

    // ── Generate presigned URL để FE xem ảnh ──────────────
    public String generatePresignedViewUrl(String fileKey) {
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(
                            s3Properties.getPresignedUrlExpiry()))
                    .getObjectRequest(req -> req
                            .bucket(s3Properties.getBucket())
                            .key(fileKey)
                    )
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner
                    .presignGetObject(presignRequest);

            return presignedRequest.url().toString();

        } catch (Exception e) {
            log.error("[S3] Failed to generate presigned view URL key={}", fileKey, e);
            throw new ApiException(ErrorCode.FAIL_TO_UPLOAD, "Failed to generate presigned view URL");
        }
    }

    // ── Xóa file trên S3 ───────────────────────────────────
    public void deleteFile(String fileKey) {
        try {
            s3Client.deleteObject(req -> req
                    .bucket(s3Properties.getBucket())
                    .key(fileKey)
            );
            log.info("[S3] Deleted file key={}", fileKey);

        } catch (Exception e) {
            log.error("[S3] Failed to delete file key={}", fileKey, e);
            throw new ApiException(ErrorCode.FAIL_TO_UPLOAD, "Failed to delete file from S3");
        }
    }

    // ── Helper generate file key ───────────────────────────
    // rooms/{roomId}/filename.webp
    // properties/{propertyId}/filename.webp
    public String generateRoomImageKey(UUID roomId, String fileName) {
        return "rooms/" + roomId + "/" + fileName;
    }

    public String generatePropertyImageKey(UUID propertyId, String fileName) {
        return "properties/" + propertyId + "/" + fileName;
    }
}
