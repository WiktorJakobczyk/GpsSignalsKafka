package pl.jakobczyk.gps.tracker.consumer.integration;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.jakobczyk.gps.tracker.consumer.GpsTrackerConsumerApplication;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;
import pl.jakobczyk.gps.tracker.consumer.integration.config.KafkaMockModAbstract;
import pl.jakobczyk.gps.tracker.consumer.integration.config.TestClockConfig;
import pl.jakobczyk.gps.tracker.consumer.service.GpsSignalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GpsTrackerConsumerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Import(TestClockConfig.class)
class GpsConsumerListenerIT extends KafkaMockModAbstract {

    @Autowired
    private GpsSignalService gpsSignalService;

    @Test
    void shouldProcessKafkaMessageWithListenerClass() {
        // given
        String jsonData = """
                  {
                    "uuid": "5f503256-d252-45b8-b8a6-92f29ab24f41",
                    "coords": {
                      "latitude": 15.49,
                      "longitude": 25.21
                    },
                    "timestamp": "2024-01-01T00:00:00",
                    "status": "ACTIVE"
                  }
                """;
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f41");
        ProducerRecord<UUID, String> gpsSignalRecord = new ProducerRecord<>("gps-signals", uuid, jsonData);
        producer.send(gpsSignalRecord);

        // then
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(gpsSignalService.getLatestSignals()).hasSize(1);
                    assertThat(gpsSignalService.getLatestSignals()).usingRecursiveComparison().isEqualTo(List.of(
                            new GpsSignal(uuid, 15.49, 25.21, LocalDateTime.parse("2024-01-01T00:00:00"), GpsSignal.DeviceStatus.ACTIVE)
                    ));
                });
    }

    @Test
    void shouldProcessKafkaDeletedMessageWithListenerClass() {
        // given
        String jsonData = """
                  {
                    "uuid": "5f503256-d252-45b8-b8a6-92f29ab24f41",
                    "coords": {
                      "latitude": 15.49,
                      "longitude": 25.21
                    },
                    "timestamp": "2024-01-01T00:00:05",
                    "status": "DELETED"
                  }
                """;
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f41");

        gpsSignalService.consumeSignal(new GpsSignal(uuid, 15.49, 25.21, LocalDateTime.parse("2024-01-01T00:00:00"), GpsSignal.DeviceStatus.ACTIVE));

        ProducerRecord<UUID, String> gpsSignalRecord = new ProducerRecord<>("gps-signals", uuid, jsonData);
        producer.send(gpsSignalRecord);

        // then
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(gpsSignalService.getLatestSignals()).isEmpty();
                });
    }

    @Test
    void shouldProcessKafkaInactiveMessageWithListenerClass() {
        // given
        String jsonData = """
                  {
                    "uuid": "5f503256-d252-45b8-b8a6-92f29ab24f41",
                    "coords": {
                      "latitude": 15.49,
                      "longitude": 25.21
                    },
                    "timestamp": "2024-01-01T00:00:05",
                    "status": "INACTIVE"
                  }
                """;
        UUID uuid = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f41");

        gpsSignalService.consumeSignal(new GpsSignal(uuid, 15.49, 25.21, LocalDateTime.parse("2024-01-01T00:00:00"), GpsSignal.DeviceStatus.ACTIVE));

        ProducerRecord<UUID, String> gpsSignalRecord = new ProducerRecord<>("gps-signals", uuid, jsonData);
        producer.send(gpsSignalRecord);

        // then
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(gpsSignalService.getLatestSignals()).hasSize(1);
                    assertThat(gpsSignalService.getLatestSignals()).usingRecursiveComparison().isEqualTo(List.of(
                            new GpsSignal(uuid, 15.49, 25.21, LocalDateTime.parse("2024-01-01T00:00:05"), GpsSignal.DeviceStatus.INACTIVE)
                    ));
                });
    }

}
