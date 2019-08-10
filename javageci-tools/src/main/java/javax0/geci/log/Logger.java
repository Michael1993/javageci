package javax0.geci.log;

import java.util.logging.Level;

public class Logger implements javax0.geci.api.Logger {

    public Logger(Class<?> forClass) {
        this.LOGGER = java.util.logging.Logger.getLogger(forClass.getName());
    }

    private final java.util.logging.Logger LOGGER;

    public void log(java.util.logging.Level level, String format, Object... params) {
        if (LOGGER.isLoggable(level)) {
            String s = String.format(format, params);
            LOGGER.log(level, s);
        }
    }

    public void trace(String format, Object... params) {
        log(Level.FINER, format, params);
    }

    public void debug(String format, Object... params) {
        log(Level.CONFIG, format, params);
    }

    public void info(String format, Object... params) {
        log(Level.INFO, format, params);
    }

    public void warning(String format, Object... params) {
        log(Level.WARNING, format, params);
    }

    public void error(String format, Object... params) {
        log(Level.SEVERE, format, params);
    }
}
