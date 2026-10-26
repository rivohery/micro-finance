package com.alibou.finance.customer.internal;

import com.alibou.finance.customer.CustomerProvider;
import com.alibou.finance.customer.application.usecase.CustomerConsultationUseCase;
import com.alibou.finance.customer.application.usecase.CustomerLifeCycleUseCase;
import com.alibou.finance.customer.application.usecase.CustomerStatisticUseCase;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerProviderImpl implements CustomerProvider {
    private final CustomerStatisticUseCase customerStatisticUseCase;
    private final CustomerConsultationUseCase customerConsultationUseCase;
    private final CustomerLifeCycleUseCase customerLifeCycleUseCase;

    @Override
    public List<RegistrationStatistic> getRegistrationStatisticsByWeek(LocalDate startWeek, LocalDate endWeek) {
        return customerStatisticUseCase.getDailyCustomerRegistrationOfWeek(startWeek, endWeek);
    }

    @Override
    public Long getNbrTotalOfCustomer() {
        return customerStatisticUseCase.getNbrTotalOfCustomer();
    }

    @Override
    public CustomerId findCustomerIdByUserId(UserId userId) {
        return customerConsultationUseCase.findCustomerIdByUserId(userId);
    }

    @Override
    public boolean verifyIfCustomerIsActive(CustomerId customerId) {
        return customerLifeCycleUseCase.verifyIfCustomerIsActive(customerId);
    }
}
