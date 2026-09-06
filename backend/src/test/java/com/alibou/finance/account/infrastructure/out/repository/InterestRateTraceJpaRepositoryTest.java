package com.alibou.finance.account.infrastructure.out.repository;

import com.alibou.finance.log.infrastructure.adapter.out.persistence.entity.InterestRateTraceEntity;
import com.alibou.finance.log.infrastructure.adapter.out.persistence.repository.InterestRateTraceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("github-actions")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InterestRateTraceJpaRepositoryTest {

    @Autowired
    private InterestRateTraceJpaRepository interestRateTraceRepository;

    @BeforeEach
    void setUp(){
        interestRateTraceRepository.deleteAll();
        InterestRateTraceEntity entity1 = create(BigDecimal.valueOf(0.02), "001-10-1234567890", "JUNE");
        InterestRateTraceEntity entity2 = create(BigDecimal.valueOf(0.02), "001-10-1234567890", "JULY");
        InterestRateTraceEntity entity3 = create(BigDecimal.valueOf(0.03), "001-10-1234567891", "JUNE");
        InterestRateTraceEntity entity4 = create(BigDecimal.valueOf(0.04), "001-10-1234567892", "JUNE");

        interestRateTraceRepository.saveAll(List.of(entity1,entity2,entity3,entity4));
        assertThat(interestRateTraceRepository.findAll().size()).isEqualTo(4);
    }

    @Test
    void should_getTotalMonthlyInterestRateSuccessfully(){
        BigDecimal total = interestRateTraceRepository.getTotalMonthlyInterestRate("JUNE", "2026");
        System.out.println(total.doubleValue());
        double expected = (0.02 + 0.03 + 0.04) * 4500;
        System.out.println(expected);
        assertThat(total.compareTo(BigDecimal.valueOf(expected))).isEqualTo(0);
    }

    @Test
    void should_findAllByMonthEqualsAndYearEqualsSuccessfully(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<InterestRateTraceEntity> response = interestRateTraceRepository.findAllByMonthEqualsAndYearEquals("JUNE", "2026", pageable );

        assertThat(response.getContent().size()).isEqualTo(3);
    }

    private InterestRateTraceEntity create(BigDecimal amount, String accountNumber, String month){
        return InterestRateTraceEntity.builder()
                .id(UUID.randomUUID())
                .year("2026")
                .month(month)
                .interestRate(BigDecimal.valueOf(0.12))
                .currencyCode("USD")
                .amount(amount)
                .mgaAmount(amount.multiply(BigDecimal.valueOf(4500)))
                .accountNumber(accountNumber)//"001-10-1234567890"
                .build();
    }
}
