package system.stellar_stay.modules.identify.mapper;

import org.mapstruct.*;
import system.stellar_stay.modules.identify.dto.accounts.request.RegisterAccountRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForUser;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForUserResponse;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.entity.Profile;

import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
// component ở đây là spring để mapstruct có thể tự động tạo bean cho mapper này và inject vào các service cần sử dụng
// unmappedTargetPolicy = ReportingPolicy.IGNORE để tránh việc báo lỗi khi có trường nào đó trong DTO mà không có trường tương ứng trong entity hoặc ngược lại.
// nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE để khi update, nếu có trường nào đó trong DTO là null thì nó sẽ không cập nhật trường đó trong entity, mà giữ nguyên giá trị cũ.
public interface AccountMapper {

    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "idCardNumber", source = "profile.idCardNumber")
    @Mapping(target = "fullName", source = "profile.fullName")
    @Mapping(target = "phoneNumber", source = "profile.phoneNumber")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    @Mapping(target = "gender", source = "profile.gender")
    @Mapping(target = "birthDate", source = "profile.birthDate")
    @Mapping(target = "loyaltyPoints", source = "profile.loyaltyPoints")
    AccountForUserResponse toAccountResponseForUser(Account account, Profile profile);


    @Mapping(target = "email", source = "registerAccountRequest.email")
    @Mapping(target = "password", source = "registerAccountRequest.password")
    Account toAccountEntity(RegisterAccountRequest registerAccountRequest);

    @Mapping(target = "idCardNumber", source = "registerAccountRequest.idCardNumber")
    @Mapping(target = "fullName", source = "registerAccountRequest.fullName")
    @Mapping(target = "phoneNumber", source = "registerAccountRequest.phoneNumber")
    @Mapping(target = "avatarUrl", source = "registerAccountRequest.avatarUrl") // Avatar sẽ do người dùng
    @Mapping(target = "gender", source = "registerAccountRequest.gender")
    @Mapping(target = "birthDate", source = "registerAccountRequest.birthDate")
    @Mapping(target = "loyaltyPoints", ignore = true)
    Profile toProfileEntity(RegisterAccountRequest registerAccountRequest);


    @Mapping(target = "email", source = "updateAccountRequestForUser.email")
    void updateAccountFromRequest(UpdateAccountRequestForUser updateAccountRequestForUser, @MappingTarget Account account);

    @Mapping(target = "idCardNumber", source = "updateAccountRequestForUser.idCardNumber")
    @Mapping(target = "fullName", source = "updateAccountRequestForUser.fullName")
    @Mapping(target = "phoneNumber", source = "updateAccountRequestForUser.phoneNumber")
    @Mapping(target = "avatarUrl", source = "updateAccountRequestForUser.avatarUrl")
    @Mapping(target = "gender", source = "updateAccountRequestForUser.gender")
    @Mapping(target = "birthDate", source = "updateAccountRequestForUser.birthDate")
    void updateProfileFromRequest(UpdateAccountRequestForUser updateAccountRequestForUser, @MappingTarget Profile profile);

}
