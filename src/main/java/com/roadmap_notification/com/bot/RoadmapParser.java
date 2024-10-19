package com.roadmap_notification.com.bot;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoadmapParser {
    public static Map<String, String> parseRoadmap(String text) {
        Map<String, String> tasks = new LinkedHashMap<>();
        String[] lines = text.split("\n");
        String currentDay = "";
        for (String line : lines) {
            if (line.startsWith("Day")) {
                int i=line.indexOf(":");
                currentDay = line.substring(0,i).trim();
            } else if (!line.trim().isEmpty() && !currentDay.isEmpty()) {
                if(!tasks.containsKey(currentDay)){
                    tasks.put(currentDay,line.trim());
                }
                else
                tasks.put(currentDay,tasks.get(currentDay)+line.trim());
            }
        }
        return tasks;
    }
}