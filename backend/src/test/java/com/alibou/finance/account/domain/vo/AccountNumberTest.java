package com.alibou.finance.account.domain.vo;

import com.alibou.finance.shared.domain.exception.IllegalArgumentException;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
public class AccountNumberTest {

    @Test
    void should_create_account_number_vo(){
        AccountNumber accountNumber = new AccountNumber("034-40-4357689190");
        assertThat(accountNumber).isNotNull();
    }

    @Test
    void should_throw_illegal_argument_exception_if_value_is_invalid(){
        assertThatThrownBy(() -> new AccountNumber("050-60-123456789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Numéros du compte invalide");
    }

    @Test
    void should_throw_illegal_argument_exception_if_value_is_null(){
        assertThatThrownBy(() -> new AccountNumber(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Numéros du compte ne doit pas être null");
    }
}
