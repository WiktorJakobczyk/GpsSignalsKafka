package pl.jakobczyk.gps.tracker.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GpsTaskTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GpsSignalService gpsSignalService;

    @InjectMocks
    private GpsTask gpsTask;

    @Test
    void givenSignalsToSend_whenSendLatestGpsPosition_shouldSendCorrectGpsPositions() throws JsonProcessingException {
        // given
        LocalDateTime dateTime = LocalDateTime.parse("2024-12-20T13:59:24");
        GpsSignal gpsSignal = new GpsSignal(
                UUID.fromString("f64abdd8-e3dd-46b6-b5cf-65c9558eaa4f"),
                10.5,
                20.5,
                dateTime,
                GpsSignal.DeviceStatus.ACTIVE
        );
        given(gpsSignalService.getLatestSignals()).willReturn(List.of(gpsSignal));

        String expectedJson = """
                [ {
                  "deviceUuid" : "f64abdd8-e3dd-46b6-b5cf-65c9558eaa4f",
                  "latitude" : 10.5,
                  "longitude" : 20.5,
                  "timestamp" : "2024-12-20T13:59:24",
                  "status" : "ACTIVE"
                } ]""";

        // when
        gpsTask.sendLatestGpsPosition();

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), captor.capture());
        ObjectMapper objectMapper = new ObjectMapper();
        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(captor.getValue()));
    }
}
