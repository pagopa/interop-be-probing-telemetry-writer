package it.pagopa.interop.probing.telemetry.writer.unit.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.xray.AWSXRay;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import it.pagopa.interop.probing.telemetry.writer.consumer.TelemetryReceiver;
import it.pagopa.interop.probing.telemetry.writer.dto.impl.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.service.TimestreamService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TelemetryReceiverTest {

  @InjectMocks
  @Autowired
  private TelemetryReceiver telemetryReceiver;

  @MockBean
  private TimestreamService timestreamService;

  @MockBean
  private SimpleMessageListenerContainer simpleMessageListenerContainer;

  @Autowired
  private ObjectMapper mapper;

  private Map<String, String> attributes = new HashMap<>();
  private String mockedId = "mockedId";
  Message message = new Message();

  @BeforeEach
  void setup() {
    AWSXRay.beginSegment("Segment-test");
    attributes.put("AWSTraceHeader", mockedId);
  }

  @AfterEach
  void clean() {
    AWSXRay.endSegment();
  }

  @Test
  @DisplayName("The receiveMessage method of TelemetryReceiver handles valid message successfully.")
  void testReceiveMessage_thenDoesNotThrowException() throws IOException {
    String messageString =
        "{\"eserviceRecordId\":3,\"status\":\"KO\",\"responseTime\":null,\"koReason\":\"exception\",\"checkTime\":\"1683023923118\"}";
    message.setBody(messageString);
    message.setAttributes(attributes);
    Mockito.doNothing().when(timestreamService)
        .writeRecord(mapper.readValue(messageString, TelemetryDto.class));
    assertDoesNotThrow(() -> telemetryReceiver.receiveMessage(message));
  }

  @Test
  @DisplayName("The receiveMessage method of TelemetryReceiver throws a ConstraintViolationException in case of an invalid message.")
  void testReceiveMessage_thenDoesThrowValidationException() throws IOException {
    assertThrows(IllegalArgumentException.class, () -> telemetryReceiver.receiveMessage(message));
  }


}
