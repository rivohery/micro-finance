package com.alibou.finance.account.infrastructure.adapter.in.controller;

import com.alibou.finance.account.application.port.dto.output.TransactionResult;
import com.alibou.finance.account.application.port.dto.output.TransferResult;
import com.alibou.finance.account.infrastructure.adapter.in.dto.TransfertRequest;
import com.alibou.finance.account.infrastructure.transactional.AccountTransactionUseCaseProxy;
import com.alibou.finance.auth.infrastructure.model.UserPrincipal;
import com.alibou.finance.account.infrastructure.adapter.in.dto.TransactionRequest;
import com.alibou.finance.log.infrastructure.adapter.in.dto.TransactionResponse;
import com.alibou.finance.log.infrastructure.adapter.out.mappers.TransactionMapper;
import com.alibou.finance.log.domain.agregate.Transaction;
import com.alibou.finance.shared.infrastructure.dto.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/operations")
@Tag(name="account-operations-endpoints", description = "Endpoint pour gérer les opérations monétaire: dépôts-retraits-transfert")
@RequiredArgsConstructor
public class AccountTransactionRestResource {
    private final AccountTransactionUseCaseProxy accountTransactionService;

    @Operation(
            summary = "deposit",
            description = "Dépôt effectuer dans le micro-finance par un employée."
    )
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication
    ){
        var user = ((UserPrincipal)authentication.getPrincipal()).getUser();
        var transactionInput = TransactionRequest.toDepositCommand(request, user);
        TransactionResult result = accountTransactionService.deposit(transactionInput);
        if(result != null){
            Transaction transaction = result.transaction();
            return ResponseEntity.ok(
                    TransactionMapper.domainToResponse(transaction)
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @Operation(
            summary = "withdraw",
            description = "Retrait effectuer dans le micro-finance par un employée."
    )
    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<TransactionResponse>withdraw(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication
    ){
        var user = ((UserPrincipal)authentication.getPrincipal()).getUser();
        var transactionInput = TransactionRequest.toWithdrawCommand(request, user);
        TransactionResult result = accountTransactionService.withdraw(transactionInput);
        if(result != null){
            Transaction transaction = result.transaction();
            return ResponseEntity.ok(
                    TransactionMapper.domainToResponse(transaction)
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @Operation(
            summary = "transfert",
            description = "Transfert d'argent d'un compte du client à un autre compte."
    )
    @PostMapping("/transfert")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<GlobalResponse>transfert(
            @Valid @RequestBody TransfertRequest request,
            Authentication authentication
    ){
        var user = ((UserPrincipal)authentication.getPrincipal()).getUser();
        var transactionInput = TransfertRequest.toTransfertCommand(request, user);
        TransferResult result = accountTransactionService.transfert(transactionInput);
        if(result != null){
            return ResponseEntity.ok(
                    GlobalResponse
                            .builder()
                            .status(HttpStatus.OK.value())
                            .message("Transfert d'argent a été effectué avec success")
                            .data(Map.of(
                                    "depositTransaction", TransactionMapper.domainToResponse(result.depositTransaction()),
                                    "withdrawTransaction", TransactionMapper.domainToResponse(result.withdrawTransaction())
                            ))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }
}
