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

  @Value("${amazon.timestream.maxConnections}")
  private Integer maxConnections;

  @Value("${amazon.timestream.attemptTimeout}")
  private Integer attemptTimeout;


  @Bean
  public TimestreamWriteClient buildWriteClient() {
    return TimestreamWriteClient.builder()
        .httpClientBuilder(ApacheHttpClient.builder().maxConnections(maxConnections))
        .overrideConfiguration(ClientOverrideConfiguration.builder()
            .apiCallAttemptTimeout(Duration.ofSeconds(attemptTimeout))
            .retryPolicy(RetryPolicy.builder().numRetries(retries).build()).build())
        .credentialsProvider(DefaultCredentialsProvider.create()).build();
  }
}
