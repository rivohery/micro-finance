package com.alibou.finance.account.application.service;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.shared.domain.IllegalArgumentException;
import com.alibou.finance.account.application.port.usecase.TransactionConsultationUseCase;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.exception.TransactionNotFoundException;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.domain.out.service.TransactionsReportPort;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionConsultationService implements TransactionConsultationUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionsReportPort transactionsReport;

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findAllByAccountNumber(AccountNumber accountNumber, Pageable pageable) {
        return transactionRepository.findAllByAccountNumber(accountNumber, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction>findAllByCreatedDate(LocalDate createdDate, Pageable pageable){
        if(Objects.isNull(createdDate) || LocalDate.parse("2000-01-01").isEqual(createdDate)){
            throw new IllegalArgumentException("Date pour filtrer les transactions ne doit pas être null");
        }
        LocalDateTime startOfDay = createdDate.atStartOfDay();
        LocalDateTime endOfDay = createdDate.atTime(LocalTime.MAX);
        return transactionRepository.findAllByCreatedDateBetween(startOfDay, endOfDay, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction findByReference(Reference reference) {
        return transactionRepository.findByReference(reference).orElseThrow(
                () -> new TransactionNotFoundException("Transaction inexistant")
        );
    }

    @Override
    @Transactional(readOnly = true)
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
