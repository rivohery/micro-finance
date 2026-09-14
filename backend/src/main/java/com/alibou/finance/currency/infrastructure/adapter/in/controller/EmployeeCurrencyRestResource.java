package com.alibou.finance.currency.infrastructure.adapter.in.controller;


import com.alibou.finance.currency.infrastructure.adapter.in.dto.CurrencyResponse;
import com.alibou.finance.currency.infrastructure.transactional.CurrencyUseCaseProxy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee/currencies")
@Tag(name="employee-currency-endpoints",description = "Endpoint pour récupérer les monnaies actives")
@RequiredArgsConstructor
public class EmployeeCurrencyRestResource {

    private final CurrencyUseCaseProxy currencyService;

    @Operation(
            summary = "fetchEnableCurrency",
            description = "Pour récupérer la liste des monnaies activé. Liste utilisé lors de création du compte"
    )
    @GetMapping("/fetch-enable-currency")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYE')")
    public ResponseEntity<List<CurrencyResponse>> fetchEnableCurrency(){
        return ResponseEntity.ok(
                currencyService.fetchEnableCurrency()
                        .stream()
                        .map(CurrencyResponse::fromDomain)
                        .toList()
        );
    }
}
