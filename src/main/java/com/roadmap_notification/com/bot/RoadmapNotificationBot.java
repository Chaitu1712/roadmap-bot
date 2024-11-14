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
    private static LocalDate startDate;  // Start date will be loaded or set

    private static final String FILENAME = "start_date.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static void main(String[] args) throws IOException, SchedulerException {
        // Load or initialize the start date
        startDate = loadStartDate();
        System.out.println("Start date is: " + startDate);
        // Path to the roadmap PDF file
        String pdfPath = "./././././././trading_bot_roadmap.pdf";

        // Extract roadmap text from the PDF
        String roadmapText = PDFExtractor.extractTextFromPDF(pdfPath);

        // Parse the extracted text to get tasks
        Map<String, String> tasks = RoadmapParser.parseRoadmap(roadmapText);

        // Get the current date
        LocalDate today = LocalDate.now();
        // Find the last day in the roadmap
          int lastDay = findLastDay(tasks);
        // WhatsApp recipient (replace with actual WhatsApp number)
        String recipient = "+917454841110";  // Replace with the recipient's WhatsApp number

        // Loop through the tasks and schedule notifications only for today
        for (Map.Entry<String, String> entry : tasks.entrySet()) {
            String dayKey = entry.getKey(); // e.g., "Day 1-2", "Day 3-4"
            String task = entry.getValue();

            // Extract day number from dayKey (simple method, modify if needed)
            if (dayMatchesToday(dayKey, today)) {
                // Schedule a daily notification for today's task
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
            // File doesn't exist, set today's date as start date and save it
            LocalDate today = LocalDate.now();
            saveStartDate(today);
            return today;
        }
    }

    // Save the start date to a file
    private static void saveStartDate(LocalDate startDate) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            writer.write(startDate.format(formatter));
        }
    }
}