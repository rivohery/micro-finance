package com.alibou.finance.account.application.service;

import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.application.port.usecase.ChangeAnnualInterestRateUseCase;
import com.alibou.finance.shared.domain.vo.accountType.AccountTypeId;
import com.alibou.finance.shared.domain.vo.accountType.InterestRate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeAnnualInterestRateServiceApplication implements ChangeAnnualInterestRateUseCase {

    private final AccountRepository accountRepository;

    @Override
    public void execute(AccountTypeId accountTypeId, InterestRate interestRate) {
        accountRepository.changeAnnualInterestRate(accountTypeId, interestRate);
    }
}
