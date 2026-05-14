package com.vividh.url_shortner.repository;

import com.vividh.url_shortner.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);
    void deleteByShortCode(String shortCode);
}
