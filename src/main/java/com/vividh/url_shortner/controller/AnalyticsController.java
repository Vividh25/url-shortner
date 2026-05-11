package com.vividh.url_shortner.controller;

import com.vividh.url_shortner.model.AnalyticsEvent;
import com.vividh.url_shortner.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class AnalyticsController {

    private final UrlService urlService;

    public AnalyticsController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(path = "/api/analytics/{shortCode}")
    public ResponseEntity<List<AnalyticsEvent>> getAnalytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getAnalytics(shortCode));
    }
}
