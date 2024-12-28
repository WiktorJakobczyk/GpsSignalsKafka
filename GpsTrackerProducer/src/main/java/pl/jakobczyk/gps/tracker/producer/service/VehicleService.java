package pl.jakobczyk.gps.tracker.producer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceStatus;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;
import pl.jakobczyk.gps.tracker.producer.dto.GpsCoord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final Clock clock;

    private final List<DeviceTracker> devices = new ArrayList<>();

    public void addDevice(UUID vehicleId, GpsCoord initialCoords) {
        devices.add(new DeviceTracker(vehicleId, initialCoords, LocalDateTime.now(clock), DeviceStatus.ACTIVE));
    }

    public void removeDevice(UUID uuid) {
        findByUuid(uuid)
                .ifPresent(device -> {
                    int index = devices.indexOf(device);
                    if (index != -1) {
                        devices.set(index, new DeviceTracker(device.uuid(), device.coords(), LocalDateTime.now(clock), DeviceStatus.DELETED));
                    }
                });
    }

    public List<DeviceTracker> getDevices() {
        return List.copyOf(devices);
    }

    public void replaceDevices(List<DeviceTracker> newDevices) {
        devices.clear();
        devices.addAll(newDevices);
    }

    private Optional<DeviceTracker> findByUuid(UUID uuid) {
        return devices.stream()
                .filter(device -> device.uuid().equals(uuid))
                .findFirst();
    }
}
