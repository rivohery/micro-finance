package com.alibou.finance.log.infrastructure.adapter.out.persistence.repository;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.log.domain.vo.transaction.Reference;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.account.domain.out.repository.TransactionRepository;
import com.alibou.finance.log.infrastructure.adapter.out.mappers.TransactionMapper;
import com.alibou.finance.log.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionDbAdapter implements TransactionRepository {
    private final TransactionJpaRepository transactionJpaRepository;

    @Override
    public PageResult<Transaction> findAllByAccountNumber(AccountNumber accountNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<TransactionEntity>pageEntities = transactionJpaRepository.findAllByAccountNumber(accountNumber.value(), pageable);
        return PageMapper.toPageResult(pageEntities, TransactionMapper::entityToDomain);
    }

    @Override
    public PageResult<Transaction> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return PageMapper.toPageResult(transactionJpaRepository.findAll(pageable), TransactionMapper::entityToDomain);
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
    public PageResult<Transaction> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return PageMapper.toPageResult(transactionJpaRepository.findAllByCreatedDateBetween(start, end, pageable), TransactionMapper::entityToDomain);
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
