package system.stellar_stay.modules.identify.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.identify.entity.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Account findByEmail(String email);

    Optional<Account> findById(UUID accountId);

    @Query(value = """
        SELECT a FROM Account a
        LEFT JOIN FETCH a.profile p
        WHERE (:keyword IS NULL
            OR LOWER(a.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
    """, countQuery = """
        SELECT count(a) FROM Account a
        LEFT JOIN a.profile p
        WHERE (:keyword IS NULL
            OR LOWER(a.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
    """)
    Page<Account> getAllAccount(@Param("keyword") String keyword, Pageable pageable);
}
