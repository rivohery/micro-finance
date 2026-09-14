package com.alibou.finance.statistic.application;

import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.statistic.domain.agregate.RegistrationStatistic;
import com.alibou.finance.statistic.domain.service.CustomerServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceApplicationTest {
    @Mock
    private CustomerServicePort customerServicePort;
    @InjectMocks
    private StatisticServiceApplication statisticServiceApplication;

    @Test
    @DisplayName("Doit retourner une liste d'objet RegistrationStatistic dans l'ordre chronologique")
    void should_GetRegistrationStatisticsByWeekOfOrderChronologique(){
        LocalDate today = LocalDate.now();

        // Calcule dynamiquement le début de la semaine (Lundi)
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate tuesday = monday.plusDays(1);
        LocalDate wednesday = monday.plusDays(2);
        LocalDate thursday = monday.plusDays(3);
        LocalDate friday = monday.plusDays(4);
        LocalDate saturday = monday.plusDays(5);

        List<RegistrationStatisticProj>registrationStatProj = List.of(
                prepareRegistrationStatisticProj(monday, 4L),
                prepareRegistrationStatisticProj(saturday, 6L),
                prepareRegistrationStatisticProj(tuesday, 7L),
                prepareRegistrationStatisticProj(wednesday, 2L),
                prepareRegistrationStatisticProj(thursday, 10L),
                prepareRegistrationStatisticProj(friday, 17L)
        );

        when(customerServicePort.getRegistrationStatisticsByWeek(any(LocalDate.class), any(LocalDate.class))).thenReturn(registrationStatProj);

        List<RegistrationStatistic> registrationStat = statisticServiceApplication.getRegistrationStatisticsByWeek();
        //--Day verification
        assertThat(registrationStat.get(0).getDayOfWeek()).isEqualTo("monday");
        assertThat(registrationStat.get(1).getDayOfWeek()).isEqualTo("tuesday");
        assertThat(registrationStat.get(2).getDayOfWeek()).isEqualTo("wednesday");
        assertThat(registrationStat.get(3).getDayOfWeek()).isEqualTo("thursday");
        assertThat(registrationStat.get(4).getDayOfWeek()).isEqualTo("friday");
        assertThat(registrationStat.get(5).getDayOfWeek()).isEqualTo("saturday");
        //--registration number verification
        assertThat(registrationStat.get(0).getNbrCustomer()).isEqualTo(4L);
        assertThat(registrationStat.get(1).getNbrCustomer()).isEqualTo(7L);
        assertThat(registrationStat.get(2).getNbrCustomer()).isEqualTo(2L);
        assertThat(registrationStat.get(3).getNbrCustomer()).isEqualTo(10L);
        assertThat(registrationStat.get(4).getNbrCustomer()).isEqualTo(17L);
        assertThat(registrationStat.get(5).getNbrCustomer()).isEqualTo(6L);
    }

    private RegistrationStatisticProj prepareRegistrationStatisticProj(LocalDate createdDate, Long nbrCustomer){
        return  new RegistrationStatisticProj() {
            @Override
            public LocalDate getCreatedDate() {
                return createdDate;
            }

            @Override
            public Long getNbrCustomer() {
                return nbrCustomer;
            }
        };
    }
}

