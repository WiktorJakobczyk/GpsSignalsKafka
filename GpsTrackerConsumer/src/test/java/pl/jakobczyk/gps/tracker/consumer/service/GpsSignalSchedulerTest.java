package pl.jakobczyk.gps.tracker.consumer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GpsSignalSchedulerTest {

    @Mock
    private GpsSignalService gpsSignalService;

    @Captor
    private ArgumentCaptor<GpsSignal> gpsSignalCaptor;

    @InjectMocks
    private GpsSignalScheduler gpsSignalScheduler;


    @Test
    void givenSignalOlderThan15Seconds_whenCheckForInactiveDevices_thenShouldSetDeviceStatusToInactive() {
        // given
        UUID uuid1 = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f41");
        UUID uuid2 = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f42");
        LocalDateTime now = LocalDateTime.now();
        GpsSignal oldSignal = new GpsSignal(uuid1, 50.061, 19.938, now.minusSeconds(20), GpsSignal.DeviceStatus.ACTIVE);
        GpsSignal activeSignal = new GpsSignal(uuid2, 50.061, 19.938, now.minusSeconds(5), GpsSignal.DeviceStatus.ACTIVE);

        given(gpsSignalService.getLatestSignals()).willReturn(List.of(oldSignal, activeSignal));

        // when
        gpsSignalScheduler.checkForInactiveDevices();

        // then
        verify(gpsSignalService).consumeSignal(gpsSignalCaptor.capture());
        GpsSignal capturedSignal = gpsSignalCaptor.getValue();

        assertThat(capturedSignal.deviceUuid()).isEqualTo(uuid1);
        assertThat(capturedSignal.timestamp()).isEqualTo(oldSignal.timestamp());
        assertThat(capturedSignal.status()).isEqualTo(GpsSignal.DeviceStatus.INACTIVE);
    }

    @Test
    void givenOnlyRecentSignals_whenCheckForInactiveDevices_thenNoChangeShouldBeCalled() {
        // given
        LocalDateTime now = LocalDateTime.now();
        GpsSignal recentSignal = new GpsSignal(UUID.randomUUID(), 50.061, 19.938, now.minusSeconds(5), GpsSignal.DeviceStatus.ACTIVE);

        when(gpsSignalService.getLatestSignals()).thenReturn(List.of(recentSignal));

        // when
        gpsSignalScheduler.checkForInactiveDevices();

        // then
        verify(gpsSignalService, never()).consumeSignal(any());
    }
}
