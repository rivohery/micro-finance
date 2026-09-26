package com.alibou.finance.customer.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.UUID;

public record CustomerRequest(

        @NotBlank(message = "Le prénom ne doit pas être vide")
        String firstName,
        @NotBlank(message = "Le prénom ne doit pas être vide")
        String lastName,
        UUID userId,

        String username,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull(message = "La date de naissance ne doit pas être vide")
        LocalDate dateOfBirth,
        @NotBlank(message = "Le numéro du telephone ne doit pas être vide")
        String phoneNumber,

        @jakarta.validation.constraints.Email(message = "L'adresse email invalide")
        String email,
        @NotBlank(message = "Le CIN ne doit pas être vide")
        String cin,
        @NotBlank(message = "L'occupation ne doit pas être vide")
        String occupation,
        @NotBlank(message = "L'adresse physique ne doit pas être vide")
        String addressValue,
        @NotBlank(message = "Le ville ne doit pas être vide")
        String addressCity,
        @NotBlank(message = "Le code postal ne doit pas être vide")
        String addressZipCode,
        @NotBlank(message = "Le pays ne doit pas être vide")
        String addressCountry
) { }
