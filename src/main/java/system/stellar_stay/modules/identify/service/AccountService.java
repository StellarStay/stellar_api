package system.stellar_stay.modules.identify.service;

import system.stellar_stay.modules.identify.dto.accounts.request.CreateAccountForAdminRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.RegisterAccountRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForAdmin;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForUser;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForAdminResponse;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForUserResponse;
import system.stellar_stay.modules.identify.entity.Account;

import java.util.UUID;

public interface AccountService {
    Account getAccountByAccountIdWithReference(UUID accountId);

//    Service for customer
    AccountForUserResponse registerAccount(RegisterAccountRequest registerAccountRequest);

    AccountForUserResponse updateAccount(UUID accountId, UpdateAccountRequestForUser updateAccountRequestForUser);

    void deleteAccount(UUID accountId);


//    Service for admin

//    AccountForAdminResponse registerAccountForAdmin(CreateAccountForAdminRequest createAccountForAdminRequest);
//
//    AccountForAdminResponse updateAccountForAdmin(UUID accountId, UpdateAccountRequestForAdmin updateAccountRequestForAdmin);
//
//    void deleteAccountForAdmin(UUID accountId);
}
