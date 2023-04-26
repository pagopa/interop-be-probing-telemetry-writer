package it.pagopa.interop.probing.telemetry.writer.config.database;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;

@Configuration
public class TimestreamConfig {

  @Value("${amazon.timestream.retries}")
  private Integer retries;

  @Value("${amazon.timestream.max.connections}")
  private Integer maxConnections;

  @Value("${amazon.timestream.attempt.timeout}")
  private Integer attemptTimeout;


  @Bean
  public TimestreamWriteClient buildWriteClient() {
    ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder();
    httpClientBuilder.maxConnections(maxConnections);

    RetryPolicy.Builder retryPolicy = RetryPolicy.builder();
    retryPolicy.numRetries(retries);

    ClientOverrideConfiguration.Builder overrideConfig = ClientOverrideConfiguration.builder();
    overrideConfig.apiCallAttemptTimeout(Duration.ofSeconds(attemptTimeout));
    overrideConfig.retryPolicy(retryPolicy.build());

    return TimestreamWriteClient.builder().httpClientBuilder(httpClientBuilder)
        .overrideConfiguration(overrideConfig.build())
        .credentialsProvider(DefaultCredentialsProvider.create()).build();
  }
}
