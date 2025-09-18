package com.crisisgame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionParser {
    private static final Pattern OPTION_LINE = Pattern.compile("(?:\\*\\*)?([A-Ea-e])(?:\\*\\*)?[\\)\\.\\:]\\s*(?:\\*\\*\\s*)?(.+?)(?:\\s*\\*\\*)?$", Pattern.MULTILINE);

    public static List<String> extractOptions(String text) {
        List<String> options = new ArrayList<>();
        
        // Split by lines and look for option patterns
        String[] lines = text.split("\\R");
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Look for patterns like **A.** or A) or A.
            Matcher m = OPTION_LINE.matcher(trimmedLine);
            if (m.find()) {
                String label = m.group(1).toUpperCase();
                String body = m.group(2).trim();
                
                // Clean up the body text - remove leading/trailing ** and extra whitespace
                body = body.replaceAll("^\\*\\*\\s*", "").replaceAll("\\s*\\*\\*$", "").trim();
                
                options.add(label + ") " + body);
            }
        }
        
        // Ensure exactly 5 entries if model drifts:
        while (options.size() < 5) options.add((char)('A' + options.size()) + ") [Option placeholder]");
        if (options.size() > 5) return options.subList(0,5);
        return options;
    }
}