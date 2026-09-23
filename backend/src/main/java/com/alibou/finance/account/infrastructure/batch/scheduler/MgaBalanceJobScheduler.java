package com.alibou.finance.account.infrastructure.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Service
public class MgaBalanceJobScheduler {
    private static final ZoneId ZONE = ZoneId.of("Indian/Antananarivo");
    private final JobLauncher jobLauncher;
    private final Job jobCalculMgaBalanceFinDeSemaine;

    public MgaBalanceJobScheduler(
            JobLauncher jobLauncher,
            @Qualifier("jobCalculMgaBalanceFinDeSemaine") Job jobCalculMgaBalanceFinDeSemaine
    ){
        this.jobLauncher = jobLauncher;
        this.jobCalculMgaBalanceFinDeSemaine = jobCalculMgaBalanceFinDeSemaine;
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Indian/Antananarivo")
    public void runEndOfWeekJob() {
        LocalDate today = LocalDate.now(ZONE);

        // Pas la fin du mois pour ne pas surcharger la resource (2 jobs # simultanés)
        if (isLastWeek(today)  && !isLastMonthly(today)) {
            try {
                System.out.println("Lancement du Job de fin de semaine...");

                JobParameters params = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();

                jobLauncher.run(jobCalculMgaBalanceFinDeSemaine, params);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'exécution du Job: " + e.getMessage());
            }
        }
    }

    private boolean isLastWeek(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isLastMonthly(LocalDate date) {
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return date.equals(lastDay);
    }
}
