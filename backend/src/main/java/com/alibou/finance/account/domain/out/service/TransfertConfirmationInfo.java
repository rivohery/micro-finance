package com.alibou.finance.account.domain.out.service;

import com.alibou.finance.auth.UserInfo;
import com.alibou.finance.shared.domain.validator.Assert;
import com.alibou.finance.shared.domain.vo.user.Email;
import com.alibou.finance.shared.domain.vo.user.Username;
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
    Username username,
    Email userEmail
) {
    public TransfertConfirmationInfo{
        Assert.field("SourceAccountNumber", sourceAccountNumber).notEmpty();
        Assert.field("TargetAccountNumber", targetAccountNumber).notEmpty();
        Assert.field("TransfertCurrencyCode", transfertCurrencyCode).notEmpty();
        Assert.field("TargetCurrencyCode", targetCurrencyCode).notEmpty();
        Assert.field("OriginalAmount", originalAmount).positive();
        Assert.field("ExchangeRate", exchangeRate).positive();
        Assert.field("FinalAmount", finalAmount).positive();
    }
    public static TransfertConfirmationInfo initializeNewInfo(
          String  sourceAccountNumber,
          String targetAccountNumber,
          String transfertCurrencyCode,
          String targetCurrencyCode,
          BigDecimal exchangeRate,
          BigDecimal finalAmount,
          BigDecimal originalAmont,
          UserInfo userInfo
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
                .username(userInfo.username())
                .userEmail(userInfo.userEmail())
                .build();
    }

}
