package com.alibou.finance.account.infrastructure.adapter.out.service;

import com.alibou.finance.account.domain.out.service.ReferenceGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ReferenceGeneratorAdapter implements ReferenceGenerator {

    private static final String ALPHA_NUMERIC_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String generateReferenceCharacter(int length) {
        StringBuilder builder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for(int i=0; i < length; i++){
            int randomIndex = secureRandom.nextInt(ALPHA_NUMERIC_CHARACTERS.length());
            builder.append(ALPHA_NUMERIC_CHARACTERS.charAt(randomIndex));
        }
        return builder.toString();
    }
}
