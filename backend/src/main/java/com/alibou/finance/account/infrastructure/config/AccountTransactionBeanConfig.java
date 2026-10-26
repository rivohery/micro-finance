package com.alibou.finance.account.infrastructure.config;

import com.alibou.finance.account.application.port.usecase.*;
import com.alibou.finance.account.application.service.*;
import com.alibou.finance.account.application.utils.TransactionOperationExecutor;
import com.alibou.finance.account.domain.out.linker.CurrencyExchangePort;
import com.alibou.finance.account.domain.out.linker.CustomerLinkerPort;
import com.alibou.finance.account.domain.out.linker.TransactionsReportPort;
import com.alibou.finance.account.domain.out.repository.AccountRepository;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.TransactionReferenceGenerator;
import com.alibou.finance.account.domain.out.service.TransfertConfirmationExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountTransactionBeanConfig {

    @Bean
    public TransactionOperationExecutor transactionOperationExecutor(
            AccountRepository accountRepository,
            CurrencyExchangePort currencyExchangePort
    ){
        return new TransactionOperationExecutor(accountRepository, currencyExchangePort);
    }

    @Bean
    public DepositUseCase depositUseCase(
            TransactionOperationExecutor transactionOperationExecutor,
            CreateTransactionUseCase createTransactionUseCase
    ){
        return new DepositServiceApplication(transactionOperationExecutor, createTransactionUseCase);
    }

    @Bean
    public WithdrawUseCase withdrawUseCase(
            TransactionOperationExecutor transactionOperationExecutor,
            CreateTransactionUseCase createTransactionUseCase
    ){
        return new WithdrawServiceApplication(transactionOperationExecutor, createTransactionUseCase);
    }

    @Bean
    public TransferUseCase transferUseCase(
            TransactionOperationExecutor transactionOperationExecutor,
            CustomerLinkerPort customerLinkerPort,
            CreateTransactionUseCase createTransactionUseCase,
            TransfertConfirmationExecutor transfertConfirmationExecutor
    ){
        return new TransferServiceApplication(
                transactionOperationExecutor,
                customerLinkerPort,
                createTransactionUseCase,
                transfertConfirmationExecutor
        );
    }

    @Bean
    public TransactionConsultationUseCase transactionConsultationUseCase(TransactionRepository transactionRepository, TransactionsReportPort transactionsReport){
        return new TransactionConsultationService(transactionRepository, transactionsReport);
    }

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(TransactionRepository transactionRepository, TransactionReferenceGenerator transactionReferenceGenerator){
        return new CreateTransactionServiceApplication(transactionRepository, transactionReferenceGenerator);
    }
}
