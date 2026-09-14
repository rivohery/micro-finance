package com.alibou.finance.account.infrastructure.out.service;

import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.alibou.finance.account.infrastructure.adapter.out.service.AccountNumberGeneratorDbAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AccountNumberGeneratorDbAdapterTest {
    @Mock
    private AccountJpaRepository accountJpaRepository;
    @InjectMocks
    private AccountNumberGeneratorDbAdapter accountNumberDbAdapterGenerator;

    @Test
    void generateUniqueAccountNumber_should_create_UniqueAccountNumber(){
        when(accountJpaRepository.existsByAccountNumber(anyString())).thenReturn(false);

        String accountNumber = accountNumberDbAdapterGenerator
                .generateUniqueAccountNumber("001","10", 10);

        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber.length()).isEqualTo(17);
        assertThat(accountNumber).startsWith("001-10");
        assertThat(accountNumber.substring(7).length()).isEqualTo(10);
    }
}
