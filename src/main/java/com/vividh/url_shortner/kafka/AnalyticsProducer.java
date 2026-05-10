package com.vividh.url_shortner.kafka;

import com.vividh.url_shortner.model.AnalyticsEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsProducer {

    public static final String TOPIC = "analytics-events";
    private final KafkaTemplate<String, AnalyticsEvent> kafkaTemplate;

    public AnalyticsProducer(KafkaTemplate<String, AnalyticsEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAnalyticsEvent(AnalyticsEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println("Published event to Kafka");
    }
}
