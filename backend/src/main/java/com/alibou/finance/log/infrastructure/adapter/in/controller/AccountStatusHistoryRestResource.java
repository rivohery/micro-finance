package com.alibou.finance.log.infrastructure.adapter.in.controller;

import com.alibou.finance.log.infrastructure.adapter.in.dto.AccountStatusHistoryResponse;
import com.alibou.finance.log.infrastructure.adapter.out.mappers.AccountStatusHistoryMapper;
import com.alibou.finance.log.domain.agregate.AccountStatusHistory;
import com.alibou.finance.log.infrastructure.transactional.AccountStatusHistoryUseCaseProxy;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/account-history-status")
@Tag(name="accountStatus-history-endpoints", description = "Endpoint pour la consultation des cycles de vie d'un compte selon son ID")
@RequiredArgsConstructor
public class AccountStatusHistoryRestResource {
    private final AccountStatusHistoryUseCaseProxy accountStatusService;

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
        PageResult<AccountStatusHistory> pageResult = accountStatusService.findAllByAccountId(AccountId.from(accountId), page, size);
        PageResponse<AccountStatusHistoryResponse> pageResponses = PageMapper.toPageResponse(pageResult, AccountStatusHistoryMapper::domainToResponse);
        return ResponseEntity.ok(pageResponses);
    }
}
