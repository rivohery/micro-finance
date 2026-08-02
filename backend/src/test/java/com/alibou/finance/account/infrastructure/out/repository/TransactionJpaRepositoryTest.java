package com.alibou.finance.account.infrastructure.out.repository;

import com.alibou.finance.account.domain.agregate.TransactionTypeEnum;
import com.alibou.finance.account.domain.vo.transaction.TransactionType;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.TransactionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
public class TransactionJpaRepositoryTest {
    @Autowired
    private TransactionJpaRepository transactionRepository;

    @BeforeEach
    void setUp(){
        transactionRepository.deleteAll();

        TransactionEntity transaction0 = createTransaction(0, LocalDateTime.of(2026,6, 17, 12,50,10 ));
        TransactionEntity transaction1 = createTransaction(1, LocalDateTime.of(2026,5, 11, 12,50,10 ));
        TransactionEntity transaction2 = createTransaction(2, LocalDateTime.of(2026,4, 13, 12,50,10 ));
        TransactionEntity transaction3 = createTransaction(3, LocalDateTime.of(2026,6, 29, 12,50,10 ));
        TransactionEntity transaction4 = createTransaction(4, LocalDateTime.of(2026,6, 2, 12,50,10 ));

        List.of(transaction0, transaction1, transaction2, transaction3,transaction4).forEach(tr -> transactionRepository.save(tr));
    }

    @Test
    @DisplayName("Devrait retourner une liste de transaction de ce mois ci")
    void shouldCheckMonthlyTransactionOfOneAccountWithSuccess(){
        LocalDate now = LocalDate.now();
        LocalDateTime startMonth = now.with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
        LocalDateTime endMonth = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        List<TransactionEntity> transactions = transactionRepository.checkMonthlyTransactionOfOneAccount("ACC-23-2345", startMonth, endMonth);

        assertThat(transactions.size()).isEqualTo(3);
        assertThat(transactions).extracting(tr -> tr.getCreatedDate().getDayOfMonth()).containsExactlyInAnyOrder(2,17,29);

    }

    private TransactionEntity createTransaction(int numTrans, LocalDateTime createdDate){
        return TransactionEntity.builder()
                .reference("TRAN-" + numTrans)
                .soldBeforeTransaction(BigDecimal.valueOf(400))
                .description("Description pour test")
                .id(UUID.randomUUID())
                .accountNumber("ACC-23-2345")
                .exchangeRate(BigDecimal.ONE)
                .finalAmount(BigDecimal.valueOf(300))
                .operatorName("John")
                .originalAmount(BigDecimal.valueOf(300))
                .targetCurrencyCode("MGA")
                .transactionCurrencyCode("MGA")
                .transactionType(TransactionTypeEnum.DEPOSIT)
                .createdDate(createdDate)
                .build();
    }
}
