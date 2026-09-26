package com.alibou.finance.account.application.utils;

import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.out.event.AccountStatusEventPublisher;
import com.alibou.finance.shared.domain.enums.AccountStatusEnum;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.shared.domain.event.AccountStatusChangeEvent;
import com.alibou.finance.shared.domain.vo.account.AccountId;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class AccountLifeCycleExecutor {

    private final AccountRepository accountRepository;
    private final AccountStatusEventPublisher accountStatusEventPublisher;

    public AccountLifeCycleResult executeTransition(
            AccountLifeCycleCommand cmd,
            Consumer<Account> transition
    ) {
        Account account = checkAccountByAccountId(cmd.accountId());

        AccountStatusEnum oldStatus = account.getAccountStatus().value();
        transition.accept(account);              // activate / reactivate / suspend / close
        account = accountRepository.save(account);

        AccountStatusChangeEvent event = new AccountStatusChangeEvent(
                account.getAccountId(),
                cmd.userInfo().username(),
                oldStatus,
                account.getAccountStatus().value(),
                cmd.reason()
        );
        accountStatusEventPublisher.publish(event);

        return new AccountLifeCycleResult(account.getAccountId(), account.getAccountStatus());
    }

    private Account checkAccountByAccountId(AccountId accountId){
        return  accountRepository.findById(accountId).orElseThrow(
                ()-> new AccountNotFoundException("Compte non trouvé")
        );
    }

}
