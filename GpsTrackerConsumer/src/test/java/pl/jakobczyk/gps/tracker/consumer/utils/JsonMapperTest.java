package pl.jakobczyk.gps.tracker.consumer.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JsonMapperTest {

    @Test
    void givenValidGpsSignals_whenMapGpsSignalsToJson_thenJsonStringShouldBeCreated() {
        // given
        UUID deviceUuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f41");
        LocalDateTime dateTime = LocalDateTime.parse("2024-12-20T13:59:24");
        GpsSignal signal = new GpsSignal(deviceUuid, 52.2296756, 21.0122287, dateTime, GpsSignal.DeviceStatus.ACTIVE);
        List<GpsSignal> signals = List.of(signal);

        // when
        String json = JsonMapper.mapGpsSignalsToJson(signals);

        // then
        String expectedJson = """
                [ {
                  "deviceUuid" : "5f503256-d252-45b8-b8a6-92f29ab24f41",
                  "latitude" : 52.2296756,
                  "longitude" : 21.0122287,
                  "timestamp" : "2024-12-20T13:59:24",
                  "status" : "ACTIVE"
                } ]""";
        assertThat(json).isEqualToNormalizingNewlines(expectedJson);
    }

    @Test
    void giveEmptyList_whenMapGpsSignalsToJson_thenJsonStringShouldBeCreated() {
        // given
        List<GpsSignal> signals = List.of();

        // when
        String json = JsonMapper.mapGpsSignalsToJson(signals);

        // then
        assertThat(json).isEqualTo("[ ]");
    }


    @Test
    void givenValidJson_whenMapToGpsSignalDto_thenGpsSignalObjectShouldBeCreated() {
        // given
        String json = """
                { "uuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                  "status": "ACTIVE",
                  "coords": {
                    "latitude": 52.2296756,
                    "longitude": 21.0122287
                    },
                  "timestamp": "2024-12-20T12:30:00"
                }""";

        // when
        GpsSignal gpsSignal = JsonMapper.mapToGpsSignalDto(json);

        // then
        assertThat(gpsSignal).usingRecursiveComparison().isEqualTo(new GpsSignal(
                UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
                52.2296756,
                21.0122287,
                LocalDateTime.parse("2024-12-20T12:30:00"),
                GpsSignal.DeviceStatus.ACTIVE
        ));
    }

    @Test
    void givenInvalidUuidFormat_whenMapToGpsSignalDto_thenIllegalArgumentExceptionShouldBeThrown() {
        // given
        String invalidJson = """
                { "uuid": "1234",
                  "status": "ACTIVE",
                  "coords": {
                    "latitude": 52.2296756,
                    "longitude": 21.0122287
                    },
                  "timestamp": "Not_a_date_time"
                }""";

        // when then
        assertThatThrownBy(() -> JsonMapper.mapToGpsSignalDto(invalidJson))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid UUID string: 1234" );
    }

    @Test
    void givenInvalidTimestampFormat_whenMapToGpsSignalDto_thenIllegalArgumentExceptionShouldBeThrown() {
        // given
        String invalidJson = """
                { "uuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                  "status": "ACTIVE",
                  "coords": {
                    "latitude": 52.2296756,
                    "longitude": 21.0122287
                    },
                  "timestamp": "invalid-timestamp"
                }""";

        // when then
        assertThatThrownBy(() -> JsonMapper.mapToGpsSignalDto(invalidJson))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid timestamp format: invalid-timestamp");
    }
}
