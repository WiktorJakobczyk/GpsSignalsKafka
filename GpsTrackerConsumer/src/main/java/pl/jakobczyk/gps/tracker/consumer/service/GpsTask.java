package pl.jakobczyk.gps.tracker.consumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.jakobczyk.gps.tracker.consumer.dto.GpsSignal;
import pl.jakobczyk.gps.tracker.consumer.utils.JsonMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GpsTask {

    private final SimpMessagingTemplate messagingTemplate;
    private final GpsSignalService gpsSignalService;

    @Scheduled(fixedRate = 500)
    public void sendLatestGpsPosition() {
        List<GpsSignal> latestSignals = gpsSignalService.getLatestSignals();
        String jsonData = JsonMapper.mapGpsSignalsToJson(latestSignals);
        messagingTemplate.convertAndSend("/topic/gps", jsonData);
    }
}
