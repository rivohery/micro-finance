package com.alibou.finance.statistic.infrastructure.adapter.in;

import com.alibou.finance.statistic.application.StatisticUseCase;
import com.alibou.finance.statistic.domain.agregate.NumberAccountStatistic;
import com.alibou.finance.statistic.domain.agregate.RegistrationStatistic;
import com.alibou.finance.statistic.domain.agregate.SoldeAccountStatistic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "statistic-endpoint", description = "Récupérer les statistiques pour le tableau de bord")
@RequestMapping("/statistic")
public class StatisticRestResource {
    private final StatisticUseCase statisticUseCase;

    @Operation(
            summary = "getNbrTotalOfCustomer",
            description = "Récupérer le nombre total des clients dont le status n'est pas clôturé"
    )
    @GetMapping("/nbr-total-of-customer")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> getNbrTotalOfCustomer() {
        return ResponseEntity.ok(statisticUseCase.getNbrTotalOfCustomer());
    }

    @Operation(
            summary = "getNbrTotalOfAccount",
            description = "Récupérer le nombre total des comptes non clôturé"
    )
    @GetMapping("/nbr-total-of-account")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> getNbrTotalOfAccount() {
        return ResponseEntity.ok(statisticUseCase.getNbrTotalOfAccount());
    }

    @Operation(
            summary = "getSoldeTotalOfAccountInMga",
            description = "Récupérer le montant total des comptes non clôturé en MGA"
    )
    @GetMapping("/sold-total-of-account")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BigDecimal> getSoldeTotalOfAccountInMga() {
        return ResponseEntity.ok(statisticUseCase.getSoldeTotalOfAccountInMga());
    }

    @Operation(
            summary = "getAccountStatisticNumber",
            description = "Récupérer le nombre total des comptes non clôturé selon leur type"
    )
    @GetMapping("/account-number-by-type")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<NumberAccountStatistic>> getAccountStatisticNumber() {
        return ResponseEntity.ok(statisticUseCase.getAccountStatisticNumber());
    }

    @Operation(
            summary = "getAccountStatisticSold",
            description = "Récupérer le solde total des comptes non clôturé selon leur type"
    )
    @GetMapping("/account-sold-by-type")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<SoldeAccountStatistic>> getAccountStatisticSold() {
        return ResponseEntity.ok(statisticUseCase.getAccountStatisticSold());
    }

    @Operation(
            summary = "getRegistrationStatisticOfWeek",
            description = "Récupérer le nombre de nouveau clients enregistrés par jour pour la semaine en cours"
    )
    @GetMapping("/registration-customer-statistic")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RegistrationStatistic>> getRegistrationStatisticOfWeek(){
        return ResponseEntity.ok(statisticUseCase.getRegistrationStatisticsByWeek());
    }

}
