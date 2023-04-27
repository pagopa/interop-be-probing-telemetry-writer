package it.pagopa.interop.probing.telemetry.writer.service;

import it.pagopa.interop.probing.telemetry.writer.dto.TelemetryDto;

public interface TimestreamService {

  void writeRecord(TelemetryDto telemetry);
}
