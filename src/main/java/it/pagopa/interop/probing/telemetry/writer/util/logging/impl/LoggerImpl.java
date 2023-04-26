package it.pagopa.interop.probing.telemetry.writer.util.logging.impl;

import java.util.UUID;
import org.springframework.stereotype.Component;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import it.pagopa.interop.probing.telemetry.writer.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.timestreamwrite.model.RejectedRecordsException;


@Slf4j
@Component
public class LoggerImpl implements Logger {

  @Override
  public void logRequest(UUID eserviceRecordId, EserviceStatus status, Integer responseTime,
      String koReason, String checkTime) {
    log.info(
        "Inserting measure. eserviceRecordId={} , status={} , responseTime={}, koReason={}, checktime={}",
        eserviceRecordId, status.getValue(), responseTime, koReason, checkTime);
  }

  @Override
  public void logException(Exception e) {
    log.error("Error: {}", e);
  }

  @Override
  public void logRejectedRecords(RejectedRecordsException e) {
    log.error("RejectedRecords: {}", e);
  }

  @Override
  public void logRejectedRecordDetail(Integer index, String reason) {
    log.error("Rejected Index {}: {}", index, reason);
  }

  @Override
  public void logWriteRecordStatus(String database, String table, int statusCode) {
    log.info("WriteRecords on table {}.{} status: {}", database, table, statusCode);
  }

}
