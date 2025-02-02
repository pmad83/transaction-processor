package pl.pm.transactionprocessor.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import pl.pm.transactionprocessor.enums.KafkaTopic;

/*
 * Klasa KafkaProducerConfig wykorzystywana jest do tworzenia topiców na Kafce.
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(KafkaTopic.COMPLETED_TRANSACTIONS).partitions(1).replicas(1).build(),
                TopicBuilder.name(KafkaTopic.EXPIRED_TRANSACTIONS).partitions(1).replicas(1).build()
        );
    }

}
