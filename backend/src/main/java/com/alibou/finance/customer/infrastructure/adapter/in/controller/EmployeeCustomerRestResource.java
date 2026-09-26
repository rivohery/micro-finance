package com.alibou.finance.customer.infrastructure.adapter.in.controller;

import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerMinResponse;
import com.alibou.finance.customer.infrastructure.adapter.out.mapper.CustomerMapper;
import com.alibou.finance.customer.infrastructure.transactional.CustomerConsultationUseCaseProxy;
import com.alibou.finance.shared.application.result.PageResult;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PageResponse<CustomerMinResponse>> searchOperableCustomer(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {

        PageResult<Customer> pageOfCustomer = customerConsultationService.searchOperableCustomer(search, page, size);
        PageResponse<CustomerMinResponse> pagesResponse = PageMapper.toPageResponse(pageOfCustomer, CustomerMapper::domainToMinResponse);
        return ResponseEntity.ok(pagesResponse);
    }

    @Operation(
            summary = "findCustomerInfo",
            description = "Pour récupérer les informations minimum sur un client"
    )
    @GetMapping("/find-customer-info/{customerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYE')")
    public  ResponseEntity<CustomerMinResponse> findCustomerInfo(
            @PathVariable("customerId") UUID customerId
    ){
        var customer = customerConsultationService.findCustomerInfo(new CustomerId(customerId));
        CustomerMinResponse customerInfo = CustomerMapper.domainToMinResponse(customer);
        return ResponseEntity.ok(customerInfo);
    }
}
