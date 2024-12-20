package com.example.ecommerce.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "gcp.storage.enabled", havingValue = "true", matchIfMissing = false)
public class StorageConfig {

    @Value("${gcp.storage.bucket}")
    private String bucketName;

    @Bean
    public Storage storage() {
        return StorageOptions.getDefaultInstance().getService();
    }

    public String getBucketName() {
        return bucketName;
    }
} 