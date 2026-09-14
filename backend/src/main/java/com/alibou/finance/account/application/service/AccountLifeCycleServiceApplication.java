package com.alibou.finance.account.application.service;

import com.alibou.finance.account.application.port.dto.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.dto.output.AccountLifeCycleResult;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.application.port.usecase.AccountLifeCycleUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.domain.exception.AccountNotFoundException;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.log.application.port.command.AccountStatusHistoryCommand;
import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.domain.vo.accountStatusHistory.DoingBy;
import com.alibou.finance.log.domain.vo.accountStatusHistory.NewStatus;
import com.alibou.finance.log.domain.vo.accountStatusHistory.OldStatus;
import com.alibou.finance.log.domain.vo.accountStatusHistory.Reason;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import lombok.RequiredArgsConstructor;
import java.util.UUID;


@RequiredArgsConstructor
public class AccountLifeCycleServiceApplication implements AccountLifeCycleUseCase {
    private final AccountRepository accountRepository;
    private final UserUseCase userUseCase;
    private final AccountStatusHistoryUseCase accountStatusHistoryUseCase;

    @Override
    public AccountLifeCycleResult activateAccount(AccountLifeCycleCommand input) {
        User user = checkAndVerifyStatusOfEmployee(input.changedBy().value());

        Account account = checkAccountByAccountId(input.accountId());
        OldStatus oldStatus = new OldStatus(account.getAccountStatus().value());
        account.activeAccount();
        account = accountRepository.save(account);
        NewStatus newStatus = new NewStatus(AccountStatusEnum.ACTIVE);

        AccountStatusHistory history =  prepareAndSaveAccountStatusHistory(account, user, oldStatus, newStatus, input.reason());
        return new AccountLifeCycleResult(account, history);
    }

    @Override
    public AccountLifeCycleResult suspendAccount(AccountLifeCycleCommand input) {
        User user = checkAndVerifyStatusOfEmployee(input.changedBy().value());

        Account account = checkAccountByAccountId(input.accountId());
        OldStatus oldStatus = new OldStatus(account.getAccountStatus().value());
        account.suspendAccount();
        account = accountRepository.save(account);
        NewStatus newStatus = new NewStatus(AccountStatusEnum.SUSPENDED);

        AccountStatusHistory history =  prepareAndSaveAccountStatusHistory(account, user, oldStatus, newStatus, input.reason());
        return new AccountLifeCycleResult(account, history);
    }

    @Override
    public AccountLifeCycleResult closeAccount(AccountLifeCycleCommand input) {
        var user = checkAndVerifyStatusOfEmployee(input.changedBy().value());

        var account = checkAccountByAccountId(input.accountId());
        OldStatus oldStatus = new OldStatus(account.getAccountStatus().value());
        account.closeAccount();
        account = accountRepository.save(account);
        NewStatus newStatus = new NewStatus(AccountStatusEnum.CLOSED);

        AccountStatusHistory history =  prepareAndSaveAccountStatusHistory(account, user, oldStatus, newStatus, input.reason());
        return new AccountLifeCycleResult(account, history);
    }

    private User checkAndVerifyStatusOfEmployee(UUID id){
        User user = userUseCase.findByUserId(UserId.from(id));
        if(!user.isEnable()){
            throw new OperationNotPermittedException("Employé non activé");
        }
        return user;
    }
    private Account checkAccountByAccountId(AccountId accountId){
        return  accountRepository.findById(accountId).orElseThrow(
                ()-> new AccountNotFoundException("Compte non trouvé")
        );
    }

    private AccountStatusHistory prepareAndSaveAccountStatusHistory(
            Account account,
            User user,
            OldStatus oldStatus,
            NewStatus newStatus,
            Reason reason
    ){
        AccountStatusHistoryCommand accountStatusHistoryCommand =  AccountStatusHistoryCommand
                .builder()
                .accountId(account.getAccountId())
                .doingBy(DoingBy.from(user.getUsername().value()))
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .build();
        AccountStatusHistory accountStatusHistory = AccountStatusHistoryCommand.toAgregate(accountStatusHistoryCommand);
        return accountStatusHistoryUseCase.save(accountStatusHistory);
    }
}
