package jm.task.core.jdbc.service;

import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GenerationType;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import java.time.LocalDateTime;

public class LoggerService {
    private static LoggerService instance;
    private final Util util;
    private Level currentLogLevel;

    public enum Level {
        ERROR, WARN, INFO, DEBUG, TRACE
    }

    @Entity
    @Table(name = "logs")
    public static class LogEntry {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "level", nullable = false, length = 10)
        private String level;

        @Column(name = "message", columnDefinition = "TEXT", nullable = false)
        private String message;

        @Column(name = "class_name", nullable = false, length = 255)
        private String className;

        @Column(name = "method_name", nullable = false, length = 255)
        private String methodName;

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "timestamp", nullable = false)
        private LocalDateTime timestamp;

        public LogEntry() {}

        public LogEntry(String level, String message, String className, String methodName, Long userId) {
            this.level = level;
            this.message = message;
            this.className = className;
            this.methodName = methodName;
            this.userId = userId;
            this.timestamp = LocalDateTime.now();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    private LoggerService() {
        this.util = Util.getInstance();
        this.currentLogLevel = Level.INFO;
    }

    public static synchronized LoggerService getInstance() {
        if (instance == null) {
            instance = new LoggerService();
        }
        return instance;
    }

    public void setLogLevel(Level level) {
        this.currentLogLevel = level;
    }

    public Level getLogLevel() {
        return currentLogLevel;
    }

    private boolean shouldLog(Level level) {
        return level.ordinal() <= currentLogLevel.ordinal();
    }

    private void log(Level level, String message, String className, String methodName, Long userId) throws SystemException {
        if (!shouldLog(level)) {
            return;
        }

        Transaction transaction = null;
        try (Session session = util.getSessionFactory().openSession()) {
            transaction = (Transaction) session.beginTransaction();
            LogEntry logEntry = new LogEntry(level.name(), message, className, methodName, userId);
            session.persist(logEntry);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            // Fallback to console
            System.err.println("LOG ERROR [" + level + "]: " + message +
                    " (Class: " + className + ", Method: " + methodName + ")");
        }
    }

    // Методы для разных уровней логирования (такие же как в первой версии)
    public void error(String message, String className, String methodName, Long userId) throws SystemException {
        log(Level.ERROR, message, className, methodName, userId);
        System.err.println("ERROR: " + message);
    }

    public void warn(String message, String className, String methodName, Long userId) throws SystemException {
        log(Level.WARN, message, className, methodName, userId);
        System.out.println("WARN: " + message);
    }

    public void info(String message, String className, String methodName, Long userId) throws SystemException {
        log(Level.INFO, message, className, methodName, userId);
        System.out.println("INFO: " + message);
    }

    public void debug(String message, String className, String methodName, Long userId) throws SystemException {
        log(Level.DEBUG, message, className, methodName, userId);
        System.out.println("DEBUG: " + message);
    }

    public void trace(String message, String className, String methodName, Long userId) throws SystemException {
        log(Level.TRACE, message, className, methodName, userId);
        System.out.println("TRACE: " + message);
    }

    // Перегруженные методы без userId
    public void error(String message, String className, String methodName) throws SystemException {
        error(message, className, methodName, null);
    }

    public void warn(String message, String className, String methodName) throws SystemException {
        warn(message, className, methodName, null);
    }

    public void info(String message, String className, String methodName) throws SystemException {
        info(message, className, methodName, null);
    }

    public void debug(String message, String className, String methodName) throws SystemException {
        debug(message, className, methodName, null);
    }

    public void trace(String message, String className, String methodName) throws SystemException {
        trace(message, className, methodName, null);
    }
}
