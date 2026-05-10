package com.vividh.url_shortner.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String shortCode;

    private String originalUrl;
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime expiresAt;

    private long clickCount = 0;
}
