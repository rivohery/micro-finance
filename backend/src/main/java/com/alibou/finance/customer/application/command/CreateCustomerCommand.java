package com.alibou.finance.customer.application.command;

import com.alibou.finance.shared.domain.vo.user.Username;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.domain.vo.user.Email;

public record CreateCustomerCommand(
        FirstName firstName,
        LastName lastName,
        Username username,
        DateOfBirth dateOfBirth,
        PhoneNumber phoneNumber,
        Email email,
        Cin cin,
        Occupation occupation,
        String addressValue,
        String addressCity,
        String addressZipCode,
        String addressCountry
) {
}
