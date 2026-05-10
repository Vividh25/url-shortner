package com.vividh.url_shortner.repository;

import com.vividh.url_shortner.model.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {
    List<AnalyticsEvent> findByShortCode(String shortCode);
}
