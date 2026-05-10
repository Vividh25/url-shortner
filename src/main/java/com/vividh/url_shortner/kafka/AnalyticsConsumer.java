package com.vividh.url_shortner.kafka;

import com.vividh.url_shortner.model.AnalyticsEvent;
import com.vividh.url_shortner.repository.AnalyticsEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnalyticsConsumer {
    private final AnalyticsEventRepository analyticsEventRepository;

    public AnalyticsConsumer(AnalyticsEventRepository analyticsEventRepository) {
        this.analyticsEventRepository = analyticsEventRepository;
    }

    @KafkaListener(topics = "analytics-events", groupId = "analytics-group")
    public void consume(AnalyticsEvent analyticsEvent) {
        analyticsEvent.setClickedAt(LocalDateTime.now());
        analyticsEventRepository.save(analyticsEvent);
        System.out.println("Consumes event from Kafka");
    }
}
