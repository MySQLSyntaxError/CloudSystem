package de.steuer.cloud.lib.logging;

import jline.console.ConsoleReader;
import lombok.Getter;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class Logger {

    private static Set<Logger> loggers = new HashSet<>();

    private ConsoleReader reader;

    private org.apache.log4j.Logger fileLogger;

    private org.apache.log4j.Logger apacheLogger;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd") {{
        this.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
    }};

    public Logger(File logsDirectory) {
        Date date = new Date(System.currentTimeMillis());
        File file = new File(logsDirectory.getAbsolutePath() + "/unknown.log");
        for (int id = 1; id < Integer.MAX_VALUE; id++) {
            file = new File(logsDirectory, this.simpleDateFormat.format(date) + "-" + id + ".log");
            if (file.exists()) {
                continue;
            }
            break;
        }

        if (!logsDirectory.exists()) {
            logsDirectory.mkdir();
        }

        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);
        this.apacheLogger = org.apache.log4j.Logger.getLogger("CloudSystemLogger");
        this.fileLogger = org.apache.log4j.Logger.getLogger("CloudSystemFileLogger");

        //PatternLayout layout = new PatternLayout("[%d{HH:mm:ss}] [%t] %m%n");
        PatternLayout layout = new PatternLayout("[%d{HH:mm:ss}] %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        this.apacheLogger.addAppender(consoleAppender);

        try {
            FileAppender fileAppender = new FileAppender(layout, file.getAbsolutePath(), false);
            FileAppender fileAppender2 = new FileAppender(layout, logsDirectory.getAbsolutePath() + "/latest.log", false);
            this.fileLogger.addAppender(fileAppender);
            this.fileLogger.addAppender(fileAppender2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.apacheLogger.setLevel(Level.INFO);
        this.fileLogger.setLevel(Level.INFO);

        try {
            this.reader = new ConsoleReader(System.in, System.out);
            this.reader.setExpandEvents(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add to Loggerlist
        Logger.getLoggers().add(this);
    }

    /**
     * Get a Logger
     */
    public static Logger getGlobal() {
        return Logger.getLoggers().iterator().next();
    }

    /**
     * logs a normal text into the console
     *
     * @param message the message which should print into the console
     */
    public void info(String message) {
        message = "§bINFO§r: " + message + "§r";

        this.fileLogger.info(this.removeColorCodes(message));
        message = this.translateColorCodes(message);

        this.apacheLogger.info(message);
    }

    /**
     * logs an error text into the console
     *
     * @param message the message which should print into the console
     */
    public void error(String message) {
        message = "§cERROR§r: " + message + "§r";

        this.fileLogger.info(this.removeColorCodes(message));
        message = this.translateColorCodes(message);

        this.apacheLogger.info(message);
    }

    /**
     * logs an error text and exceptiopn into the console
     *
     * @param message the message which should print into the console
     */
    public void error(String message, Exception e) {
        message = "§cERROR§r: " + message + "§r";

        this.fileLogger.error(this.removeColorCodes(message), e);
        message = this.translateColorCodes(message);

        //this.apacheLogger.info(message);
        this.apacheLogger.error(message, e);
    }

    /**
     * logs a debug text into the console
     *
     * @param message the message which should print into the console
     */
    public void debug(String message) {
        message = "§5DEBUG§r: " + message + "§r";

        this.fileLogger.info(this.removeColorCodes(message));
        message = this.translateColorCodes(message);

        this.apacheLogger.info(message);
    }

    /**
     * logs a warning text into the console
     *
     * @param message the message which should print into the console
     */
    public void warning(String message) {
        message = "§eWARNING§r: " + message + "§r";

        this.fileLogger.info(this.removeColorCodes(message));
        message = this.translateColorCodes(message);

        this.apacheLogger.info(message);
    }

    private String translateColorCodes(String message) {
        Map<String, String> replace = new HashMap<String, String>() {{
            this.put("§a", Color.GREEN);
            this.put("§b", Color.CYAN);
            this.put("§c", Color.RED);
            this.put("§d", Color.MAGENTA);
            this.put("§e", Color.YELLOW);
            this.put("§f", Color.RESET);

            this.put("§0", Color.RESET);
            this.put("§1", Color.BLUE);
            this.put("§2", Color.GREEN);
            this.put("§3", Color.CYAN);
            this.put("§4", Color.RED);
            this.put("§5", Color.MAGENTA);
            this.put("§6", Color.YELLOW);
            this.put("§7", Color.GRAY);
            this.put("§8", Color.GRAY);
            this.put("§9", Color.BLUE);

            this.put("§r", Color.RESET);
            this.put("§l", Color.BOLD);
            this.put("§n", Color.UNDERLINED);
        }};
        for (Map.Entry entry : replace.entrySet()) {
            message = message.replaceAll((String) entry.getKey(), (String) entry.getValue());
        }

        return message;
    }

    private String removeColorCodes(String message) {
        String[] list = new String[]{
                "a", "b", "c", "d", "e", "f",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "r", "l", "n"
        };
        for (String colorcode : list) {
            message = message.replaceAll("§" + colorcode, "");
        }

        return message;
    }

    public ConsoleReader getReader() {
        return reader;
    }

    public org.apache.log4j.Logger getApacheLogger() {
        return apacheLogger;
    }

    public org.apache.log4j.Logger getFileLogger() {
        return fileLogger;
    }

    public static Set<Logger> getLoggers() {
        return loggers;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }
}
