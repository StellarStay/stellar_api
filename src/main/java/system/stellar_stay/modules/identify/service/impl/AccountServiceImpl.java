package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.repository.AccountRepository;
import system.stellar_stay.modules.identify.service.AccountService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

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
}
