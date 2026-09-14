package com.alibou.finance.account.domain.out.service;

public interface AccountNumberGenerator {
    String generateUniqueAccountNumber(String agenceNumber, String accountTypeCode, int numericGeneratorLength);
}
