package pl.jakobczyk.gps.tracker.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;

@Configuration
@Profile("!integration-test")
public class GpsProducerConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
