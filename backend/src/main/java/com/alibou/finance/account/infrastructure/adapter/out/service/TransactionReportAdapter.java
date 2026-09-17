package com.alibou.finance.account.infrastructure.adapter.out.service;

import com.alibou.finance.history.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.service.TransactionsReportPort;
import com.alibou.finance.history.infrastructure.adapter.in.dto.TransactionResponse;
import com.alibou.finance.history.infrastructure.adapter.out.mappers.TransactionMapper;
import com.alibou.finance.account.infrastructure.adapter.out.service.pdf.TransactionPdfReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionReportAdapter implements TransactionsReportPort {

    private final TransactionPdfReport transactionPdfReport;
    @Override
    public byte[] reportTransactions(List<Transaction> transactions, LocalDate createdDate) {
        List<TransactionResponse>transactionsDto = transactions
                .stream()
                .map(TransactionMapper::domainToResponse)
                .toList();
        return transactionPdfReport.reportTransactionsToPdf(transactionsDto, createdDate);
    }
}
