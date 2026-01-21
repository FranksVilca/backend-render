package com.ensolvers.notes_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;

/**
 * Configuration to handle Render's DATABASE_URL format conversion.
 * Render provides postgres:// but Spring Boot needs jdbc:postgresql://
 */
@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @PostConstruct
    public void init() {
        if (!databaseUrl.isEmpty()) {
            String jdbcUrl = databaseUrl;

            // Convert postgres:// to jdbc:postgresql://
            if (databaseUrl.startsWith("postgres://")) {
                jdbcUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
            }
            // Convert postgresql:// to jdbc:postgresql://
            else if (databaseUrl.startsWith("postgresql://")) {
                jdbcUrl = "jdbc:" + databaseUrl;
            }

            // Set as system property so Spring can use it
            if (!jdbcUrl.equals(databaseUrl)) {
                System.setProperty("JDBC_DATABASE_URL", jdbcUrl);
                System.out.println("✓ Converted DATABASE_URL to JDBC format for Spring Boot");
            }
        }
    }
}
