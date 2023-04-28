package it.pagopa.interop.probing.telemetry.writer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import it.pagopa.interop.probing.telemetry.writer.annotations.validator.TelemetryValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TelemetryValidator.class)
public @interface ValidateTelemetry {

  String message() default "A telemetry with an OK status must have a response time and no ko reason, while one with a KO status must have a ko reason and no response time";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
