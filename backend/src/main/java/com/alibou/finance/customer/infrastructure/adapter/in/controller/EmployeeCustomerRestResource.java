package com.alibou.finance.customer.infrastructure.adapter.in.controller;

import com.alibou.finance.account.infrastructure.adapter.in.dto.AccountResponse;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerMinResponse;
import com.alibou.finance.customer.infrastructure.adapter.out.mapper.CustomerMapper;
import com.alibou.finance.customer.infrastructure.transactional.CustomerConsultationUseCaseProxy;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/employees/clients")
@Tag(name = "employees-clients-endpoints", description = "Endpoint pour récupérer les clients selon un critère de recherche")
@RequiredArgsConstructor
public class EmployeeCustomerRestResource {
    private final CustomerConsultationUseCaseProxy customerConsultationService;

    @Operation(
            summary = "findAllEnableCustomerBySearch",
            description = "Pour récupérer la liste de tous les clients dont le status autant qu'utilisateur est activé"
    )
    @GetMapping("/find-all-enable")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYE')")
    public ResponseEntity<PageResponse<CustomerMinResponse>> findAllEnableCustomerBySearch(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {

        PageResult<Customer> pageOfCustomer = customerConsultationService.findAllEnableCustomerBySearch(search, page, size);
        PageResponse<CustomerMinResponse> pagesResponse = PageMapper.toPageResponse(pageOfCustomer, CustomerMapper::domainToMinResponse);
        return ResponseEntity.ok(pagesResponse);
    }

    @Operation(
            summary = "findCustomerWithAccounts",
            description = "Pour récupérer les informations sur un client accompagner de ses comptes"
    )
    @GetMapping("/find-details-with-accounts/{customerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYE')")
    public ResponseEntity<Map<String, Object>>findCustomerWithAccounts(
            @PathVariable("customerId") UUID customerId
    ){
        var detailCustomerWithAccount = customerConsultationService.findCustomerWithAccounts(CustomerId.from(customerId));
        CustomerMinResponse customer = CustomerMapper.domainToMinResponse(detailCustomerWithAccount.customer());
        List<AccountResponse> accounts = detailCustomerWithAccount.accounts().stream().map(AccountResponse::fromDomain).toList();
        return ResponseEntity.ok(Map.of("customer", customer, "accounts", accounts));
    }
}
