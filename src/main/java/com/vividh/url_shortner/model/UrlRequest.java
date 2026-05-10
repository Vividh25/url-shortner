package com.vividh.url_shortner.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequest {
    private String originalUrl;
    private LocalDateTime expiresAt;
}
