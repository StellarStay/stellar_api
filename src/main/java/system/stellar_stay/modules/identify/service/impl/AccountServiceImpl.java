package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.dto.accounts.request.CreateAccountForAdminRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.RegisterAccountRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForAdmin;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForUser;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForAdminResponse;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForUserResponse;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.entity.AccountRole;
import system.stellar_stay.modules.identify.entity.Profile;
import system.stellar_stay.modules.identify.entity.Role;
import system.stellar_stay.modules.identify.enums.AccountStatus;
import system.stellar_stay.modules.identify.mapper.AccountMapper;
import system.stellar_stay.modules.identify.repository.AccountRepository;
import system.stellar_stay.modules.identify.repository.AccountRoleRepository;
import system.stellar_stay.modules.identify.repository.ProfileRepository;
import system.stellar_stay.modules.identify.service.AccountService;
import system.stellar_stay.modules.identify.service.RoleService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleService roleService;

    @Override
    public Account getAccountByAccountIdWithReference(UUID accountId) {
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        return accountRepository.getReferenceById(accountId);
        // Thay vì dùng findById, chúng ta dùng getReferenceById để tránh việc truy vấn dữ liệu ngay lập tức từ database.
        // Điều này giúp tối ưu hiệu suất khi chúng ta chỉ cần tham chiếu đến đối tượng mà không cần lấy toàn bộ dữ liệu của nó.
        // Tức nó sẽ trả ra một proxy object, và chỉ khi nào chúng ta thực sự truy cập vào các thuộc tính của đối tượng đó thì nó mới thực hiện truy vấn để lấy dữ liệu từ database.
    }

    @Override
    public AccountForUserResponse registerAccount(RegisterAccountRequest registerAccountRequest) {
        if (registerAccountRequest == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Register account request cannot be null");
        }
        Account account = accountMapper.toAccountEntity(registerAccountRequest);
        account.setEmail(account.getEmail().toLowerCase());
        account.setPassword(passwordEncoder.encode(registerAccountRequest.password()));
        account.setEmailVerified(false);
        account.setAccountStatus(AccountStatus.INACTIVE);
        Account savedAccount = accountRepository.save(account);

        Profile profile = accountMapper.toProfileEntity(registerAccountRequest);
        profile.setAccount(savedAccount);
        profile.setLoyaltyPoints(0);
        Profile savedProfile = profileRepository.save(profile);


        AccountRole accountRole = new AccountRole();
        Role role = roleService.findRoleByCode("CUSTOMER");
        accountRole.setRole(role);
        accountRole.setAccount(savedAccount);
        accountRoleRepository.save(accountRole);


        return accountMapper.toAccountResponseForUser(savedAccount, savedProfile);
    }

    @Override
    public AccountForUserResponse updateAccount(UUID accountId, UpdateAccountRequestForUser updateAccountRequestForUser) {
        if (accountId == null || updateAccountRequestForUser == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID and update request cannot be null");
        }
        Account accountFound = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));

        Profile profileFound = accountFound.getProfile();
        if (profileFound == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Profile not found for the account");
        }

        accountMapper.updateAccountFromRequest(updateAccountRequestForUser, accountFound);
        accountMapper.updateProfileFromRequest(updateAccountRequestForUser, profileFound);

        Account updatedAccount = accountRepository.save(accountFound);
        Profile updatedProfile = profileRepository.save(profileFound);

        return accountMapper.toAccountResponseForUser(updatedAccount, updatedProfile);
    }

    @Override
    public void deleteAccount(UUID accountID) {
        if (accountID == null) {
            throw  new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        Account accountFound = accountRepository.findById(accountID)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));
        accountFound.setAccountStatus(AccountStatus.INACTIVE);
        accountRepository.save(accountFound);
    }

//    @Override
//    public AccountForAdminResponse registerAccountForAdmin(CreateAccountForAdminRequest createAccountForAdminRequest) {
//
//    }
//
//    @Override
//    public AccountForAdminResponse updateAccountForAdmin(UUID accountID, UpdateAccountRequestForAdmin updateAccountRequestForAdmin) {
//
//    }
//
//    @Override
//    public void deleteAccountForAdmin(UUID accountID) {
//
//    }
}
