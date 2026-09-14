package com.alibou.finance.account.infrastructure.adapter.in.controller;

import com.alibou.finance.account.application.port.dto.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.dto.output.AccountLifeCycleResult;
import com.alibou.finance.account.domain.vo.AccountId;
import com.alibou.finance.account.application.port.dto.vo.ChangedBy;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.vo.AccountNumber;
import com.alibou.finance.account.infrastructure.adapter.in.dto.*;
import com.alibou.finance.account.infrastructure.transactional.AccountLifeCycleUseCaseProxy;
import com.alibou.finance.account.infrastructure.transactional.AccountConsultationUseCaseProxy;
import com.alibou.finance.account.infrastructure.transactional.CreateNewAccountUseCaseProxy;
import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.infrastructure.model.UserPrincipal;
import com.alibou.finance.log.domain.vo.accountStatusHistory.Reason;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.dto.GlobalResponse;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final AccountLifeCycleUseCaseProxy accountLifeCycleService;
    private final CreateNewAccountUseCaseProxy createNewAccountUseCase;
    private final AccountConsultationUseCaseProxy accountConsultationService;

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
        Account created = createNewAccountUseCase.execute(newAccount);
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
        AccountLifeCycleResult result = accountLifeCycleService.activateAccount(accountLifeCycleInput);
        return ResponseEntity.ok(
                    GlobalResponse.builder()
                        .message("Le compte est maintenant activé")
                        .status(HttpStatus.OK.value())
                        .data(Map.of(
                                "accountId", result.account().getAccountId().value(),
                                  "newStatus", result.account().getAccountStatus().value().name(),
                                "accountHistoryId", result.history().getAccountStatusHistoryId().value()
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
        AccountLifeCycleResult result  = accountLifeCycleService.suspendAccount(accountLifeCycleInput);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message("Le compte est maintenant suspendu")
                        .status(HttpStatus.OK.value())
                        .data(Map.of(
                                "accountId", result.account().getAccountId().value(),
                                "newStatus", result.account().getAccountStatus().value().name(),
                                "accountHistoryId", result.history().getAccountStatusHistoryId().value()
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
        AccountLifeCycleResult result = accountLifeCycleService.closeAccount(accountLifeCycleInput);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message("Le compte est maintenant clôturé définitivement")
                        .status(HttpStatus.OK.value())
                        .data(Map.of(
                                "accountId", result.account().getAccountId().value(),
                                "newStatus", result.account().getAccountStatus().value().name(),
                                "accountHistoryId", result.history().getAccountStatusHistoryId().value()
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
        PageResult<Account> pages = accountConsultationService.findAllAccountBySearch(search, page, size);
        PageResponse<AccountResponse>pageResponse = PageMapper.toPageResponse(pages, AccountResponse::fromDomain);
        return ResponseEntity.ok(pageResponse);
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

    private AccountLifeCycleCommand buildAccountLifeCycleInput(User user, AccountLifeCycleRequest request){
        return AccountLifeCycleCommand.builder()
                .accountId(AccountId.from(request.accountId()))
                .changedBy(ChangedBy.from(user.getUserId().value()))
                .reason(new Reason(request.reason()))
                .build();
    }

}
