package pl.jakobczyk.gps.tracker.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.jakobczyk.gps.tracker.producer.dto.DeviceTracker;

import java.util.List;
import java.util.UUID;


@Component

public class GpsProducer {
    private final String gpsTrackTopicName;
    private final KafkaProducer<UUID, Object> kafkaProducer;

    private static final ObjectWriter OBJECT_WRITER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .writer().withDefaultPrettyPrinter();

    public GpsProducer(KafkaProducer<UUID, Object> kafkaProducer, @Value("${gps.signals.topic.name}") String gpsTrackTopicName) {
        this.kafkaProducer = kafkaProducer;
        this.gpsTrackTopicName = gpsTrackTopicName;
    }

    public void publish(DeviceTracker vehicle) {
        publish(List.of(vehicle));
    }

    public void publish(List<DeviceTracker> vehicles) {
        vehicles.forEach(veh -> {
            try {
                String jsonData = OBJECT_WRITER.writeValueAsString(veh);
                ProducerRecord<UUID, Object> gpsSignalRecord = new ProducerRecord<>(gpsTrackTopicName, veh.uuid(), jsonData);
                kafkaProducer.send(gpsSignalRecord);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
