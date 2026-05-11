package com.vividh.url_shortner.exception;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String shortCode) {

        super("URL is expired. Shortcode: " + shortCode);
    }
}
