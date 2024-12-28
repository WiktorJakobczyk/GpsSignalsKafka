package pl.jakobczyk.gps.tracker.producer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceStatus;
import pl.jakobczyk.gps.tracker.producer.util.GpsCoordUtil;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;
import pl.jakobczyk.gps.tracker.producer.dto.GpsCoord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GpsSignalSimulatorTest {
    @Mock
    private Clock clock;
    @Mock
    private GpsProducer gpsProducer;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private GpsSignalSimulator gpsSignalSimulator;

    @Test
    void givenDevices_whenSimulateMovement_thenNewPositionsGeneratedAndPublished() {
        // given
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f4c");
        GpsCoord initialCoord = new GpsCoord(15.24, 19.25);
        GpsCoord simulatedCoord = new GpsCoord(15.25, 19.26);
        LocalDateTime previousTime = LocalDateTime.of(2024, 12, 19, 13, 25, 50);
        LocalDateTime simulatedTime = LocalDateTime.of(2024, 12, 19, 13, 30, 50);
        mockClockToFixedTime(simulatedTime);

        DeviceTracker existingDevice = new DeviceTracker(uuid, initialCoord, previousTime, DeviceStatus.ACTIVE);
        DeviceTracker simulatedDevice = new DeviceTracker(uuid, simulatedCoord, simulatedTime, DeviceStatus.ACTIVE);

        given(vehicleService.getDevices())
                .willReturn(List.of(existingDevice))
                .willReturn(List.of(simulatedDevice));

        try (var mockedStaticGps = mockStatic(GpsCoordUtil.class)) {
            mockedStaticGps.when(() -> GpsCoordUtil.simulateMovement(initialCoord)).thenReturn(simulatedCoord);

            // when
            gpsSignalSimulator.simulateMovement();

            // then
            verify(vehicleService).replaceDevices(List.of(simulatedDevice));
            verify(gpsProducer).publish(List.of(simulatedDevice));
            assertThat(vehicleService.getDevices()).containsExactly(simulatedDevice);
        }
    }

    private void mockClockToFixedTime(LocalDateTime creationTime) {
        Clock fixedClock = Clock.fixed(creationTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
    }

}