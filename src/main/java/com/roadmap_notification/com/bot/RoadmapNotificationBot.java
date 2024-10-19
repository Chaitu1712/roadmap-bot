package com.roadmap_notification.com.bot;
import org.quartz.SchedulerException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class RoadmapNotificationBot {
    private static LocalDate startDate;
    
    private static final String FILENAME = "start_date.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static void main(String[] args) throws IOException, SchedulerException {

        startDate = loadStartDate();
        System.out.println("Start date is: " + startDate);

        String pdfPath = "/PATH/TO/YOUR/ROADMAP.pdf";  // Replace with the actual path to the PDF file

        // Extract roadmap text from the PDF
        String roadmapText = PDFExtractor.extractTextFromPDF(pdfPath);

        Map<String, String> tasks = RoadmapParser.parseRoadmap(roadmapText);

        LocalDate today = LocalDate.now();
          int lastDay = findLastDay(tasks);
        String recipient = "YOUR MOBILE NO."; // Replace with the actual WhatsApp number

        // Loop through the tasks and schedule notifications only for today
        for (Map.Entry<String, String> entry : tasks.entrySet()) {
            String dayKey = entry.getKey(); // e.g., "Day 1-2", "Day 3-4"
            String task = entry.getValue();

            if (dayMatchesToday(dayKey, today)) {
                NotificationScheduler.scheduleDailyReminder(task, recipient, today);
            }
        }
        LocalDate lastTaskDate = startDate.plusDays(lastDay - 1);
        if (today.isAfter(lastTaskDate)) {
            // Shutdown the scheduler
            NotificationScheduler.shutdownScheduler();

            // Exit the program
            System.out.println("All tasks are completed. Shutting down...");
            System.exit(0);
        }
    }

    // Helper function to match the day in the roadmap with today's date
    private static boolean dayMatchesToday(String dayKey, LocalDate today) {
        String[] days = dayKey.replace("Day", "").trim().split("-");
        int startDay = Integer.parseInt(days[0].trim());
        int endDay = days.length > 1 ? Integer.parseInt(days[1].trim()) : startDay;

        LocalDate startTaskDate = startDate.plusDays(startDay - 1);
        LocalDate endTaskDate = startDate.plusDays(endDay - 1);

        return !today.isBefore(startTaskDate) && !today.isAfter(endTaskDate);
    }
    private static int findLastDay(Map<String, String> tasks) {
        int lastDay = 0;
        for (String dayKey : tasks.keySet()) {
            String[] days = dayKey.replace("Day", "").trim().split("-");
            int endDay = days.length > 1 ? Integer.parseInt(days[1].trim()) : Integer.parseInt(days[0].trim());
            lastDay = Math.max(lastDay, endDay);
        }
        return lastDay;
    }
     // Load the start date from the file (or initialize it if file does not exist)
    private static LocalDate loadStartDate() throws IOException {
        File file = new File(FILENAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String dateString = reader.readLine();
                return LocalDate.parse(dateString, formatter);
            }
        } else {
            LocalDate today = LocalDate.now();
            saveStartDate(today);
            return today;
        }
    }

    private static void saveStartDate(LocalDate startDate) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            writer.write(startDate.format(formatter));
        }
    }
}