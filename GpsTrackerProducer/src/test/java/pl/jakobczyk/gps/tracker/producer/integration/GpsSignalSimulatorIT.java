package pl.jakobczyk.gps.tracker.producer.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.jakobczyk.gps.tracker.producer.GpsTrackerProducerApplication;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceStatus;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;
import pl.jakobczyk.gps.tracker.producer.dto.GpsCoord;
import pl.jakobczyk.gps.tracker.producer.integration.config.KafkaMockModAbstract;
import pl.jakobczyk.gps.tracker.producer.integration.config.TestClockConfig;
import pl.jakobczyk.gps.tracker.producer.service.GpsSignalSimulator;
import pl.jakobczyk.gps.tracker.producer.service.VehicleService;
import pl.jakobczyk.gps.tracker.producer.util.FileLoaderUtil;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GpsTrackerProducerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Import(TestClockConfig.class)
public class GpsSignalSimulatorIT extends KafkaMockModAbstract {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private GpsSignalSimulator gpsSignalSimulator;

    @AfterEach
    void cleanDeviceData() {
        vehicleService.replaceDevices(Collections.emptyList());
    }

    @Test
    void shouldSimulateGpsWhenOnlyActiveDevicesPresentAndSendCorrectKafkaMessageToTopic() throws JsonProcessingException {
        // given
        vehicleService.addDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512"), new GpsCoord(15.50, 25.32));
        vehicleService.addDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271513"), new GpsCoord(16.50, 26.32));

        // when
        gpsSignalSimulator.simulateMovement();

        // then
        String expectedMessages = FileLoaderUtil.loadExpectedResource("/expectedKafkaMessage/activeSignalsOnly.json");
        JsonNode expectedNodes = objectMapper.readTree(expectedMessages);

        List<DeviceTracker> devicesNewSignals = vehicleService.getDevices();
        checkDevice(devicesNewSignals.get(0), UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512"), 15.50, 25.32);
        checkDevice(devicesNewSignals.get(1), UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271513"), 16.50, 26.32);

        ConsumerRecords<UUID, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10L));
        List<String> jsonRecords = StreamSupport.stream(records.records("gps-signals").spliterator(), false)
                .map(ConsumerRecord::value)
                .toList();
        assertThat(jsonRecords).hasSize(2);
        for (int index = 0; index < jsonRecords.size(); index++) {
            JsonNode jsonNode = objectMapper.readTree(jsonRecords.get(index));
            checkKafkaMessage(jsonNode, expectedNodes.get(index));
        }
    }

    @Test
    void shouldSimulateGpsWhenOActiveDevicesAndOneIsRemovedAndSendCorrectKafkaMessageToTopic() throws JsonProcessingException {
        // given
        vehicleService.addDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512"), new GpsCoord(15.50, 25.32));
        vehicleService.addDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271513"), new GpsCoord(16.50, 26.32));
        vehicleService.removeDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271513"));

        // when
        gpsSignalSimulator.simulateMovement();

        // then
        String expectedMessages = FileLoaderUtil.loadExpectedResource("/expectedKafkaMessage/withDeleteSignal.json");
        JsonNode expectedNodes = objectMapper.readTree(expectedMessages);

        List<DeviceTracker> devicesNewSignals = vehicleService.getDevices();
        checkDevice(devicesNewSignals.getFirst(), UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512"), 15.50, 25.32);

        ConsumerRecords<UUID, String> records = KafkaTestUtils.getRecords(consumer);
        List<String> jsonRecords = StreamSupport.stream(records.records("gps-signals").spliterator(), false)
                .map(ConsumerRecord::value)
                .toList();
        assertThat(jsonRecords).hasSize(2);
        for (int index = 0; index < jsonRecords.size(); index++) {
            JsonNode jsonNode = objectMapper.readTree(jsonRecords.get(index));
            checkKafkaMessage(jsonNode, expectedNodes.get(index));
        }
    }

    private void checkDevice(DeviceTracker device, UUID expectedUuid, double expectedLatitude, double expectedLongitude) {
        assertThat(device.uuid()).isEqualTo(expectedUuid);
        assertThat(device.status()).isEqualTo(DeviceStatus.ACTIVE);
        assertThat(device.coords().latitude()).isNotEqualTo(expectedLatitude);
        assertThat(device.coords().longitude()).isNotEqualTo(expectedLongitude);
    }

    private void checkKafkaMessage(JsonNode actualNode, JsonNode expectedNode) {
        assertThat(actualNode.get("uuid").asText()).isEqualTo(expectedNode.get("uuid").asText());
        assertThat(actualNode.get("timestamp").asText()).isEqualTo(expectedNode.get("timestamp").asText());
        assertThat(actualNode.get("status").asText()).isEqualTo(expectedNode.get("status").asText());
    }

}
