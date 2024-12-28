package pl.jakobczyk.gps.tracker.producer.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.jakobczyk.gps.tracker.producer.GpsTrackerProducerApplication;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceStatus;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;
import pl.jakobczyk.gps.tracker.producer.dto.GpsCoord;
import pl.jakobczyk.gps.tracker.producer.integration.config.KafkaMockModAbstract;
import pl.jakobczyk.gps.tracker.producer.integration.config.TestClockConfig;
import pl.jakobczyk.gps.tracker.producer.service.VehicleService;
import pl.jakobczyk.gps.tracker.producer.util.FileLoaderUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = GpsTrackerProducerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Import(TestClockConfig.class)
public class GpsTrackerSimulatorControllerIT extends KafkaMockModAbstract {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void cleanUp() {
        vehicleService.replaceDevices(Collections.emptyList());
    }

    @Test
    void shouldReturnExpectedListOfGpsSignals() throws Exception {
        // given
        vehicleService.addDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512"), new GpsCoord(15.50, 25.32));
        vehicleService.addDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271513"), new GpsCoord(16.50, 26.32));

        // when then
        mockMvc.perform(get("/api/producer/gps"))
                .andExpect(status().isOk())
                .andExpect(content().json(FileLoaderUtil.loadExpectedResource("/expectedResponse/getAllExpected.json")));
    }

    @Test
    void shouldAddNewSignal() throws Exception {
        // when then
        mockMvc.perform(post("/api/producer/gps/device"))
                .andExpect(status().isNoContent());
        List<DeviceTracker> devices = vehicleService.getDevices();
        assertThat(devices).hasSize(1);
    }

    @Test
    void shouldRemoveExistingSignal() throws Exception {
        // given
        vehicleService.addDevice(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512"), new GpsCoord(15.50, 25.32));
        // when then
        mockMvc.perform(delete("/api/producer/gps/device/c487a0d8-ede1-4d25-b61e-f9862f271512"))
                .andExpect(status().isNoContent());
        List<DeviceTracker> devices = vehicleService.getDevices();
        assertThat(devices).hasSize(1);
        assertThat(devices).usingRecursiveComparison().isEqualTo(List.of(
                new DeviceTracker(UUID.fromString("c487a0d8-ede1-4d25-b61e-f9862f271512"), new GpsCoord(15.50, 25.32), LocalDateTime.parse("2024-01-01T00:00"), DeviceStatus.DELETED)
        ));
    }

}
