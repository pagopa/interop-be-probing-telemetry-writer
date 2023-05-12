package it.pagopa.interop.probing.telemetry.writer.consumer;

import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import it.pagopa.interop.probing.telemetry.writer.dto.impl.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.service.TimestreamService;
import it.pagopa.interop.probing.telemetry.writer.util.logging.Logger;
import it.pagopa.interop.probing.telemetry.writer.util.logging.LoggingPlaceholders;

@Component
public class TelemetryReceiver {

  @Autowired
  private TimestreamService timestreamService;
  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private Logger logger;

  @SqsListener(value = "${amazon.sqs.endpoint.telemetry-queue}",
      deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
  public void receiveMessage(final String message)
      throws JsonMappingException, JsonProcessingException {
    MDC.put(LoggingPlaceholders.TRACE_ID_PLACEHOLDER,
        "- [CID= " + UUID.randomUUID().toString().toLowerCase() + "]");
    logger.logConsumerMessage(message);
    try {
      timestreamService.writeRecord(mapper.readValue(message, TelemetryDto.class));
    } finally {
      MDC.remove(LoggingPlaceholders.TRACE_ID_PLACEHOLDER);
    }
  }
}
