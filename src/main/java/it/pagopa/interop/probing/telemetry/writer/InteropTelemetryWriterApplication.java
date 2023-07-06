package it.pagopa.interop.probing.telemetry.writer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy
public class InteropTelemetryWriterApplication {

  public static void main(String[] args) {
    SpringApplication.run(InteropTelemetryWriterApplication.class, args);
  }

}
