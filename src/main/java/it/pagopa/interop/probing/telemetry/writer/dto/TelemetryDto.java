package it.pagopa.interop.probing.telemetry.writer.dto;

import java.util.UUID;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import lombok.Data;

@Data
public class TelemetryDto {

  private UUID eserviceRecordId;

  private EserviceStatus status;

  private Integer responseTime;

  private String koReason;

  private String checkTime;

}
