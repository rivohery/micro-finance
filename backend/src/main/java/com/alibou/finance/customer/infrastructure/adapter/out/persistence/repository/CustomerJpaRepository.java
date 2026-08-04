package com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity>findByCin(String cin);
    boolean existsByCin(String cin);
    Page<CustomerEntity> findAllByFirstNameStartingWithIgnoreCaseOrLastNameStartingWithIgnoreCaseOrCinStartingWith(
            String firstName, String lastName, String cin, Pageable pageable
    );

    @EntityGraph(attributePaths = {"userEntity"})
    Optional<CustomerEntity>findById(UUID id);

    @Query("""
        select ce.id from CustomerEntity ce where ce.userEntity.id = :userId
    """)
    Optional<UUID>findIdByUserId(@Param("userId")UUID userId);

    @Query("""
        Select ce from CustomerEntity ce
        where ce.userEntity.enable = true and (
            lower(ce.firstName) like lower(concat(:search, '%')) 
            or lower(ce.lastName) like lower(concat(:search, '%')) 
            or ce.cin like concat(:search, '%')
        ) 
    """)
    Page<CustomerEntity>fetchAllEnableCustomerBySearchBegin(@Param("search")String search, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("""
        update CustomerEntity ce set ce.status = :status where ce.id = :id 
    """)
    int updateCustomerStatus(@Param("id") UUID id,@Param("status") CustomerStatus status);

    @Query("""
        select count(c.id) from CustomerEntity c where c.status != :status
    """)
    Long getNbrTotalOfCustomerNoClosed(@Param("status") CustomerStatus status);

    @Query("""
        SELECT c.createdDate AS createdDate, 
               COUNT(c.id) AS nbrCustomer 
        FROM CustomerEntity c 
        WHERE c.createdDate BETWEEN :startWeek AND :endWeek 
        GROUP BY c.createdDate
    """)
    List<RegistrationStatisticProj> getCustomersPerDayOfWeek(
            @Param("startWeek") LocalDate startWeek,
            @Param("endWeek") LocalDate endWeek
    );
}

