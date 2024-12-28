package pl.jakobczyk.gps.tracker.producer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;
import pl.jakobczyk.gps.tracker.producer.service.VehicleService;
import pl.jakobczyk.gps.tracker.producer.util.GpsCoordUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/producer/gps")
@RequiredArgsConstructor
public class GpsTrackerSimulatorController {

    private final VehicleService vehicleService;

    @PostMapping("/device")
    public ResponseEntity<String> addDevice() {
        vehicleService.addDevice(UUID.randomUUID(), GpsCoordUtil.generateRandomGpsCoord());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/device/{uuid}")
    public ResponseEntity<String> deleteVehicle(@PathVariable UUID uuid) {
        vehicleService.removeDevice(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DeviceTracker>> getAllDevices() {
        List<DeviceTracker> vehicles = vehicleService.getDevices();
        return ResponseEntity.ok(vehicles);
    }

}
