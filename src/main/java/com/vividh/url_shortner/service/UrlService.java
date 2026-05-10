package com.vividh.url_shortner.service;

import com.vividh.url_shortner.config.RedisConfig;
import com.vividh.url_shortner.exception.UrlNotFoundException;
import com.vividh.url_shortner.kafka.AnalyticsProducer;
import com.vividh.url_shortner.model.AnalyticsEvent;
import com.vividh.url_shortner.model.Url;
import com.vividh.url_shortner.model.UrlRequest;
import com.vividh.url_shortner.model.UrlResponse;
import com.vividh.url_shortner.repository.UrlRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    private final IdGeneratorService idGeneratorService;
    private final RedisTemplate<String, String> template;
    private final UrlRepository urlRepository;
    private final AnalyticsProducer analyticsProducer;

    public UrlService(IdGeneratorService idGeneratorService, RedisTemplate<String, String> template, UrlRepository urlRepository, AnalyticsProducer analyticsProducer) {
        this.idGeneratorService = idGeneratorService;
        this.template = template;
        this.urlRepository = urlRepository;
        this.analyticsProducer = analyticsProducer;
    }

    public UrlResponse createShortUrl(UrlRequest urlRequest) {
        String shortCode = idGeneratorService.generateShortCode();
        while (urlRepository.findByShortCode(shortCode).isPresent()) {
            shortCode = idGeneratorService.generateShortCode();
        }
        Url url = Url.builder()
                .shortCode(shortCode)
                .originalUrl(urlRequest.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .expiresAt(urlRequest.getExpiresAt())
                .clickCount(0)
                .build();

        urlRepository.save(url);

        String shortUrl = "https://localhost:8080/" + shortCode;

        template.opsForValue().set(shortCode, urlRequest.getOriginalUrl());

        return new UrlResponse(shortCode, urlRequest.getOriginalUrl(), shortUrl, LocalDateTime.now(), null);
    }

    public String getOriginalUrl(String shortCode) throws Exception {

        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        analyticsEvent.setShortCode(shortCode);
        analyticsProducer.sendAnalyticsEvent(analyticsEvent);

        String cached = template.opsForValue().get(shortCode);

        if (cached != null) return cached;

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not present in DB"));

        template.opsForValue().set(shortCode, url.getOriginalUrl());
        return url.getOriginalUrl();

    }

}
