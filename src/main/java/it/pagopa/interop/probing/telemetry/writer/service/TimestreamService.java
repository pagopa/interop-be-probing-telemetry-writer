package it.pagopa.interop.probing.telemetry.writer.service;

import it.pagopa.interop.probing.telemetry.writer.dto.impl.TelemetryDto;

public interface TimestreamService {

  void writeRecord(TelemetryDto telemetry);
}
