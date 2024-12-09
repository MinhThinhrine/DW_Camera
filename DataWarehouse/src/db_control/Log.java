package db_control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Log {
    private int idLog;
    private LocalDateTime timestamp;
    private String level;
    private String message;
    private String context;
    private String stackTrace;

    // Constructor
    public Log(String level, String message, String context, String stackTrace) {
        if (level == null || level.isEmpty()) {
            throw new IllegalArgumentException("Level không được để trống.");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message không được để trống.");
        }
        this.timestamp = LocalDateTime.now();
        this.level = level;
        this.message = message;
        this.context = context != null ? context : "No Context"; // Giá trị mặc định
        this.stackTrace = stackTrace;
    }


    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
