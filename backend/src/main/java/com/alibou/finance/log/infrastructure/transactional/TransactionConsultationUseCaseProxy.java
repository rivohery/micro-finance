package com.alibou.finance.log.infrastructure.transactional;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.log.application.port.usecase.TransactionConsultationUseCase;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.log.domain.vo.transaction.Reference;
import com.alibou.finance.shared.application.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionConsultationUseCaseProxy {
    private final TransactionConsultationUseCase transactionConsultationUseCase;
    @Transactional(readOnly = true)
    public PageResult<Transaction> findAllByAccountNumber(AccountNumber accountNumber, int page, int size) {
        return transactionConsultationUseCase.findAllByAccountNumber(accountNumber, page, size);
    }

    @Transactional(readOnly = true)
    public PageResult<Transaction> findAllByCreatedDate(LocalDate createdDate, int page, int size) {
        return transactionConsultationUseCase.findAllByCreatedDate(createdDate, page, size);
    }

    @Transactional(readOnly = true)
    public Transaction findByReference(Reference reference) {
        return transactionConsultationUseCase.findByReference(reference);
    }

    @Transactional(readOnly = true)
    public byte[] exportToPdf(LocalDate createdDate) {
        return transactionConsultationUseCase.exportToPdf(createdDate);
    }

}
