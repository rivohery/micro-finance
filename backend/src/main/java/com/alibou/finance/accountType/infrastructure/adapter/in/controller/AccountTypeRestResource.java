package com.alibou.finance.accountType.infrastructure.adapter.in.controller;

import com.alibou.finance.accountType.domain.agregate.AccountType;
import com.alibou.finance.accountType.domain.vo.AccountTypeId;
import com.alibou.finance.accountType.infrastructure.adapter.in.dto.AccountTypeRequest;
import com.alibou.finance.accountType.infrastructure.adapter.in.dto.AccountTypeResponse;
import com.alibou.finance.accountType.infrastructure.transactional.AccountTypeUseCaseProxy;
import com.alibou.finance.shared.infrastructure.dto.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/account-type")
@Tag(name="accountType-crud-endpoints", description = "Endpoint pour gérer les paramètres sur le type de compte")
@RequiredArgsConstructor
public class AccountTypeRestResource {

    private final AccountTypeUseCaseProxy accountTypeService;

    @Operation(
            summary = "findById",
            description = "Pour récupérer les informations sur le type de compte par son identifiant: accountTypeId"
    )
    @GetMapping("/{accountTypeId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AccountTypeResponse>findById(@PathVariable("accountTypeId") UUID accountTypeIdValue){
        AccountTypeId accountTypeId = AccountTypeId.from(accountTypeIdValue);
        return ResponseEntity.ok(
                AccountTypeResponse.from(accountTypeService.findById(accountTypeId))
        );
    }

    @Operation(
            summary = "findAll",
            description = "Pour récupérer la liste des types de comptes, accès réservé à l'admin "
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYE')")
    public ResponseEntity<List<AccountTypeResponse>>findAll(){
        return ResponseEntity.ok(
                accountTypeService.findAll()
                        .stream()
                        .map(AccountTypeResponse::from)
                        .toList()
        );
    }

    @Operation(
            summary = "create",
            description = "Pour créer un nouveau type de compte, rôle réservé à l'admin "
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AccountTypeResponse>create(
            @Valid @RequestBody AccountTypeRequest request
    ){
        AccountType accountType = AccountTypeRequest.toDomain(request);
        return ResponseEntity.ok(
                AccountTypeResponse.from(accountTypeService.create(accountType))
        );
    }

    @Operation(
            summary = "update",
            description = "Pour modifier les informations sur le type de compte, rôle réservé à l'admin "
    )
    @PutMapping("/{accountTypeId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AccountTypeResponse>update(
            @PathVariable("accountTypeId") UUID accountTypeIdValue,
            @Valid @RequestBody AccountTypeRequest request
    ){
        AccountType accountType = AccountTypeRequest.toDomain(request);
        accountType.buildAccountTypeIdFrom(accountTypeIdValue);
        return ResponseEntity.ok(
                AccountTypeResponse.from(accountTypeService.update(accountType))
        );
    }

    @Operation(
            summary = "delete",
            description = "Pour supprimer un type de compte par son identifiant, rôle réservé à l'admin "
    )
    @DeleteMapping("/{accountTypeId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>delete(
            @PathVariable("accountTypeId") UUID accountTypeId
    ){
        AccountTypeId accountTypeIdVo = AccountTypeId.from(accountTypeId);
        accountTypeService.deleteById(accountTypeIdVo);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Le type de compte est supprimé de la base de donnée")
                        .data(Map.of("accountTypeId", accountTypeId))
                        .build()
        );
    }
}
