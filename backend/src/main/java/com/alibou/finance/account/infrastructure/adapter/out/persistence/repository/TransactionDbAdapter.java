package com.alibou.finance.account.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.TransactionMapper;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionDbAdapter implements TransactionRepository {
    private final TransactionJpaRepository transactionJpaRepository;

    @Override
    public Page<Transaction> findAllByAccountNumber(AccountNumber accountNumber, Pageable pageable) {
        return transactionJpaRepository.findAllByAccountNumber(accountNumber.value(), pageable)
                    .map(TransactionMapper::entityToDomain);
    }

    @Override
    public Page<Transaction> findAll(Pageable pageable) {
        return transactionJpaRepository.findAll(pageable).map(TransactionMapper::entityToDomain);
    }

    @Override
    public Optional<Transaction> findByReference(Reference reference) {
        return transactionJpaRepository.findByReference(reference.value()).map(TransactionMapper::entityToDomain);
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionMapper.domainToEntity(transaction);
        return TransactionMapper.entityToDomain(transactionJpaRepository.save(entity));
    }

    @Override
    public Page<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return transactionJpaRepository.findAllByCreatedDateBetween(start, end, pageable).map(TransactionMapper::entityToDomain);
    }

    @Override
    public List<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end) {
        return transactionJpaRepository.findAllByCreatedDateBetween(start, end)
                .stream()
                .map(TransactionMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Transaction> checkMonthlyTransactionOfAccount(AccountNumber accountNumber, LocalDateTime startMonth, LocalDateTime endMonth) {
        return transactionJpaRepository.checkMonthlyTransactionOfOneAccount(accountNumber.value(), startMonth, endMonth)
                .stream()
                .sorted(Comparator.comparing(TransactionEntity::getCreatedDate))
                .map(TransactionMapper::entityToDomain)
                .toList();
    }
}
