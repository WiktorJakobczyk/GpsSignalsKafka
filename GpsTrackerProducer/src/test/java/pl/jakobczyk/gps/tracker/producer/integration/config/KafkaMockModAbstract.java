package pl.jakobczyk.gps.tracker.producer.integration.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singleton;

@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:0" })
public class KafkaMockModAbstract {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    protected Consumer<UUID, String> consumer;

    @BeforeEach
    public void prepareMock() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "true", embeddedKafka));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new DefaultKafkaConsumerFactory<>(configs, new UUIDDeserializer(), new StringDeserializer()).createConsumer();
        consumer.subscribe(singleton("gps-signals"));
    }

    @AfterEach
    public void cleanUp() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
