package it.pagopa.interop.probing.telemetry.writer.annotations.validator;

import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import it.pagopa.interop.probing.telemetry.writer.annotations.ValidateTelemetry;
import it.pagopa.interop.probing.telemetry.writer.dto.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;

public class TelemetryValidator implements ConstraintValidator<ValidateTelemetry, TelemetryDto> {

  @Override
  public boolean isValid(TelemetryDto dto, ConstraintValidatorContext context) {
    return Objects.isNull(dto.getStatus())
        || dto.getStatus().equals(EserviceStatus.KO) && Objects.nonNull(dto.getKoReason())
            && Objects.isNull(dto.getResponseTime())
        || dto.getStatus().equals(EserviceStatus.OK) && Objects.nonNull(dto.getResponseTime())
            && Objects.isNull(dto.getKoReason());
  }

}
