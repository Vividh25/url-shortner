package com.vividh.url_shortner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortCode;

    private LocalDateTime clickedAt;

    private String ipAddress;

    private String userAgent;
}
