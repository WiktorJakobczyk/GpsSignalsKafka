package pl.jakobczyk.gps.tracker.producer.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StringUtils;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GpsProducerTest {

    @SuppressWarnings("unchecked")
    KafkaProducer<UUID, Object> mockProducer = (KafkaProducer<UUID, Object>) mock(KafkaProducer.class);

    @InjectMocks
    private GpsProducer gpsProducer;

    @BeforeEach
    void setUp() {
        gpsProducer = new GpsProducer(mockProducer, "topic_test");
    }

    @Test
    void givenDevice_whenPublish_thenKafkaProducerSendCalled() {
        // given
        UUID uuid = UUID.randomUUID();
        DeviceTracker device = new DeviceTracker(uuid, null, null, null);

        // when
        gpsProducer.publish(device);

        // then
        verify(mockProducer).send(any());
    }

    @Test
    void givenDevices_whenPublish_thenKafkaProducerSendCalledForEachDevice() {
        // given
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        DeviceTracker device1 = new DeviceTracker(uuid1, null, null, null);
        DeviceTracker device2 = new DeviceTracker(uuid2, null, null, null);
        List<DeviceTracker> devices = List.of(device1, device2);

        // when
        gpsProducer.publish(devices);

        // then
        verify(mockProducer, times(2)).send(any());
    }

    @Test
    void givenDevice_whenPublish_thenKafkaProducerSendCorrectContent() {
        // given
        UUID uuid = UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512");
        DeviceTracker device = new DeviceTracker(uuid, null, null, null);
        String topicName = "topic_test";
        String expectedValue = """
                {
                  "uuid" : "c487a0d8-ede1-4d25-b61e-f9862f271512",
                  "coords" : null,
                  "timestamp" : null,
                  "status" : null
                }
                """;

        // when
        gpsProducer.publish(device);

        // then
        verify(mockProducer).send(argThat(message ->
                message.topic().equals(topicName)
                        && message.key().equals(uuid)
                        && StringUtils.trimAllWhitespace(message.value().toString()).equals(StringUtils.trimAllWhitespace(expectedValue))));
    }
}
