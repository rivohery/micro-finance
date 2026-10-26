package com.alibou.finance.account.infrastructure.transactional;

import com.alibou.finance.account.application.port.command.DepositCommand;
import com.alibou.finance.account.application.port.command.TransferCommand;
import com.alibou.finance.account.application.port.command.WithdrawCommand;
import com.alibou.finance.account.application.port.result.TransactionResult;
import com.alibou.finance.account.application.port.result.TransferResult;
import com.alibou.finance.account.application.port.usecase.DepositUseCase;
import com.alibou.finance.account.application.port.usecase.TransferUseCase;
import com.alibou.finance.account.application.port.usecase.WithdrawUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AccountTransactionUseCaseProxy {

    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;

    @Transactional
    public TransactionResult deposit(DepositCommand input) {
        return depositUseCase.execute(input);
    }

    @Transactional
    public TransactionResult withdraw(WithdrawCommand input) {
        return withdrawUseCase.execute(input);
    }

    @Transactional
    public TransferResult transfert(TransferCommand input) {
        return transferUseCase.execute(input);
    }

}
