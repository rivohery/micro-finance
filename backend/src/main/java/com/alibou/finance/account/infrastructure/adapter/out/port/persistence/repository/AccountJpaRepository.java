package com.alibou.finance.account.infrastructure.adapter.out.port.persistence.repository;

import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.AccountProjection;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.port.persistence.projection.SoldeAccountStatisticProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<AccountEntity>findById(UUID accountId);

    Optional<AccountEntity>findByAccountNumber(String accountNumber);


    @Query("""
        Select ae.id as id, ae.balance as balance, ae.createdDate as createdDate, ae.lastModifiedDate as lastModifiedDate,
        ae.accountNumber as accountNumber, ae.accountStatus as status, ae.customerId as customerId, 
        ae.accountTypeName as accountTypeName, ae.currencyCode as currencyCode 
        from AccountEntity ae
        where ae.accountNumber like concat(:accountNumber, '%') AND ae.accountStatus <> 'CLOSED'
        order by ae.createdDate asc 
    """)
    Page<AccountProjection>getAllAccountByAccountNumberBegin(@Param("accountNumber") String accountNumber, Pageable pageable);


    @Query("""
        Select ae.id as id, ae.balance as balance, ae.createdDate as createdDate, ae.lastModifiedDate as lastModifiedDate,
        ae.accountNumber as accountNumber, ae.accountStatus as status, ae.customerId as customerId,
        ae.accountTypeName as accountTypeName, ae.currencyCode as currencyCode 
        from AccountEntity ae where ae.customerId =:customerId
    """)
    List<AccountProjection>getAllByCustomerId(@Param("customerId") UUID customerId);


    @Query("""
        Select ae.accountTypeName as accountType, count(ae.id) as nbrAccountByType from AccountEntity ae 
        where ae.accountStatus != 'CLOSED' group by ae.accountTypeName
    """)
    List<NumberAccountStatisticProj>getStatisticNumberOfAccountNoClosed();

    @Query("""
        Select ae.accountTypeName as accountType, sum(ae.mgaBalance) as soldeAccountByType from AccountEntity ae 
        where ae.accountStatus != 'CLOSED' group by ae.accountTypeName
    """)
    List<SoldeAccountStatisticProj>getAccountStatisticSoldNoClosed();

    @Query("""
        select sum(ae.mgaBalance) from AccountEntity ae where ae.accountStatus != 'CLOSED'
    """)
    BigDecimal getSoldTotalOfAccountNoClosed();

    @Query("""
        select count(ae.id) from AccountEntity ae where ae.accountStatus != 'CLOSED'
    """)
    Long getNbrTotalOfAccountNoClosed();

    @Query("""  
        Select ae from AccountEntity ae where ae.accountTypeId = :accountTypeId and ae.accountStatus <> 'CLOSED'
    """)
    List<AccountEntity>getAccountsNoClosedByTypeId(UUID accountTypeId);

}
