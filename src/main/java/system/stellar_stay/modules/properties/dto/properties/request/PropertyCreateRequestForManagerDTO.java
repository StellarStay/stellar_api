package system.stellar_stay.modules.properties.dto.properties.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import system.stellar_stay.modules.properties.enums.PropertiesType;
import java.math.BigDecimal;

public record PropertyCreateRequestForManagerDTO(
        @NotBlank(message = "Tên property không được để trống")
        String name,

        @NotBlank(message = "Slug không được để trống")
        String slug,

        @NotNull(message = "Loại property không được để trống")
        PropertiesType type,

        String description,

        @NotBlank(message = "Địa chỉ chi tiết không được để trống")
        String address,

        @NotBlank(message = "Thành phố không được để trống")
        String city,

        @NotBlank(message = "Quận/Huyện không được để trống")
        String district,

        @NotBlank(message = "Phường/Xã không được để trống")
        String ward,

        @NotNull(message = "Vĩ độ không được để trống")
        BigDecimal latitude,

        @NotNull(message = "Kinh độ không được để trống")
        BigDecimal longitude,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
        String phone,

        @NotBlank(message = "Email không được để trống")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email không đúng định dạng")
        String email
) {
}
