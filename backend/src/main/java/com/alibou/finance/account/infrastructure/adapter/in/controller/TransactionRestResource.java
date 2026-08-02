package com.alibou.finance.account.infrastructure.adapter.in.controller;

import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.application.port.usecase.TransactionConsultationUseCase;
import com.alibou.finance.account.infrastructure.adapter.in.dto.TransactionResponse;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.TransactionMapper;
import com.alibou.finance.account.domain.agregate.Transaction;
import com.alibou.finance.account.domain.vo.transaction.Reference;
import com.alibou.finance.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/transactions")
@Tag(name="fetch-transactions-endpoints", description = "Endpoint pour consulter les transactions selon la critère de recherche")
@RequiredArgsConstructor
public class TransactionRestResource {
    private final TransactionConsultationUseCase transactionService;

    @Operation(
            summary = "findAllByCreatedDate",
            description = "Pour récupérer les pages des transactions suivant sa date de création."
    )
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>>findAllByCreatedDate(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(name="createdDate", defaultValue = "2000-01-01")
            LocalDate createdDate,
            @RequestParam(name="page", defaultValue = "0")int page,
            @RequestParam(name="size", defaultValue = "10")int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction>pages = transactionService.findAllByCreatedDate(createdDate, pageable);
        return ResponseEntity.ok(
                new PageResponse<>(
                        pages.getContent().stream().map(TransactionMapper::domainToResponse).toList(),
                        pages.getNumber(),
                        pages.getSize(),
                        pages.getTotalElements(),
                        pages.getTotalPages(),
                        pages.isFirst(),
                        pages.isLast()
                )
        );
    }

    @Operation(
            summary = "findAllByAccountNumber",
            description = "Pour récupérer les pages des transactions d'un compte par son numéros de compte."
    )
    @GetMapping("/belong/{accountNumber}")
    public ResponseEntity<PageResponse<TransactionResponse>>findAllByAccountNumber(
          @PathVariable("accountNumber")String accountNumber,
          @RequestParam(name="page", defaultValue = "0")int page,
          @RequestParam(name="size", defaultValue = "10")int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        AccountNumber accountNumberVo = new AccountNumber(accountNumber);
        Page<Transaction>pages = transactionService.findAllByAccountNumber(accountNumberVo, pageable);
        return ResponseEntity.ok(
                new PageResponse<>(
                        pages.getContent().stream().map(TransactionMapper::domainToResponse).toList(),
                        pages.getNumber(),
                        pages.getSize(),
                        pages.getTotalElements(),
                        pages.getTotalPages(),
                        pages.isFirst(),
                        pages.isLast()
                )
        );
    }

    @Operation(
            summary = "findByReference",
            description = "Pour récupérer une transaction par son reference."
    )
    @GetMapping("/reference/{reference}")
    public ResponseEntity<TransactionResponse>findByReference(@PathVariable("reference") String value){
        Transaction transaction = transactionService.findByReference(new Reference(value));
        return ResponseEntity.ok(TransactionMapper.domainToResponse(transaction));
    }

    @Operation(
            summary = "exportToPdf",
            description = "Pour exporter la liste des transactions d'une date donnée en fichier pdf."
    )
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> exportToPdf(
            @RequestParam(name="createdDate", defaultValue = "2000-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdDate
    ){
        byte[]pdf = transactionService.exportToPdf(createdDate);
        if(Objects.nonNull(pdf)){
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }
        return ResponseEntity.internalServerError().build();
    }


}
