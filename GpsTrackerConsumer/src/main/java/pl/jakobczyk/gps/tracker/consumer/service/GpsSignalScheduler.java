package pl.jakobczyk.gps.tracker.consumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GpsSignalScheduler {
    private final GpsSignalService gpsSignalService;

    @Scheduled(fixedRate = 2000)
    public void checkForInactiveDevices() {
        gpsSignalService.getLatestSignals().forEach( signal -> {
            if (signal.timestamp().isBefore(LocalDateTime.now().minusSeconds(15))) {
                GpsSignal updatedSignal = new GpsSignal(
                        signal.deviceUuid(),
                        signal.latitude(),
                        signal.longitude(),
                        signal.timestamp(),
                        GpsSignal.DeviceStatus.INACTIVE
                );
                gpsSignalService.consumeSignal(updatedSignal);
            }
        });
    }
}
