package com.alibou.finance.customer.infrastructure.adapter.in.controller;

import com.alibou.finance.customer.application.port.CustomerUseCase;
import com.alibou.finance.customer.domain.vo.CustomerId;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerMinResponse;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerRequest;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.CustomerResponse;
import com.alibou.finance.customer.domain.model.Customer;
import com.alibou.finance.customer.domain.model.CustomerStatus;
import com.alibou.finance.customer.infrastructure.adapter.in.dto.UpdateStatusClientRequest;
import com.alibou.finance.customer.infrastructure.adapter.out.mapper.CustomerMapper;
import com.alibou.finance.shared.dto.GlobalResponse;
import com.alibou.finance.shared.dto.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/clients")
@Tag(name = "admin-clients-endpoints", description = "Endpoint pour gérer le cycle de vie des clients, accessible seulement par un admin")
@RequiredArgsConstructor
public class AdminCustomerRestResource {
    private final CustomerUseCase customerService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "createCustomer",
            description = "Pour créer un nouveau client avec un photo par un admin du micro-finance."
    )
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> createCustomer(
            @RequestParam(name = "customerInfo", required = true) String customerInfo,
            @Parameter
            @RequestPart(name = "file", required = true) MultipartFile uploadedFile
    ) throws IOException {
        CustomerRequest request = objectMapper.readValue(customerInfo, CustomerRequest.class);

        Customer newCustomer = CustomerRequest.toDomain(request);
        Customer created = customerService.create(newCustomer, uploadedFile.getBytes(), uploadedFile.getOriginalFilename());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GlobalResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Le client a été bien ajouté dans la base de donnée")
                        .data(Map.of("clientId", created.getCustomerId().value()))
                        .build()
                );
    }

    @Operation(
            summary = "updateCustomer",
            description = "Pour modifier les informations sur un client, rôle réservé à l'admin."
    )
    @PutMapping(value="/update/{customerId}", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>updateCustomer(
           @PathVariable("customerId") UUID customerId,
           @RequestParam(name = "customerInfo", required = true) String customerInfo,
           @Parameter
           @RequestPart(name="file", required = false) MultipartFile uploadedFile
    ) throws IOException {
        CustomerRequest customerRequest = objectMapper.readValue(customerInfo, CustomerRequest.class);
        Customer customer = CustomerRequest.toDomain(customerRequest);
        customer.setCustomerId(CustomerId.from(customerId));
        byte[]contentFile =  (uploadedFile != null && !uploadedFile.isEmpty() && uploadedFile.getBytes().length > 0) ? uploadedFile.getBytes() : null ;
        String fileName = (uploadedFile != null && !uploadedFile.isEmpty()) ? uploadedFile.getOriginalFilename() : null;
        Customer updated = customerService.update(customer, contentFile, fileName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Modification des informations clients réussie")
                        .data(Map.of("clientId", updated.getCustomerId().value()))
                        .build()
                );
    }

    @Operation(
            summary = "findCustomerDetailsById",
            description = "Pour récupérer les informations détaillés sur un client; resource réservé à l'admin"
    )
    @GetMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CustomerResponse> findCustomerDetailsById(@PathVariable("customerId") UUID customerId) {
        var customer = customerService.findCustomerDetailsById(CustomerId.from(customerId));
        if (customer != null) {
            return ResponseEntity.ok(CustomerMapper.domainToCustomerFullResponse(customer));
        }
        return ResponseEntity.internalServerError().build();
    }

    @Operation(
            summary = "findAllCustomerBySearchStart",
            description = "Pour récupérer la liste des clients par page suivant un critère de recherche: nom, prénom, ou CIN du client, resource réservé à l'admin."
    )
    @GetMapping("/find-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<CustomerMinResponse>> findAllCustomerBySearchStart(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> pageOfCustomer = customerService.findAllCustomerBySearchStart(search, pageable);
        List<CustomerMinResponse> content = pageOfCustomer
                .getContent()
                .stream()
                .map(CustomerMapper::domainToMinResponse)
                .toList();
        return ResponseEntity.ok(
                new PageResponse<>(
                        content,
                        pageOfCustomer.getNumber(),
                        pageOfCustomer.getSize(),
                        pageOfCustomer.getTotalElements(),
                        pageOfCustomer.getTotalPages(),
                        pageOfCustomer.isFirst(),
                        pageOfCustomer.isLast()
                ));
    }

    @Operation(
            summary = "updateStatusCustomer",
            description = "Pour modifier le status d'un client, rôle réservé à l'admin"
    )
    @PatchMapping("/update-status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> updateStatusCustomer(
            @Valid @RequestBody UpdateStatusClientRequest request
    ) {
        CustomerId customerId = CustomerId.from(request.id());
        CustomerStatus status = customerService.updateStatusCustomer(customerId, request.status());
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message(String.format("Le status du client a été bien modifié en: %s", status.name()))
                        .build()
        );
    }

    @Operation(
            summary = "closeCustomerAccount",
            description = "Pour fermer le compte d'un utilisateur et changer son status en BLACK_LIST, rôle réservé à l'admin"
    )
    @PatchMapping("/close-account/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> closeCustomerAccount(
            @PathVariable("customerId") UUID customerId
    ) {
        var closed = customerService.CloseCustomerAccount(CustomerId.from(customerId));
        if (closed) {
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .status(HttpStatus.OK.value())
                            .message("Le compte du client est désactivé définitivement")
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }
}
