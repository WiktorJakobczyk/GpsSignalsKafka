package pl.jakobczyk.gps.tracker.producer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceStatus;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;
import pl.jakobczyk.gps.tracker.producer.dto.GpsCoord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    @Mock
    private Clock clock;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void givenGpsDeviceData_whenAddDevice_thenDeviceAddedCorrectly() {
        // given
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f4c");
        GpsCoord gpsCoord = new GpsCoord(15.25, 17.26);
        LocalDateTime creationTime = LocalDateTime.of(2024, 12, 19, 13, 30, 50);
        mockClockToFixedTime(creationTime);

        // when
        vehicleService.addDevice(uuid, gpsCoord);

        // then
        assertThat(vehicleService.getDevices()).usingRecursiveComparison().isEqualTo(List.of(
                new DeviceTracker(uuid, gpsCoord, creationTime, DeviceStatus.ACTIVE)
        ));
    }

    @Test
    void givenExistingUuid_whenRemoveDevice_thenDeviceStatusSetToRemoved() {
        // given
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f4c");
        LocalDateTime creationTime = LocalDateTime.of(2024, 12, 19, 13, 30, 50);
        mockClockToFixedTime(creationTime);
        vehicleService.addDevice(uuid, new GpsCoord(15.24, 19.25));

        // when
        vehicleService.removeDevice(uuid);

        // then
        assertThat(vehicleService.getDevices()).usingRecursiveComparison().isEqualTo(List.of(
                new DeviceTracker(UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f4c"), new GpsCoord(15.24, 19.25), LocalDateTime.now(clock), DeviceStatus.DELETED)
        ));
    }

    @Test
    void givenNewDevicesList_whenReplaceDevices_thenDevicesReplaced() {
        // given
        UUID uuid1 = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f4c");
        UUID uuid2 = UUID.fromString("5f556845-d552-45b8-b8a6-92f29ab24f4c");

        DeviceTracker deviceTracker1 = new DeviceTracker(uuid1, new GpsCoord(15.24, 19.25), LocalDateTime.parse("2024-12-30T12:30:50"), DeviceStatus.ACTIVE);
        DeviceTracker deviceTracker2 = new DeviceTracker(uuid2, new GpsCoord(12.24, 76.25), LocalDateTime.parse("2024-12-30T11:30:50"), DeviceStatus.DELETED);

        // when
        vehicleService.replaceDevices(List.of(deviceTracker1, deviceTracker2));

        // then
        assertThat(vehicleService.getDevices()).containsExactlyInAnyOrder(
                new DeviceTracker(uuid1, new GpsCoord(15.24, 19.25), LocalDateTime.parse("2024-12-30T12:30:50"), DeviceStatus.ACTIVE),
                new DeviceTracker(uuid2, new GpsCoord(12.24, 76.25), LocalDateTime.parse("2024-12-30T11:30:50"), DeviceStatus.DELETED)
        );
    }

    private void mockClockToFixedTime(LocalDateTime creationTime) {
        Clock fixedClock = Clock.fixed(creationTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
    }
}