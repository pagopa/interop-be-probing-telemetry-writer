package it.pagopa.interop.probing.telemetry.writer.util.logging;

import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;

public interface Logger {

  void logConsumerMessage(String message);

  void logWriteRecordStatus(String database, String table, int statusCode);

  void logRequest(Long eserviceRecordId, EserviceStatus status, Integer responseTime,
      String koReason, String checkTime);
}
