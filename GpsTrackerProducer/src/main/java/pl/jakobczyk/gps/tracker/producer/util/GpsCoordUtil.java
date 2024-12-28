package pl.jakobczyk.gps.tracker.producer.util;

import lombok.experimental.UtilityClass;
import pl.jakobczyk.gps.tracker.producer.dto.GpsCoord;

import java.util.Random;

@UtilityClass
public class GpsCoordUtil {
    private static final int MIN_LATITUDE = -70;
    private static final int MAX_LATITUDE = 70;
    private static final int MIN_LONGITUDE = -140;
    private static final int MAX_LONGITUDE = 140;
    private static final double SIMULATION_MOVE_SPEED = 0.5;

    public static GpsCoord generateRandomGpsCoord() {
        double latitude = MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * Math.random();
        double longitude = MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * Math.random();
        return new GpsCoord(latitude, longitude);
    }

    public static GpsCoord simulateMovement(GpsCoord currentCoords) {
        double newLatitude = currentCoords.latitude() + (Math.random() - 0.5) * SIMULATION_MOVE_SPEED;
        double newLongitude = currentCoords.longitude() + (Math.random() - 0.5) * SIMULATION_MOVE_SPEED;
        return new GpsCoord(newLatitude, newLongitude);
    }
}