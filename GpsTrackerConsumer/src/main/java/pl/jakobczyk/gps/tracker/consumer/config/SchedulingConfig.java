package pl.jakobczyk.gps.tracker.consumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile("!integration-test")
@EnableScheduling
public class SchedulingConfig {
}
