package com.alibou.finance.customer.application.command;

import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.domain.vo.user.CustomerId;


public record UpdateCustomerCommand(
        CustomerId customerId,
        UserId userId, // <-- nécessaire pour User.update
        FirstName firstName,
        LastName lastName,
        DateOfBirth dateOfBirth,
        PhoneNumber phoneNumber,
        Cin cin,
        Occupation occupation,
        String addressValue,
        String addressCity,
        String addressZipCode,
        String addressCountry
) {
}
