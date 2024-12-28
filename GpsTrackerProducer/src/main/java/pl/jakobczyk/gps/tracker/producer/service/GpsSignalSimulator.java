package pl.jakobczyk.gps.tracker.producer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceStatus;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;
import pl.jakobczyk.gps.tracker.producer.util.GpsCoordUtil;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class GpsSignalSimulator {
    private final Clock clock;
    private final GpsProducer gpsProducer;
    private final VehicleService vehicleService;

    @Scheduled(fixedRateString = "${scheduled.task.simulate.fixedRate}")
    public void simulateMovement() {
        Map<DeviceStatus, List<DeviceTracker>> groupedDevices = vehicleService.getDevices().stream()
                .collect(Collectors.groupingBy(DeviceTracker::status));

        List<DeviceTracker> activeDevicesWithNewPositions = groupedDevices.getOrDefault(DeviceStatus.ACTIVE, Collections.emptyList()).stream()
                .map(vehicle -> new DeviceTracker(vehicle.uuid(), GpsCoordUtil.simulateMovement(vehicle.coords()), LocalDateTime.now(clock), vehicle.status()))
                .toList();
        List<DeviceTracker> deletedDevices = groupedDevices.getOrDefault(DeviceStatus.DELETED, Collections.emptyList());

        vehicleService.replaceDevices(activeDevicesWithNewPositions);
        gpsProducer.publish(Stream.concat(activeDevicesWithNewPositions.stream(), deletedDevices.stream()).toList());
    }


}
