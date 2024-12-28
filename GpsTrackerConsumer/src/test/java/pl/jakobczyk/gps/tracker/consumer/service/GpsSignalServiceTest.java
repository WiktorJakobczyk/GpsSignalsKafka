package pl.jakobczyk.gps.tracker.consumer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GpsSignalServiceTest {

    @ParameterizedTest
    @MethodSource("gpsSignalTestCases")
    void givenSignalToConsume_whenConsumeSignal_thenProcessGpsSignalCorrectly(Map<UUID, GpsSignal> initialSignals, GpsSignal signalToConsume, List<GpsSignal> expectedSignalsAfterOperation) {
        // given
        GpsSignalService gpsSignalService = new GpsSignalService(initialSignals);

        // when
        gpsSignalService.consumeSignal(signalToConsume);

        // then
        List<GpsSignal> latestSignals = gpsSignalService.getLatestSignals();
        assertThat(latestSignals)
                .as("The list of GPS signals should match the expected state after consuming the signal.")
                .hasSize(expectedSignalsAfterOperation.size())
                .usingRecursiveComparison()
                .isEqualTo(expectedSignalsAfterOperation);
    }

    @Test
    void givenSignalWithUnknownStatus_whenConsumeSignal_thenThrowExceptionForUnknownDeviceStatus() {
        // given
        GpsSignalService gpsSignalService = new GpsSignalService(new HashMap<>());
        GpsSignal unknownStatusSignal = new GpsSignal(UUID.randomUUID(), 10.5, 20.5, null, GpsSignal.DeviceStatus.UNKNOWN);

        // when then
        assertThatThrownBy(() -> gpsSignalService.consumeSignal(unknownStatusSignal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No operation found for status: UNKNOWN");
    }

    @Test
    void givenInitialGpsSignals_whenGetLatestSignals_thenShouldReturnAllInitialSignals() {
        // given
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f4c");
        LocalDateTime now = LocalDateTime.now();
        GpsSignalService gpsSignalService = new GpsSignalService(new HashMap<>(Map.of(
                uuid, new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE)
        )));

        // when
        List<GpsSignal> latestSignals = gpsSignalService.getLatestSignals();

        // then
        assertThat(latestSignals).containsExactly(new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE));
    }

    private static Stream<Arguments> gpsSignalTestCases() {
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f4c");
        LocalDateTime now = LocalDateTime.now();

        return Stream.of(
                // Adding a new device - status ACTIVE
                Arguments.of(
                        new HashMap<>(),
                        new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE),
                        List.of(new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE))
                ),

                // Adding a new device - status INACTIVE
                Arguments.of(
                        new HashMap<>(Map.of(
                                uuid, new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE)
                        )),
                        new GpsSignal(UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f42"), 15.0, 25.0, now, GpsSignal.DeviceStatus.INACTIVE),
                        List.of(
                                new GpsSignal(UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f42"), 15.0, 25.0, now, GpsSignal.DeviceStatus.INACTIVE),
                                new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE)
                        )
                ),

                // Updating an existing device - status INACTIVE
                Arguments.of(
                        new HashMap<>(Map.of(
                                uuid, new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE)
                        )),
                        new GpsSignal(uuid, 15.0, 25.0, now, GpsSignal.DeviceStatus.INACTIVE),
                        List.of(new GpsSignal(uuid, 15.0, 25.0, now, GpsSignal.DeviceStatus.INACTIVE))
                ),

                // Removing a device - status DELETED
                Arguments.of(
                        new HashMap<>(Map.of(
                                uuid, new GpsSignal(uuid, 10.5, 20.5, now, GpsSignal.DeviceStatus.ACTIVE)
                        )),
                        new GpsSignal(uuid, 0.0, 0.0, now, GpsSignal.DeviceStatus.DELETED),
                        List.of()
                )
        );
    }
}
