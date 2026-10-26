package com.alibou.finance.customer.domain.agregate;

public enum CustomerStatusEnum {
    PENDING_KYC,   // inscription, KYC en cours
    ACTIVE,        // KYC validé, aucun blocage
    SUSPENDED,     // blocage temporaire (fraude, impayé)
    CLOSED         // relation terminée
}
