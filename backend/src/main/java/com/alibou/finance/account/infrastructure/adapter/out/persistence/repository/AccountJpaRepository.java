package com.alibou.finance.account.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.AccountProjection;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.NumberAccountStatisticProj;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.projection.SoldeAccountStatisticProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    boolean existsByAccountNumber(String accountNumber);

    @EntityGraph(attributePaths = {"accountTypeEntity","currencyEntity"})
    Optional<AccountEntity>findByAccountNumber(String accountNumber);

    @EntityGraph(attributePaths = {"accountTypeEntity","currencyEntity"})
    Page<AccountEntity>findAllByAccountNumberStartsWith(String accountNumber, Pageable pageable);


    @Query("""
        Select ae.id as id, ae.balance as balance, ae.createdDate as createdDate, ae.lastModifiedDate as lastModifiedDate,
        ae.accountNumber as accountNumber, ae.accountStatus as status, ae.customerId as customerId, ae.accountTypeEntity.name as accountTypeName, ae.currencyEntity.code as currencyCode 
        from AccountEntity ae
        where ae.accountNumber like concat(:accountNumber, '%') order by ae.createdDate asc 
    """)
    Page<AccountProjection>getAllAccountByAccountNumberBegin(@Param("accountNumber") String accountNumber, Pageable pageable);


    @Query("""
        Select ae.id as id, ae.balance as balance, ae.createdDate as createdDate, ae.lastModifiedDate as lastModifiedDate,
        ae.accountNumber as accountNumber, ae.accountStatus as status, ae.customerId as customerId, 
        ae.accountTypeEntity.name as accountTypeName, ae.currencyEntity.code as currencyCode 
        from AccountEntity ae where ae.customerId =:customerId
    """)
    List<AccountProjection>getAllByCustomerId(@Param("customerId") UUID customerId);


    @Query("""
        Select ae.accountTypeEntity.name as accountType, count(ae.id) as nbrAccountByType from AccountEntity ae 
        where ae.accountStatus != :accountStatus group by ae.accountTypeEntity.name
    """)
    List<NumberAccountStatisticProj>getStatisticNumberOfAccountNoClosed(@Param("accountStatus") AccountStatusEnum accountStatus);

    @Query("""
        Select ae.accountTypeEntity.name as accountType, sum(ae.mgaBalance) as soldeAccountByType from AccountEntity ae 
        where ae.accountStatus != :accountStatus group by ae.accountTypeEntity.name
    """)
    List<SoldeAccountStatisticProj>getAccountStatisticSoldNoClosed(@Param("accountStatus") AccountStatusEnum accountStatus);

    @Query("""
        select sum(ae.mgaBalance) from AccountEntity ae where ae.accountStatus != :accountStatus
    """)
    BigDecimal getSoldTotalOfAccountNoClosed(@Param("accountStatus") AccountStatusEnum accountStatus);

    @Query("""
        select count(ae.id) from AccountEntity ae where ae.accountStatus != :accountStatus
    """)
    Long getNbrTotalOfAccountNoClosed(@Param("accountStatus") AccountStatusEnum accountStatus);

    @EntityGraph(attributePaths = {"accountTypeEntity","currencyEntity"})
    @Query("""
        Select ae from AccountEntity ae 
        where ae.accountTypeEntity.code in :codes and ae.accountStatus != :status
    """)
    Page<AccountEntity>getAllByAccountTypeEntityCodeIn(@Param("codes") Set<String>codes,@Param("status")AccountStatusEnum status, Pageable pageable);



}
