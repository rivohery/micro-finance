package com.alibou.finance.account.application;

import com.alibou.finance.account.application.service.CalculMgaBalanceServiceApplication;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.exception.ThirdPartyServiceException;
import com.alibou.finance.account.domain.out.service.CurrencyExchangePort;
import com.alibou.finance.account.domain.vo.Balance;
import com.alibou.finance.account.domain.vo.MgaBalance;
import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculMgaBalanceServiceApplicationTest {
    @Mock
    private CurrencyExchangePort currencyExchangePort;
    @InjectMocks
    private CalculMgaBalanceServiceApplication calculMgaBalanceService;

    private Account account;

    private Currency mgaCurrency;
    private Currency eurCurrency;
    private BigDecimal balance;
    @BeforeEach
    void setUp(){
        mgaCurrency = Currency.builder().code(new CurrencyCode("MGA")).build();
        eurCurrency = Currency.builder().code(new CurrencyCode("EUR")).build();
        balance = new BigDecimal("2000.0");
        account =  Account.builder()
                .balance(new Balance(balance))
                .currency(eurCurrency)
                .build();
    }

    @Test
    void execute_shouldCalculMgaBalanceWithSuccess(){
        BigDecimal mgaExchangeRate = new BigDecimal("4500");
        when(currencyExchangePort.getExchangeRate("EUR", "MGA")).thenReturn(mgaExchangeRate);

        Account result = calculMgaBalanceService.execute(account);

        BigDecimal expectedMgaBalance = balance.multiply(mgaExchangeRate);
        assertThat(result).isSameAs(account);
        assertThat(result.getMgaBalance().value()).isEqualByComparingTo(expectedMgaBalance);

        verify(currencyExchangePort).getExchangeRate(anyString(), anyString());
    }

    @Test
    void execute_handleThirdPartyServiceException(){
        BigDecimal mgaExchangeRate = new BigDecimal("4500");
        doThrow(new ThirdPartyServiceException("Service non disponible"))
                .when(currencyExchangePort)
                .getExchangeRate(anyString(), anyString());

        assertThatThrownBy(() -> calculMgaBalanceService.execute(account))
                .isInstanceOf(ThirdPartyServiceException.class)
                .hasMessage("Service non disponible");

        verify(currencyExchangePort).getExchangeRate(anyString(), anyString());
    }
}
