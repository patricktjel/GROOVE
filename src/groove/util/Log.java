package groove.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Log {

    public static Logger getLogger(String className) {
        try (InputStream stream = Log.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException ignored) {}
        return Logger.getLogger(className);
    }
}
