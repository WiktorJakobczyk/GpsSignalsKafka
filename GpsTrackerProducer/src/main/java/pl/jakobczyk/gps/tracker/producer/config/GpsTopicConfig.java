package pl.jakobczyk.gps.tracker.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class GpsTopicConfig {
    @Value("${gps.signals.topic.name}")
    private String gpsTrackTopicName;
    @Value("${gps.tracker.topic.partitions}")
    private Integer partitions;
    @Value("${gps.tracker.topic.replication.factor}")
    private Integer replicas;

    @Bean
    public NewTopic gpsTrackTopic() {
        return TopicBuilder.name(gpsTrackTopicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
