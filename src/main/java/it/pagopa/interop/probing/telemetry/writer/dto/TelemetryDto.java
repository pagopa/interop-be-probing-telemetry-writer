package it.pagopa.interop.probing.telemetry.writer.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import lombok.Data;

@Data
public class TelemetryDto {

  @NotNull(message = "must not be null")
  @Min(value = 1, message = "must be at least 1")
  private Long eserviceRecordId;

  @NotNull(message = "must not be null")
  private EserviceStatus status;

  @NotNull(message = "must not be null")
  @Min(value = 1, message = "must be at least 1")
  private Integer responseTime;

  @NotBlank(message = "must not be blank")
  private String koReason;

  @NotBlank(message = "must not be blank")
  private String checkTime;

}
