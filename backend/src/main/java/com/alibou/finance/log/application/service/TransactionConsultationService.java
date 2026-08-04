package com.alibou.finance.log.application.service;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.domain.IllegalArgumentException;
import com.alibou.finance.log.application.port.usecase.TransactionConsultationUseCase;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.account.domain.exception.TransactionNotFoundException;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.TransactionsReportPort;
import com.alibou.finance.log.domain.vo.transaction.Reference;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class TransactionConsultationService implements TransactionConsultationUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionsReportPort transactionsReport;

    @Override
    public PageResult<Transaction> findAllByAccountNumber(AccountNumber accountNumber, int page, int size) {
        return transactionRepository.findAllByAccountNumber(accountNumber, page, size);
    }

    @Override
    public PageResult<Transaction>findAllByCreatedDate(LocalDate createdDate, int page, int size){
        if(Objects.isNull(createdDate) || LocalDate.parse("2000-01-01").isEqual(createdDate)){
            throw new IllegalArgumentException("Date pour filtrer les transactions ne doit pas être null");
        }
        LocalDateTime startOfDay = createdDate.atStartOfDay();
        LocalDateTime endOfDay = createdDate.atTime(LocalTime.MAX);
        return transactionRepository.findAllByCreatedDateBetween(startOfDay, endOfDay, page, size);
    }

    @Override
    public Transaction findByReference(Reference reference) {
        return transactionRepository.findByReference(reference).orElseThrow(
                () -> new TransactionNotFoundException("Transaction inexistant")
        );
    }

    @Override
    public byte[] exportToPdf(LocalDate createdDate) {
        if(Objects.isNull(createdDate) || LocalDate.parse("2000-01-01").isEqual(createdDate)){
            throw new IllegalArgumentException("Date pour filtrer les transactions ne doit pas être null");
        }
        LocalDateTime startOfDay = createdDate.atStartOfDay();
        LocalDateTime endOfDay = createdDate.atTime(LocalTime.MAX);
        List<Transaction>transactions = transactionRepository.findAllByCreatedDateBetween(startOfDay, endOfDay);

        return transactionsReport.reportTransactions(transactions, createdDate);
    }
}
