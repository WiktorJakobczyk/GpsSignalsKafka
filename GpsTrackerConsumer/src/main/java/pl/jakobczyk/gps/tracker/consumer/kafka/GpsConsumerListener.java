package pl.jakobczyk.gps.tracker.consumer.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;
import pl.jakobczyk.gps.tracker.consumer.service.GpsSignalService;
import pl.jakobczyk.gps.tracker.consumer.utils.JsonMapper;

@Component
@RequiredArgsConstructor
public class GpsConsumerListener {

    private static final String KAFKA_GPS_TOPIC = "gps-signals";
    private static final String KAFKA_GPS_GROUP = "gps-signals-receiver";

    private final GpsSignalService gpsSignalService;

    @KafkaListener(
            topics = KAFKA_GPS_TOPIC,
            groupId = KAFKA_GPS_GROUP
    )
    public void listenTopicGps(String data) {
        GpsSignal gpsSignal = JsonMapper.mapToGpsSignalDto(data);
        gpsSignalService.consumeSignal(gpsSignal);
    }
}
