package pl.jakobczyk.gps.tracker.producer.util;

import lombok.experimental.UtilityClass;
import pl.jakobczyk.gps.tracker.producer.integration.GpsTrackerSimulatorControllerIT;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@UtilityClass
public class FileLoaderUtil {

    public static String loadExpectedResource(String path) {
        URL url = GpsTrackerSimulatorControllerIT.class.getResource("/integration" + path);
        try (InputStream inputStream = url.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load resource: /integration" + path, e);
        }
    }
}
