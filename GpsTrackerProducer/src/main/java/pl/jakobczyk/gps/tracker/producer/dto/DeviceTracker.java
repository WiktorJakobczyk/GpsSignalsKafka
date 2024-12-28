package pl.jakobczyk.gps.tracker.producer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceTracker(
        UUID uuid,
        GpsCoord coords,
        LocalDateTime timestamp,
        DeviceStatus status
) {

}
