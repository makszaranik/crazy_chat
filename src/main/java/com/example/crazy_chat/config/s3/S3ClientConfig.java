package com.example.crazy_chat.config.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;


@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {

    private final S3PropertiesHolder s3PropertiesHolder;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            s3PropertiesHolder.accessKey(),
            s3PropertiesHolder.secretKey()
        );

        return S3Client.builder()
            .endpointOverride(URI.create(s3PropertiesHolder.endpoint()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.US_EAST_1)
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build())
            .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            s3PropertiesHolder.accessKey(),
            s3PropertiesHolder.secretKey()
        );

        return S3Presigner.builder()
            .endpointOverride(URI.create(s3PropertiesHolder.endpoint()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.US_EAST_1)
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build())
            .build();
    }

    @ConfigurationProperties(prefix = "s3")
    public record S3PropertiesHolder(
        String endpoint,
        String accessKey,
        String secretKey,
        String bucket
    ) {}

}

