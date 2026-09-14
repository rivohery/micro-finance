package com.alibou.finance.account.domain.out.service.dto;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.shared.domain.Assert;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransfertConfirmationInfo(
    String sourceAccountNumber,
    String targetAccountNumber,
    BigDecimal originalAmount,
    BigDecimal exchangeRate,
    String transfertCurrencyCode,
    String targetCurrencyCode,
    BigDecimal finalAmount,
    LocalDateTime dateTransfert,
    User user
) {
    public TransfertConfirmationInfo{
        Assert.field("SourceAccountNumber", sourceAccountNumber).notEmpty();
        Assert.field("TargetAccountNumber", targetAccountNumber).notEmpty();
        Assert.field("TransfertCurrencyCode", transfertCurrencyCode).notEmpty();
        Assert.field("TargetCurrencyCode", targetCurrencyCode).notEmpty();
        Assert.field("OriginalAmount", originalAmount).positive();
        Assert.field("ExchangeRate", exchangeRate).positive();
        Assert.field("FinalAmount", finalAmount).positive();
        Assert.notNull("User", user);
    }
    public static TransfertConfirmationInfo initializeNewInfo(
          String  sourceAccountNumber,
          String targetAccountNumber,
          String transfertCurrencyCode,
          String targetCurrencyCode,
          BigDecimal exchangeRate,
          BigDecimal finalAmount,
          BigDecimal originalAmont,
          User user
    ){
        return TransfertConfirmationInfo.builder()
                .sourceAccountNumber(sourceAccountNumber)
                .targetAccountNumber(targetAccountNumber)
                .dateTransfert(LocalDateTime.now())
                .transfertCurrencyCode(transfertCurrencyCode)
                .targetCurrencyCode(targetCurrencyCode)
                .exchangeRate(exchangeRate)
                .finalAmount(finalAmount)
                .originalAmount(originalAmont)
                .user(user)
                .build();
    }
}
