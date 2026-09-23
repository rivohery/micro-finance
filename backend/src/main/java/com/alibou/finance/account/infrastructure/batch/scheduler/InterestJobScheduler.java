package com.alibou.finance.account.infrastructure.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class InterestJobScheduler {
    private static final ZoneId ZONE = ZoneId.of("Indian/Antananarivo");
    private final JobLauncher jobLauncher;
    private final Job jobCalculInteretFinDeMois;

    public InterestJobScheduler(
            JobLauncher jobLauncher,
            @Qualifier("jobCalculInteretFinDeMois") Job jobCalculInteretFinDeMois
    ){
        this.jobLauncher = jobLauncher;
        this.jobCalculInteretFinDeMois = jobCalculInteretFinDeMois;
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Indian/Antananarivo")
    public void runEndOfMonthJob() {
        LocalDate today = LocalDate.now(ZONE);

        // Vérification : aujourd'hui est-il le dernier jour du mois ?
        if (today.getDayOfMonth() == today.lengthOfMonth()) {
            try {
                System.out.println("Lancement du Job de fin de mois...");

                JobParameters params = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();

                jobLauncher.run(jobCalculInteretFinDeMois, params);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'exécution du Job: " + e.getMessage());
            }
        }
    }
}
