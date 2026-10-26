package com.alibou.finance.statistic.infrastructure.adapter.out;

import com.alibou.finance.customer.CustomerProvider;
import com.alibou.finance.shared.infrastructure.dto.RegistrationStatistic;
import com.alibou.finance.statistic.domain.out.CustomerStatisticLinker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerStatisticLinkerAdapter implements CustomerStatisticLinker {

    private final CustomerProvider customerProvider;
    @Override
    public Long getNbrTotalOfCustomer() {
        return customerProvider.getNbrTotalOfCustomer();
    }

    @Override
    public List<RegistrationStatistic> getRegistrationStatisticsByWeek(LocalDate startWeek, LocalDate endWeek) {
        return customerProvider.getRegistrationStatisticsByWeek(startWeek, endWeek);
    }
}
