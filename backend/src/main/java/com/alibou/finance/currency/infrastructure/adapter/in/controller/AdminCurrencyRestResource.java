package com.alibou.finance.currency.infrastructure.adapter.in.controller;

import com.alibou.finance.currency.domain.agregate.Currency;
import com.alibou.finance.currency.domain.vo.CurrencyId;
import com.alibou.finance.currency.infrastructure.adapter.in.dto.CreateCurrencyRequest;
import com.alibou.finance.currency.infrastructure.adapter.in.dto.CurrencyResponse;
import com.alibou.finance.currency.infrastructure.adapter.in.dto.UpdateCurrencyRequest;
import com.alibou.finance.currency.infrastructure.transactional.CurrencyUseCaseProxy;
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
@RequestMapping("/admin/currencies")
@Tag(name="admin-currency-endpoints",description = "Endpoint pour le CRUD Operation sur les monnaies utilisé")
@RequiredArgsConstructor
public class AdminCurrencyRestResource {
    private final CurrencyUseCaseProxy currencyService;

    @Operation(
            summary = "create",
            description = "Création d'un nouveau monnaie utilisé dans le micro-finance par ADMIN."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CurrencyResponse>create(@Valid @RequestBody CreateCurrencyRequest request){
        Currency currency = CreateCurrencyRequest.toDomain(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CurrencyResponse.fromDomain(currencyService.create(currency)));
    }


    @Operation(
            summary = "update",
            description = "Pour modifier les paramètres monétaire; rôle réservé à l'ADMIN."
    )
    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CurrencyResponse>update(@Valid @RequestBody UpdateCurrencyRequest request){
        Currency currency = UpdateCurrencyRequest.toDomain(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CurrencyResponse.fromDomain(currencyService.update(currency)));
    }

    @Operation(
            summary = "findAll",
            description = "Lister tous les monnaies enregistré dans la BD même s'il n'est pas activé, accès réservé à L'ADMIN"
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CurrencyResponse>>findAll(){
        return ResponseEntity.ok(
                currencyService.findAll()
                        .stream()
                        .map(CurrencyResponse::fromDomain)
                        .toList()
        );
    }


    @Operation(
            summary = "findById",
            description = "Pour récupérer les informations sur la monnaie par son ID"
    )
    @GetMapping("/{currencyId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CurrencyResponse>findById(@PathVariable("currencyId") UUID currencyId){
        Currency currency = currencyService.findById(CurrencyId.from(currencyId));
        return ResponseEntity.ok(
                CurrencyResponse.fromDomain(currency)
        );
    }

    @Operation(
            summary = "deleteById",
            description = "Pour supprimer la monnaie par son ID, rôle réservé à l'admin"
    )
    @DeleteMapping("/{currencyId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>deleteById(@PathVariable("currencyId") UUID currencyId){
        currencyService.deleteById(CurrencyId.from(currencyId));
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message("Suppression réussie")
                        .data(Map.of("currencyId", currencyId))
                        .build()
        );
    }

}
