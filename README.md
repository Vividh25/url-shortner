# URL Shortener

A production-grade URL shortening service built with Java and Spring Boot, demonstrating core system design concepts including caching, rate limiting, async event processing, and distributed ID generation.

## Features
- Shorten long URLs to compact short codes
- Redirect short URLs to original destinations
- Redis-based caching for sub-millisecond redirect lookups
- Sliding window rate limiter using Redis sorted sets
- Async click analytics via Kafka
- URL expiry support
- Click count tracking
- IP address and user agent tracking per click
- REST API with global exception handling and input validation

## Tech Stack
- Java 17, Spring Boot 3.2.5
- PostgreSQL — persistent storage
- Redis — caching and rate limiting
- Apache Kafka — async analytics processing
- Spring Data JPA, Maven

## System Design Concepts Demonstrated

**Caching (Cache-Aside Pattern)**
Redirect lookups check Redis first. On cache miss, PostgreSQL is queried and the result is cached for subsequent requests. This gives sub-millisecond p99 latency for repeat redirects.

**Rate Limiting (Sliding Window)**
Each IP address is limited to 10 requests per minute using a Redis sorted set. Timestamps are stored as scores — old entries are pruned on every request, giving an accurate sliding window count without fixed reset intervals.

**Async Event Processing**
Every redirect publishes an analytics event to a Kafka topic. A consumer picks it up asynchronously and persists it to PostgreSQL. Redirect performance is never blocked by analytics write throughput.

**Distributed ID Generation**
Short codes are generated using Base62 encoding with SecureRandom, giving 56 billion possible combinations for 6-character codes. Collision detection retries generation until a unique code is found.

**URL Expiry**
URLs can be created with an optional expiry timestamp. Expired URLs return an appropriate error response instead of redirecting.

## API Endpoints

### Create a short URL
POST /api/urls
```json
{
    "originalUrl": "https://www.example.com",
    "expiresAt": "2026-12-31T23:59:59"
}
```

### Redirect to original URL
GET /{shortCode}

### Get all URLs
GET /api/urls

### Delete a URL
DELETE /api/urls/{shortCode}

### Get analytics for a short code
GET /api/analytics/{shortCode}

## Setup

**Prerequisites:** Java 17, PostgreSQL, Redis, Apache Kafka

1. Clone the repo
2. Create a PostgreSQL database called `urlshortnerdb`
3. Copy `application.properties.example` to `application.properties` and fill in your credentials
4. Start Redis: `brew services start redis`
5. Start Kafka and create the topic:
```bash
bin/kafka-server-start.sh config/server.properties
bin/kafka-topics.sh --create --topic analytics-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```
6. Run `mvn spring-boot:run -DskipTests`

## Known Tradeoffs
- Click count only increments on cache miss — cached redirects don't update the counter. In production this would be handled with a Redis counter synced to PostgreSQL periodically.
- Short code generation uses random Base62 — a Snowflake-style ID generator would be more suitable for distributed deployments.
