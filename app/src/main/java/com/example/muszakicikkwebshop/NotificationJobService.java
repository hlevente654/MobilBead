package com.example.muszakicikkwebshop;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new NotificationHandler(getApplicationContext()).send("Charging");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
