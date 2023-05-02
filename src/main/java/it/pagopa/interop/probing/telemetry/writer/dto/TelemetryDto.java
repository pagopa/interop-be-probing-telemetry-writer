package it.pagopa.interop.probing.telemetry.writer.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import it.pagopa.interop.probing.telemetry.writer.annotations.ValidateTelemetry;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidateTelemetry
public class TelemetryDto {

  @NotNull(message = "must not be null")
  @Min(value = 1, message = "must be at least 1")
  private Long eserviceRecordId;

  @NotNull(message = "must not be null")
  private EserviceStatus status;

  @Min(value = 1, message = "must be at least 1")
  private Long responseTime;

  @Size(min = 1, max = 2048, message = "must be at least 1 and less than or equal to 2048")
  private String koReason;

  @NotBlank(message = "must not be blank")
  @Pattern(regexp = "\\d+", message = "should be numeric")
  private String checkTime;

}
