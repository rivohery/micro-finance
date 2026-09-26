package com.alibou.finance.account.infrastructure.adapter.out.port.service;

import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.linker.TransactionsReportPort;
import com.alibou.finance.account.infrastructure.adapter.in.dto.TransactionResponse;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.TransactionMapper;
import com.alibou.finance.account.infrastructure.adapter.out.port.service.pdf.TransactionPdfReport;
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
