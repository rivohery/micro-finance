package com.alibou.finance.log.infrastructure.in.controller;

import com.alibou.finance.log.application.port.usecase.AccountStatusHistoryUseCase;
import com.alibou.finance.log.infrastructure.in.dto.AccountStatusHistoryResponse;
import com.alibou.finance.log.infrastructure.out.mappers.AccountStatusHistoryMapper;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.shared.dto.PageResponse;
import com.alibou.finance.account.domain.vo.AccountId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/account-history-status")
@Tag(name="accountStatus-history-endpoints", description = "Endpoint pour la consultation des cycles de vie d'un compte selon son ID")
@RequiredArgsConstructor
public class AccountStatusHistoryRestResource {
    private final AccountStatusHistoryUseCase accountStatusService;


    @Operation(
            summary = "status history account",
            description = "Récupérer la page d'un objet {AccountStatusHistoryResponse} qui liste le changement de status d'un compte."
    )
    @GetMapping("/{accountId}")
    public ResponseEntity<PageResponse<AccountStatusHistoryResponse>>findAllByAccountId(
            @PathVariable("accountId") UUID accountId,
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10")int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("doingAt").descending());
        Page<AccountStatusHistory>pages = accountStatusService.findAllByAccountId(AccountId.from(accountId), pageable);
        List<AccountStatusHistoryResponse>content = pages.getContent().stream().map(AccountStatusHistoryMapper::domainToResponse).toList();
        return ResponseEntity.ok(
                    new PageResponse<>(
                        content,
                        pages.getNumber(),
                        pages.getSize(),
                        pages.getTotalElements(),
                        pages.getTotalPages(),
                        pages.isFirst(),
                        pages.isLast()
                    )
            );
    }
}
