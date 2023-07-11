package it.pagopa.interop.probing.telemetry.writer.consumer;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.TraceHeader;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import it.pagopa.interop.probing.telemetry.writer.dto.impl.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.service.TimestreamService;
import it.pagopa.interop.probing.telemetry.writer.util.logging.Logger;
import it.pagopa.interop.probing.telemetry.writer.util.logging.LoggingPlaceholders;

@Component
@XRayEnabled
public class TelemetryReceiver {

  @Autowired
  private TimestreamService timestreamService;
  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private Logger logger;

  @Value("${spring.application.name}")
  private String awsXraySegmentName;

  @SqsListener(value = "${amazon.sqs.endpoint.telemetry-queue}",
      deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
  public void receiveMessage(final Message message) throws JsonProcessingException {

    String traceHeaderStr = message.getAttributes().get("AWSTraceHeader");
    TraceHeader traceHeader = TraceHeader.fromString(traceHeaderStr);
    if (AWSXRay.getCurrentSegmentOptional().isEmpty()) {
      AWSXRay.getGlobalRecorder().beginSegment(awsXraySegmentName, traceHeader.getRootTraceId(),
          traceHeader.getParentId());
    }
    MDC.put(LoggingPlaceholders.TRACE_ID_XRAY_PLACEHOLDER,
        LoggingPlaceholders.TRACE_ID_XRAY_MDC_PREFIX
            + AWSXRay.getCurrentSegment().getTraceId().toString() + "]");

    logger.logConsumerMessage(message.getBody());
    try {
      timestreamService.writeRecord(mapper.readValue(message.getBody(), TelemetryDto.class));
    } finally {
      MDC.remove(LoggingPlaceholders.TRACE_ID_XRAY_PLACEHOLDER);
    }
    AWSXRay.endSegment();
  }
}
