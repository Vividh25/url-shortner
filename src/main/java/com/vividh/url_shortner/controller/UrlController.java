package com.vividh.url_shortner.controller;

import com.vividh.url_shortner.model.UrlRequest;
import com.vividh.url_shortner.model.UrlResponse;
import com.vividh.url_shortner.service.RateLimiterService;
import com.vividh.url_shortner.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@CrossOrigin
public class UrlController {
    private final RateLimiterService rateLimiterService;
    private final UrlService urlService;

    public UrlController(RateLimiterService rateLimiterService, UrlService urlService) {
        this.urlService = urlService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest urlRequest) {
        return ResponseEntity.ok(urlService.createShortUrl(urlRequest));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode, HttpServletRequest httpServletRequest) throws Exception {
        String IpAddress = httpServletRequest.getRemoteAddr();
        if (!rateLimiterService.isAllowed(IpAddress))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        String url = urlService.getOriginalUrl(shortCode);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

}
