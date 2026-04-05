package system.stellar_stay.modules.identify.service;

import system.stellar_stay.modules.identify.entity.Account;

import java.util.UUID;

public interface AccountService {
    Account getAccountByAccountIdWithReference(UUID accountId);
}
