package it.pagopa.interop.probing.telemetry.writer.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.util.ReflectionTestUtils;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import it.pagopa.interop.probing.telemetry.writer.dto.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.service.TimestreamService;
import it.pagopa.interop.probing.telemetry.writer.service.impl.TimestreamServiceImpl;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsRequest;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsResponse;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TimestreamServiceTest {

  @InjectMocks
  @Autowired
  private TimestreamService timestreamService = new TimestreamServiceImpl();

  @MockBean
  private SimpleMessageListenerContainer simpleMessageListenerContainer;

  private TelemetryDto input;

  @Mock
  private TimestreamWriteClient writeClient;

  @Mock
  private WriteRecordsResponse writeRecordsResponse;

  @Mock
  private SdkHttpResponse sdkHttpResponse;



  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(timestreamService, "table", "testTable");
    ReflectionTestUtils.setField(timestreamService, "database", "testDatabase");
    input = TelemetryDto.builder().eserviceRecordId(3L).status(EserviceStatus.KO)
        .koReason("exception").checkTime("1683023923118").build();
  }

  @Test
  @DisplayName("The writeRecord method successfully build a writing request.")
  void testWriteRecord_thenDoesNotThrowException() throws IOException {
    Mockito.when(writeClient.writeRecords(Mockito.any(WriteRecordsRequest.class)))
        .thenReturn(writeRecordsResponse);
    Mockito.when(writeRecordsResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);
    Mockito.when(sdkHttpResponse.statusCode()).thenReturn(200);
    assertDoesNotThrow(() -> timestreamService.writeRecord(input));
  }
}
