package pl.jakobczyk.gps.tracker.consumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GpsSignalService {
    private final Map<GpsSignal.DeviceStatus, GpsSignalOperation> gpsSignalStatusOperations = new HashMap<>(Map.of(
            GpsSignal.DeviceStatus.ACTIVE, this::processActiveDevice,
            GpsSignal.DeviceStatus.INACTIVE, this::processInactiveDevice,
            GpsSignal.DeviceStatus.DELETED, this::processDeletedDevice
    ));

    private final Map<UUID, GpsSignal> gpsSignals;

    public void consumeSignal(GpsSignal gpsSignal) {
        GpsSignalOperation operation = gpsSignalStatusOperations.get(gpsSignal.status());
        if (operation == null) {
            throw new IllegalArgumentException("No operation found for status: " + gpsSignal.status());
        }
        operation.apply(gpsSignal);
    }

    public List<GpsSignal> getLatestSignals() {
        return List.copyOf(gpsSignals.values());
    }

    private void processActiveDevice(GpsSignal gpsSignal) {
        gpsSignals.put(gpsSignal.deviceUuid(), gpsSignal);
    }

    private void processInactiveDevice(GpsSignal gpsSignal) {
        gpsSignals.put(gpsSignal.deviceUuid(), gpsSignal);
    }

    private void processDeletedDevice(GpsSignal gpsSignal) {
        gpsSignals.remove(gpsSignal.deviceUuid());
    }

    @FunctionalInterface
    public interface GpsSignalOperation {
        void apply(GpsSignal gpsSignal);
    }
}
