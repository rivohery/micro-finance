package com.alibou.finance.account.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final Job JobCalculInteretFinDeMois;

    //@Scheduled(cron = "0 59 * * * *") seulement pour test

    // S'exécute tous les jours à 23:00:00
    @Scheduled(cron = "0 0 23 * * *")
    public void runEndOfMonthJob() {
        LocalDate today = LocalDate.now();

        // Vérification : est-ce que aujourd'hui est le dernier jour du mois ?
        if (today.getDayOfMonth() == today.lengthOfMonth()) {
            try {
                System.out.println("Lancement du Job de fin de mois...");

                JobParameters params = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();

                jobLauncher.run(JobCalculInteretFinDeMois, params);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'exécution du Job: " + e.getMessage());
            }
        }
    }
}
