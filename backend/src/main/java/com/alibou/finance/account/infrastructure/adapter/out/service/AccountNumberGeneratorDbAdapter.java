package com.alibou.finance.account.infrastructure.adapter.out.service;

import com.alibou.finance.account.domain.out.service.AccountNumberGenerator;
import com.alibou.finance.account.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountNumberGeneratorDbAdapter implements AccountNumberGenerator {
    private static final String NUMERIC_CHARACTERS = "0123456789";
    private final AccountJpaRepository accountJpaRepository;

    @Override
    public String generateUniqueAccountNumber(String agenceNumber, String accountTypeCode, int numericGeneratorLength) {
        boolean isUniqueAccountNumber = true;
        String accountNumber = "";
        while (isUniqueAccountNumber){
            accountNumber = buildAccountNumber(agenceNumber, accountTypeCode, numericGeneratorLength);
            isUniqueAccountNumber = accountJpaRepository.existsByAccountNumber(accountNumber);
        }
        return accountNumber;
    }

    private String buildAccountNumber(String agenceNumber, String accountTypeCode, int numericGeneratorLength){
        StringBuilder accountNumberBuilder = new StringBuilder();
        accountNumberBuilder.append(agenceNumber);
        accountNumberBuilder.append("-");
        accountNumberBuilder.append(accountTypeCode);
        accountNumberBuilder.append("-");

        String numericValue = numericGenerator(numericGeneratorLength);
        accountNumberBuilder.append(numericValue);
        return accountNumberBuilder.toString();
    }

    private String numericGenerator(int length){
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for(int i=0; i < length; i++){
            int randomIndex = secureRandom.nextInt(NUMERIC_CHARACTERS.length());
            codeBuilder.append(NUMERIC_CHARACTERS.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}
