package com.alibou.finance.account.infrastructure.adapter.in.controller;

import com.alibou.finance.account.infrastructure.adapter.in.dto.AccountResponse;
import com.alibou.finance.account.application.port.command.AccountLifeCycleCommand;
import com.alibou.finance.account.application.port.command.CreateAccountCommand;
import com.alibou.finance.account.application.port.result.AccountLifeCycleResult;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.auth.UserProvider;
import com.alibou.finance.shared.domain.vo.account.AccountNumber;
import com.alibou.finance.account.infrastructure.adapter.in.dto.*;
import com.alibou.finance.account.infrastructure.adapter.out.mapper.AccountMapper;
import com.alibou.finance.account.infrastructure.transactional.AccountLifeCycleUseCaseProxy;
import com.alibou.finance.account.infrastructure.transactional.AccountConsultationUseCaseProxy;
import com.alibou.finance.account.infrastructure.transactional.CreateNewAccountUseCaseProxy;
import com.alibou.finance.shared.application.result.PageResult;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
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
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(name="accounts-endpoints", description = "Endpoint pour consulter et gérer le cycle de vie d'un compte")
@RequiredArgsConstructor
public class AccountRestResource {

    private final AccountLifeCycleUseCaseProxy accountLifeCycleService;
    private final CreateNewAccountUseCaseProxy createNewAccountUseCase;
    private final AccountConsultationUseCaseProxy accountConsultationService;
    private final UserProvider userProvider;

    @Operation(
            summary = "createNewAccount",
            description = "Création d'un nouveau compte par un employé du micro-finance avec solde zero et status 'PENDING'."
    )
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<GlobalResponse>createNewAccount(
       @Valid @RequestBody CreateAccountRequest request
    ){
        CreateAccountCommand createAccountCommand = CreateAccountRequest.toCreateAccountCommand(request);
        Account created = createNewAccountUseCase.execute(createAccountCommand);
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
        UserInfo userInfo = userProvider.getUserConnected(authentication);
        AccountLifeCycleCommand accountLifeCycleCommand = AccountMapper.buildAccountLifeCycleCommand(userInfo, request);
        AccountLifeCycleResult result = accountLifeCycleService.activateAccount(accountLifeCycleCommand);
        return buildLifeCycleResponse(result, "Le compte est activé");
    }

    @Operation(
            summary = "reactivateAccount",
            description = "Réactiver un compte par un employé  du micro-finance."
    )
    @PatchMapping("/reactivate-account")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<GlobalResponse>reactivateAccount(
            @Valid @RequestBody AccountLifeCycleRequest request,
            Authentication authentication
    ){
        UserInfo userInfo = userProvider.getUserConnected(authentication);
        AccountLifeCycleCommand accountLifeCycleCommand = AccountMapper.buildAccountLifeCycleCommand(userInfo, request);
        AccountLifeCycleResult result  = accountLifeCycleService.reactivateAccount(accountLifeCycleCommand);
        return buildLifeCycleResponse(result, "Le compte est réactivé de nouveau");
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
        UserInfo userInfo = userProvider.getUserConnected(authentication);
        AccountLifeCycleCommand accountLifeCycleCommand = AccountMapper.buildAccountLifeCycleCommand(userInfo, request);
        AccountLifeCycleResult result  = accountLifeCycleService.suspendAccount(accountLifeCycleCommand);
        return buildLifeCycleResponse(result, "Ce compte est maintenant en suspension");
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
        UserInfo userInfo = userProvider.getUserConnected(authentication);
        AccountLifeCycleCommand accountLifeCycleCommand = AccountMapper.buildAccountLifeCycleCommand(userInfo, request);
        AccountLifeCycleResult result = accountLifeCycleService.closeAccount(accountLifeCycleCommand);
        return buildLifeCycleResponse(result, "Le clôture compte est réalisé avec succès");
    }

    private ResponseEntity<GlobalResponse> buildLifeCycleResponse(
            AccountLifeCycleResult result, String message
    ) {
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message(message)
                        .status(HttpStatus.OK.value())
                        .data(Map.of(
                                "accountId", result.accountId().value(),
                                "newStatus", result.accountStatus().value().name()
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
            return ResponseEntity.ok(AccountMapper.domainToAccountResponse(account));
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
        PageResponse<AccountResponse>pageResponse = PageMapper.toPageResponse(pages, AccountMapper::domainToAccountResponse);
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/find-accounts-by-customerId/{customerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE')")
    public ResponseEntity<List<AccountResponse>>getAccountsByCustomerId(@PathVariable("customerId") UUID customerId){
        CustomerId customerIdVo = new CustomerId(customerId);
        List<AccountResponse> accounts = accountConsultationService.findAllByCustomerId(customerIdVo);
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "findMyAccounts",
            description = "Récupérer une liste des comptes de l'utilisateur connecté."
    )
    @GetMapping("/me")
    public ResponseEntity<List<AccountResponse>>findMyAccounts(
            Authentication authentication
    ){
        UserInfo userInfo = userProvider.getUserConnected(authentication);
        List<Account>accounts = accountConsultationService.findAllByUserConnected(userInfo.userId());
        return ResponseEntity.ok(
                    accounts
                        .stream()
                        .map(AccountMapper::domainToAccountResponse)
                        .toList()
        );
    }



}
