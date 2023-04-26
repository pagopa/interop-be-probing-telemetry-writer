package it.pagopa.interop.probing.telemetry.writer.util.logging;

import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import software.amazon.awssdk.services.timestreamwrite.model.RejectedRecordsException;

public interface Logger {
  void logException(Exception e);

  void logRejectedRecords(RejectedRecordsException e);

  void logRejectedRecordDetail(Integer index, String reason);

  void logWriteRecordStatus(String database, String table, int statusCode);

  void logRequest(String serviceName, EserviceStatus status, Integer responseTime, String koReason,
      String checkTime);
}
