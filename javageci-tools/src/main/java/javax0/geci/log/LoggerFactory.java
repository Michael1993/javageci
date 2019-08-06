package javax0.geci.log;

public class LoggerFactory {

    public static Logger getLogger(Class<?> caller) {
        return new Logger(caller);
    }
}
