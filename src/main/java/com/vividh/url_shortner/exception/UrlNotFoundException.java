package com.vividh.url_shortner.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String shortCode) {

        super("Url not found with shortcode: " + shortCode);
    }
}
