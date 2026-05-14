package com.vividh.url_shortner.service;

import com.vividh.url_shortner.exception.UrlExpiredException;
import com.vividh.url_shortner.exception.UrlNotFoundException;
import com.vividh.url_shortner.kafka.AnalyticsProducer;
import com.vividh.url_shortner.model.AnalyticsEvent;
import com.vividh.url_shortner.model.Url;
import com.vividh.url_shortner.model.UrlRequest;
import com.vividh.url_shortner.model.UrlResponse;
import com.vividh.url_shortner.repository.AnalyticsEventRepository;
import com.vividh.url_shortner.repository.UrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlService {
    private final IdGeneratorService idGeneratorService;
    private final RedisTemplate<String, String> template;
    private final UrlRepository urlRepository;
    private final AnalyticsProducer analyticsProducer;
    private final AnalyticsEventRepository analyticsEventRepository;

    public UrlService(IdGeneratorService idGeneratorService,
                      RedisTemplate<String, String> template,
                      UrlRepository urlRepository,
                      AnalyticsProducer analyticsProducer,
                      AnalyticsEventRepository analyticsEventRepository
    ) {
        this.idGeneratorService = idGeneratorService;
        this.template = template;
        this.urlRepository = urlRepository;
        this.analyticsProducer = analyticsProducer;
        this.analyticsEventRepository = analyticsEventRepository;
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

        String shortUrl = "http://localhost:8080/" + shortCode;

        template.opsForValue().set(shortCode, urlRequest.getOriginalUrl());

        return new UrlResponse(shortCode, urlRequest.getOriginalUrl(), shortUrl, LocalDateTime.now(), null);
    }

    public String getOriginalUrl(String shortCode, String userAgent, String IpAddress) throws Exception {

        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        analyticsEvent.setShortCode(shortCode);
        analyticsEvent.setIpAddress(IpAddress);
        analyticsEvent.setUserAgent(userAgent);

        System.out.println("IP: " + IpAddress + "UserAgent: " + userAgent);

        analyticsProducer.sendAnalyticsEvent(analyticsEvent);

        String cached = template.opsForValue().get(shortCode);

        if (cached != null) return cached;

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not present in DB"));

        LocalDateTime expireTime = url.getExpiresAt();
        if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Url is expired");
        }

        long clickCount = url.getClickCount();
        clickCount++;
        url.setClickCount(clickCount);
        urlRepository.save(url);

        template.opsForValue().set(shortCode, url.getOriginalUrl());
        return url.getOriginalUrl();

    }

    public List<AnalyticsEvent> getAnalytics(String shortCode) {
        return analyticsEventRepository.findByShortCode(shortCode);
    }

    public List<UrlResponse> getAllUrls() {
        List<Url> urls = urlRepository.findAll();
        return urls.stream()
                .map(url -> new UrlResponse(url.getShortCode(), url.getOriginalUrl(), "http://localhost:8080/" + url.getShortCode(), url.getCreatedAt(), url.getExpiresAt()))
                .toList();
    }

    @Transactional
    public void deleteUrlByShortCode(String shortCode) {
        urlRepository.deleteByShortCode(shortCode);
        template.delete(shortCode);
    }

}
