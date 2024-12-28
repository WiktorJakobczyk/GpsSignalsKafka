package pl.jakobczyk.gps.tracker.consumer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GpsSignal(
        UUID deviceUuid,
        double latitude,
        double longitude,
        LocalDateTime timestamp,
        DeviceStatus status
) {

    public enum DeviceStatus {
        ACTIVE,
        INACTIVE,
        DELETED,
        UNKNOWN
    }
}
