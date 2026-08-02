package com.alibou.finance.account.infrastructure.adapter.in.controller;

import com.alibou.finance.account.application.port.dto.input.AccountLifeCycleInput;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.application.port.dto.vo.ChangedBy;
import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.application.port.usecase.AccountLifeCycleUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.infrastructure.adapter.in.dto.*;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.infrastructure.model.UserPrincipal;
import com.alibou.finance.log.domain.vo.AccountStatusHistoryId;
import com.alibou.finance.log.domain.vo.Reason;
import com.alibou.finance.shared.dto.GlobalResponse;
import com.alibou.finance.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@Tag(name="accounts-endpoints", description = "Endpoint pour consulter et gérer le cycle de vie d'un compte")
@RequiredArgsConstructor
public class AccountRestResource {

    private final AccountLifeCycleUseCase accountLifeCycleService;
    private final AccountConsultationUseCase accountConsultationService;

    @Operation(
            summary = "createNewAccount",
            description = "Création d'un nouveau compte par un employé du micro-finance avec solde zero et status 'PENDING'."
    )
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<GlobalResponse>createNewAccount(
       @Valid @RequestBody CreateAccountRequest request
    ){
        Account newAccount = CreateAccountRequest.toDomain(request);
        Account created = accountLifeCycleService.create(newAccount);
        if(created != null){
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            GlobalResponse.builder()
                                    .message("Le compte a été crée avec success")
                                    .status(HttpStatus.CREATED.value())
                                    .build()
                    );
        }
        return ResponseEntity.internalServerError().build();
    }


    @Operation(
            summary = "activateAccount",
            description = "Activer un compte par un employé  du micro-finance pour le premier dépôt ou après suspension."
    )
    @PatchMapping("/activate-account")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<GlobalResponse>activateAccount(
          @Valid @RequestBody AccountLifeCycleRequest request,
          Authentication authentication
    ){
        User user =  ((UserPrincipal)authentication.getPrincipal()).getUser();
        var accountLifeCycleInput = buildAccountLifeCycleInput(user, request);
        Map<String, Object> result = accountLifeCycleService.activateAccount(accountLifeCycleInput);
        return ResponseEntity.ok(
                    GlobalResponse.builder()
                        .message("Le compte est maintenant activé")
                        .data(Map.of(
                                "accountId", ((AccountId)result.get("accountId")).value(),
                                "accountHistoryId", ((AccountStatusHistoryId)result.get("accountHistoryId")).value()
                        ))
                        .build()
        );
    }

    @Operation(
            summary = "suspendAccount",
            description = "Suspendre  un compte par un employé  du micro-finance."
    )
    @PatchMapping("/suspend-account")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<GlobalResponse>suspendAccount(
            @Valid @RequestBody AccountLifeCycleRequest request,
            Authentication authentication
    ){
        var user =  ((UserPrincipal)authentication.getPrincipal()).getUser();
        var accountLifeCycleInput = buildAccountLifeCycleInput(user, request);
        Map<String, Object> result = accountLifeCycleService.suspendAccount(accountLifeCycleInput);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message("Le compte est maintenant suspendu")
                        .data(Map.of(
                                "accountId", ((AccountId)result.get("accountId")).value(),
                                "accountHistoryId", ((AccountStatusHistoryId)result.get("accountHistoryId")).value()
                        ))
                        .build()
        );
    }

    @Operation(
            summary = "closeAccount",
            description = "Clôturer un compte par un employé de micro-finance."
    )
    @PatchMapping("/close-account")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<GlobalResponse>closeAccount(
            @Valid @RequestBody AccountLifeCycleRequest request,
            Authentication authentication
    ){
        var user =  ((UserPrincipal)authentication.getPrincipal()).getUser();
        var accountLifeCycleInput = buildAccountLifeCycleInput(user, request);
        Map<String, Object> result = accountLifeCycleService.closeAccount(accountLifeCycleInput);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message("Le compte est maintenant clôturé définitivement")
                        .data(Map.of(
                                "accountId", ((AccountId)result.get("accountId")).value(),
                                "accountHistoryId", ((AccountStatusHistoryId)result.get("accountHistoryId")).value()
                        ))
                        .build()
        );
    }

    @Operation(
            summary = "findByAccountNumber",
            description = "Récupérer un compte par son numéros de compte."
    )
    @GetMapping("/account-number/{accountNumber}")
    public ResponseEntity<AccountResponse>findByAccountNumber(@PathVariable("accountNumber") String accountNumber){
        AccountNumber accountNumberVo = AccountNumber.from(accountNumber);
        var account = accountConsultationService.findByAccountNumber(accountNumberVo);
        if(account != null){
            return ResponseEntity.ok(AccountResponse.fromDomain(account));
        }
        return ResponseEntity.internalServerError().build();
    }

    @Operation(
            summary = "FindAllAccountBySearch",
            description = "Récupérer une page des comptes en fonction du numéros de compte."
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<PageResponse<AccountResponse>>findAllAccountBySearch(
           @RequestParam(name = "search", defaultValue = "")String search,
           @RequestParam(name = "page", defaultValue = "0")int page,
           @RequestParam(name = "size", defaultValue = "10")int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Account> pages = accountConsultationService.findAllAccountBySearch(search, pageable);
        return ResponseEntity.ok(
                new PageResponse<>(
                        pages.getContent().stream().map(AccountResponse::fromDomain).toList(),
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
            summary = "findMyAccounts",
            description = "Récupérer une liste des comptes de l'utilisateur connecté."
    )
    @GetMapping("/me")
    public ResponseEntity<List<AccountResponse>>findMyAccounts(
            Authentication authentication
    ){
        User user = ((UserPrincipal)authentication.getPrincipal()).getUser();
        List<Account>accounts = accountConsultationService.findAllByUserConnected(user);
        return ResponseEntity.ok(
                    accounts
                        .stream()
                        .map(AccountResponse::fromDomain)
                        .toList()
        );
    }

    private AccountLifeCycleInput buildAccountLifeCycleInput(User user, AccountLifeCycleRequest request){
        return AccountLifeCycleInput.builder()
                .accountId(AccountId.from(request.accountId()))
                .changedBy(ChangedBy.from(user.getUserId().value()))
                .reason(new Reason(request.reason()))
                .build();
    }

}
