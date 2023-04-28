package it.pagopa.interop.probing.telemetry.writer.util.logging.impl;

import org.springframework.stereotype.Component;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import it.pagopa.interop.probing.telemetry.writer.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class LoggerImpl implements Logger {

  @Override
  public void logRequest(Long eserviceRecordId, EserviceStatus status, Long responseTime,
      String koReason, String checkTime) {
    log.info(
        "Inserting measure. eserviceRecordId={} , status={} , responseTime={}, koReason={}, checktime={}",
        eserviceRecordId, status.getValue(), responseTime, koReason, checkTime);
  }

  @Override
  public void logWriteRecordStatus(String database, String table, int statusCode) {
    log.info("WriteRecords on table {}.{} status: {}", database, table, statusCode);
  }

  @Override
  public void logConsumerMessage(String message) {
    log.info("Message received : {}", message);
  }

}
