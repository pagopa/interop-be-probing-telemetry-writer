package it.pagopa.interop.probing.telemetry.writer.dto;

import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import lombok.Data;

@Data
public class TelemetryDto {

  private String serviceName;

  private EserviceStatus status;

  private Integer responseTime;

  private String koReason;

  private String checkTime;

}
