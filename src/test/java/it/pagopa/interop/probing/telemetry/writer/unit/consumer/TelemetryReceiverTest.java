package it.pagopa.interop.probing.telemetry.writer.unit.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import it.pagopa.interop.probing.telemetry.writer.consumer.TelemetryReceiver;
import it.pagopa.interop.probing.telemetry.writer.dto.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.service.TimestreamService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TelemetryReceiverTest {

  @InjectMocks
  @Autowired
  private TelemetryReceiver telemetryReceiver;

  @Mock
  private TimestreamService timestreamService;

  @MockBean
  private SimpleMessageListenerContainer simpleMessageListenerContainer;

  @Autowired
  private ObjectMapper mapper;


  @Test
  @DisplayName("The receiveMessage method of TelemetryReceiver handles valid message successfully.")
  void testReceiveMessage_thenDoesNotThrowException() throws IOException {
    String message =
        "{\"eserviceRecordId\":3,\"status\":\"KO\",\"responseTime\":null,\"koReason\":\"exception\",\"checkTime\":\"1683023923118\"}";
    Mockito.doNothing().when(timestreamService)
        .writeRecord(mapper.readValue(message, TelemetryDto.class));
    assertDoesNotThrow(() -> telemetryReceiver.receiveMessage(message));
  }

  @Test
  @DisplayName("The receiveMessage method of TelemetryReceiver throws a ConstraintViolationException in case of an invalid message.")
  void testReceiveMessage_thenDoesThrowValidationException() throws IOException {
    String message =
        "{\"eserviceRecordId\":3,\"status\":\"OK\",\"responseTime\":null,\"koReason\":\"exception\",\"checkTime\":\"1683023923118\"}";
    assertThrows(ConstraintViolationException.class,
        () -> telemetryReceiver.receiveMessage(message));
  }


}
