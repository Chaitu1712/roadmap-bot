package com.roadmap_notification.com.bot;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.LocalDate;

public class NotificationScheduler {
    private static Scheduler scheduler;
    public static class WhatsAppNotificationJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            String task = context.getJobDetail().getJobDataMap().getString("task");
            String to = context.getJobDetail().getJobDataMap().getString("to");
            // Send WhatsApp message
            WhatsAppNotifier.sendWhatsAppMessage(to, "Reminder: " + task);
        }
    }

    // Method to schedule a daily reminder
    public static void scheduleDailyReminder(String task, String to, LocalDate date) throws SchedulerException {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();

        JobDetail job = JobBuilder.newJob(WhatsAppNotificationJob.class)
                .withIdentity("job_" + date, "group1")
                .usingJobData("task", task)
                .usingJobData("to", to)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_" + date, "group1")
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(19, 45)) // 7.45 PM every day REPLACE WIHT YOUR REQUIRED TIME IN 24 HOUR FORMAT
                .build();
        scheduler.scheduleJob(job, trigger);
    }
    public static void shutdownScheduler() throws SchedulerException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}