package pl.jakobczyk.gps.tracker.consumer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;
import pl.jakobczyk.gps.tracker.consumer.integration.config.KafkaMockModAbstract;
import pl.jakobczyk.gps.tracker.consumer.integration.config.StompClientAbstract;
import pl.jakobczyk.gps.tracker.consumer.integration.config.TestClockConfig;
import pl.jakobczyk.gps.tracker.consumer.service.GpsSignalService;
import pl.jakobczyk.gps.tracker.consumer.service.GpsTask;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Import(TestClockConfig.class)
public class WebSocketIntegrationTest extends KafkaMockModAbstract {

    @Autowired
    private GpsSignalService gpsSignalService;

    @Autowired
    private GpsTask gpsTask;

    @LocalServerPort
    private Integer port;

    private static final UUID UUID_1 = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f41");
    private static final UUID UUID_2 = UUID.fromString("5f503256-d252-45b8-b8a6-92f29ab24f42");

    @AfterEach
    public void cleanUp() {
        gpsSignalService.consumeSignal(new GpsSignal(UUID_1, 0, 0, LocalDateTime.now(), GpsSignal.DeviceStatus.DELETED));
        gpsSignalService.consumeSignal(new GpsSignal(UUID_2, 0, 0, LocalDateTime.now(), GpsSignal.DeviceStatus.DELETED));
    }

    @Test
    public void shouldReceiveAGpsSignalsFromSubscribedTopicFromWebsocket() throws Exception {
        // given
        StompClientAbstract stompClient = new StompClientAbstract(new ArrayBlockingQueue<>(1)) {
            @Override
            protected StompSessionHandlerAdapter sessionHandler() {
                return new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        // Prepare mocked data after stomp connection
                        gpsSignalService.consumeSignal(new GpsSignal(UUID_1, 15.49, 25.21, LocalDateTime.parse("2024-01-01T00:00:00"), GpsSignal.DeviceStatus.ACTIVE));
                    }
                };
            }
        };

        stompClient.setUpWebsocketConnection(port);

        // when
        gpsTask.sendLatestGpsPosition();

        // then
        String expected = """
                [ {
                  "deviceUuid" : "5f503256-d252-45b8-b8a6-92f29ab24f41",
                  "latitude" : 15.49,
                  "longitude" : 25.21,
                  "timestamp" : "2024-01-01T00:00:00",
                  "status" : "ACTIVE"
                } ]""";
        ObjectMapper objectMapper = new ObjectMapper();
        String poll = stompClient.blockingQueue.poll(5, SECONDS);
        if (poll == null) {
            fail("No message from WebSocket received.");
        }
        assertThat(objectMapper.readTree(poll)).isEqualTo(objectMapper.readTree(expected));
    }

    @Test
    public void shouldReceiveAGpsSignalsFromSubscribedTopicFromWebsocketMultipleStatuses() throws Exception {
        // given
        StompClientAbstract stompClient = new StompClientAbstract(new ArrayBlockingQueue<>(2)) {
            @Override
            protected StompSessionHandlerAdapter sessionHandler() {
                return new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        // Prepare mocked data after stomp connection
                        gpsSignalService.consumeSignal(new GpsSignal(UUID_1, 15.49, 25.21, LocalDateTime.parse("2024-01-01T00:00:00"), GpsSignal.DeviceStatus.ACTIVE));
                        gpsSignalService.consumeSignal(new GpsSignal(UUID_2, 12.49, 22.21, LocalDateTime.parse("2024-01-01T00:00:00"), GpsSignal.DeviceStatus.INACTIVE));
                    }
                };
            }
        };

        stompClient.setUpWebsocketConnection(port);

        // when
        gpsTask.sendLatestGpsPosition();

        // then
        String expected = """
                [ {
                  "deviceUuid" : "5f503256-d252-45b8-b8a6-92f29ab24f41",
                  "latitude" : 15.49,
                  "longitude" : 25.21,
                  "timestamp" : "2024-01-01T00:00:00",
                  "status" : "ACTIVE"
                },
                {
                  "deviceUuid" : "5f503256-d252-45b8-b8a6-92f29ab24f42",
                  "latitude" : 12.49,
                  "longitude" : 22.21,
                  "timestamp" : "2024-01-01T00:00:00",
                  "status" : "INACTIVE"
                } ]""";
        ObjectMapper objectMapper = new ObjectMapper();
        String poll = stompClient.blockingQueue.poll(5, SECONDS);
        if (poll == null) {
            fail("No message from WebSocket received.");
        }
        assertThat(objectMapper.readTree(poll)).isEqualTo(objectMapper.readTree(expected));
    }
}
