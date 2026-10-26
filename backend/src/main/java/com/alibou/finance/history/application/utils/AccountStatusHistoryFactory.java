package com.alibou.finance.history.application.utils;

import com.alibou.finance.history.application.port.command.AccountStatusHistoryCommand;
import com.alibou.finance.history.domain.agregate.AccountStatusHistory;
import com.alibou.finance.history.domain.vo.accountStatusHistory.AccountStatusHistoryId;
import com.alibou.finance.history.domain.vo.accountStatusHistory.DoingAt;

public class AccountStatusHistoryFactory {
    public static AccountStatusHistory prepareFrom(AccountStatusHistoryCommand command){
        return AccountStatusHistory
                .builder()
                .accountStatusHistoryId(AccountStatusHistoryId.generate())
                .doingAt(DoingAt.now())
                .accountId(command.accountId())
                .doingBy(command.doingBy())
                .reason(command.reason())
                .oldStatus(command.oldStatus())
                .newStatus(command.newStatus())
                .build();
    }
}
