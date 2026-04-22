package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import system.stellar_stay.modules.identify.dto.accounts.request.CreateAccountForAdminRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.RegisterAccountRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForAdmin;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForUser;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForAdminResponse;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForUserResponse;
import system.stellar_stay.modules.identify.dto.roles.response.RoleInformationResponseDTO;
import system.stellar_stay.modules.identify.entity.*;
import system.stellar_stay.modules.identify.enums.AccountStatus;
import system.stellar_stay.modules.identify.enums.OTPType;
import system.stellar_stay.modules.identify.mapper.AccountMapper;
import system.stellar_stay.modules.identify.mapper.RoleMapper;
import system.stellar_stay.modules.identify.repository.AccountRepository;
import system.stellar_stay.modules.identify.repository.AccountRoleRepository;
import system.stellar_stay.modules.identify.repository.ProfileRepository;
import system.stellar_stay.modules.identify.service.*;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final RoleMapper roleMapper;
    private final PermissionService permissionService;
    private final RefreshTokenService refreshTokenService;
    private final OTPService otpService;


    @Override
    @Transactional
    public AccountForUserResponse registerAccount(RegisterAccountRequest registerAccountRequest) {
        if (registerAccountRequest == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Register account request cannot be null");
        }
        Account account = accountMapper.toAccountEntityForUser(registerAccountRequest);
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

        // Gửi OTP để xác thực email
        otpService.generateAndSendOTP(registerAccountRequest.email(), OTPType.REGISTERED);

        return accountMapper.toAccountResponseForUser(savedAccount, savedProfile);
    }

    @Override
    public void verifyRegisterAccount(String email, String otp) {
        if (email == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Email cannot be null");
        }
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found with email: " + email);
        }
        otpService.verifyOTP(email, otp, OTPType.REGISTERED);
        account.setEmailVerified(true);
        account.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }


    @Override
    @Transactional
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
    @Transactional
    public void deleteAccount(UUID accountID) {
        if (accountID == null) {
            throw  new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        Account accountFound = accountRepository.findById(accountID)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));
        accountFound.setAccountStatus(AccountStatus.INACTIVE);
        accountRepository.save(accountFound);
    }

    @Override
    @Transactional
    public AccountForAdminResponse createAccountForAdmin(CreateAccountForAdminRequest createAccountForAdminRequest) {
        if (createAccountForAdminRequest == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Create account request cannot be null");
        }
        Account account = accountMapper.toAccountEntityForAdmin(createAccountForAdminRequest);
        account.setEmail(account.getEmail().toLowerCase());
        account.setPassword(passwordEncoder.encode(createAccountForAdminRequest.password()));
        Account savedAccount = accountRepository.save(account);

        Profile profile = accountMapper.toProfileEntityForAdmin(createAccountForAdminRequest);
        profile.setAccount(savedAccount);
        Profile savedProfile = profileRepository.save(profile);

        // Giờ thì gắn role cho account
        // Và từ role sẽ lấy ra permission để trả về cho admin
        Set<Role> roles = roleService.findRoleByRoleIds(createAccountForAdminRequest.roleIds());
//        // Gắn role cho account
        for (Role role : roles) {
            AccountRole accountRole = new AccountRole();
            accountRole.setAccount(savedAccount);
            accountRole.setRole(role);
            accountRoleRepository.save(accountRole);
        }
            // Từ role entity, map sang RoleInformationResponseDTO để trả về cho admin
        Set<RoleInformationResponseDTO> roleInformationResponseDTOS = new HashSet<>();
        for (Role role : roles) {
            RoleInformationResponseDTO roleInformationResponseDTO = roleMapper.toRoleInformationResponse(role);
            roleInformationResponseDTOS.add(roleInformationResponseDTO);
        }
        Set<String> permissions = permissionService.getPermissionsByRolesId(roleService.findAllRolesIdsFromSetRole(roles));

        AccountForAdminResponse accountForAdminResponse = accountMapper.toAccountResponseForAdmin(savedAccount, savedProfile);
        accountForAdminResponse.setRoles(roleInformationResponseDTOS);
        accountForAdminResponse.setPermissions(permissions);
        return accountForAdminResponse;
    }

    @Override
    @Transactional
    public AccountForAdminResponse updateAccountForAdmin(UUID accountId, UpdateAccountRequestForAdmin updateAccountRequestForAdmin) {
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID and update request cannot be null");
        }
        // Update tài khoản cho user thì cứ làm như lúc update dành cho user thôi
        // Sau đó có update role và permission thì cập nhật thêm
            // Cập nhật role thì xóa hết role của người đó đi rồi gắn lại role mới vào
        Account accountFound = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));
        accountMapper.updateAccountFromAdminRequest(updateAccountRequestForAdmin, accountFound);
        accountRepository.save(accountFound);

        Profile profileFound = accountFound.getProfile();
        if (profileFound == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Profile not found for the account");
        }
        accountMapper.updateProfileFromAdminRequest(updateAccountRequestForAdmin, profileFound);
        profileRepository.save(profileFound);

        Set<RoleInformationResponseDTO> rolesSet = new HashSet<>();
        Set<String> permissions = new HashSet<>();


        if(updateAccountRequestForAdmin.roleIds() != null && !updateAccountRequestForAdmin.roleIds().isEmpty()) {
            // Xóa role của accountId đó theo accountId trước
            accountRoleRepository.deleteAccountRoleByAccountId(accountFound.getId());

            // Gắn lại role mới vào
                // Lấy ra set role mới từ roleId mà req gửi lên
            Set<Role> newSetRoles = roleService.findRoleByRoleIds(updateAccountRequestForAdmin.roleIds());
                // Gắn role mới vào account
            Set<AccountRole> accountRoles = newSetRoles.stream()
                    .map(role -> {
                        AccountRole accountRole = new AccountRole();
                        accountRole.setAccount(accountFound);
                        accountRole.setRole(role);
                        return accountRole;
                    })
                    .collect(Collectors.toSet());
            accountRoleRepository.saveAll(accountRoles);

            // Xóa cache permission cũ của account đó đi để khi có request mới thì nó sẽ lấy permission mới dựa trên role mới
            permissionService.invalidatePermissions(accountFound.getId());
            refreshTokenService.revokeAllRefreshToken(accountFound.getId());

            // Từ role entity, map sang RoleInformationResponseDTO để trả về cho admin
                // Map từ set role entity sang set RoleInformationResponseDTO
            rolesSet = newSetRoles.stream()
                    .map(roleMapper :: toRoleInformationResponse)
                    .collect(Collectors.toSet());
                // Từ set role mới này, lấy ra set permission để trả về cho admin
            permissions = permissionService.getPermissionsByRolesId(roleService.findAllRolesIdsFromSetRole(newSetRoles));
        }

        else{
            // Nếu không có gì thay đổi thì cứ lấy role entity của account đó
            Set<Role> existingRole = roleService.findEntityRolesByAccountIdForAdminGetEntity(accountFound.getId());
            // Map từ role entity vừa lấy lên đó sang RoleInformationResponseDTO để trả về cho admin
            rolesSet = existingRole.stream()
                    .map(roleMapper :: toRoleInformationResponse)
                    .collect(Collectors.toSet());
            // Từ set role entity vừa lấy lên đó, lấy ra set permission để trả về cho admin
            permissions = permissionService.getPermissionsForAccount(accountFound.getId());

        }
        AccountForAdminResponse accountForAdminResponse = accountMapper.toAccountResponseForAdmin(accountFound, profileFound);
        accountForAdminResponse.setRoles(rolesSet);
        accountForAdminResponse.setPermissions(permissions);
        return accountForAdminResponse;
    }

    @Override
    @Transactional
    public void inActiveAccountForAdmin(UUID accountId) {
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        Account accountFound = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));
        accountFound.setAccountStatus(AccountStatus.INACTIVE);

        accountRepository.save(accountFound);

        permissionService.invalidatePermissions(accountFound.getId());
        refreshTokenService.revokeAllRefreshToken(accountFound.getId());

    }

    @Override
    @Transactional
    public void banAccountForAdmin(UUID accountId) {
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        Account accountFound = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));
        accountFound.setAccountStatus(AccountStatus.BANNED);
        accountRepository.save(accountFound);

        permissionService.invalidatePermissions(accountFound.getId());
        refreshTokenService.revokeAllRefreshToken(accountFound.getId());
    }

    @Override
    public Page<AccountForAdminResponse> getAllAccountsForAdmin(int page, int size, String sortBy, String sortDir, String keyword) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Account> accountPage = accountRepository.getAllAccount(keyword, pageable);

        Page<AccountForAdminResponse> responses = accountPage.map(account -> {
            // Lấy role và permission của account đó để trả về cho admin

            Set<Role> roles = roleService.findEntityRolesByAccountIdForAdminGetEntity(account.getId());
            // map từ role entity sang RoleInformationResponseDTO để trả về cho admin
            Set<RoleInformationResponseDTO> roleInformationResponseDTOS = roles.stream()
                    .map(roleMapper::toRoleInformationResponse)
                    .collect(Collectors.toSet());
            Set<String> permissions = permissionService.getPermissionsForAccount(account.getId());

            AccountForAdminResponse accountForAdminResponse = accountMapper.toAccountResponseForAdmin(account, account.getProfile());
            accountForAdminResponse.setRoles(roleInformationResponseDTOS);
            accountForAdminResponse.setPermissions(permissions);

            return accountForAdminResponse;
        });
        return responses;
    }

}
