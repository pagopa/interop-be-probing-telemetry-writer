package it.pagopa.interop.probing.telemetry.writer.service.impl;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.telemetry.writer.dto.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.service.TimestreamService;
import it.pagopa.interop.probing.telemetry.writer.util.EserviceStatus;
import it.pagopa.interop.probing.telemetry.writer.util.logging.Logger;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.Dimension;
import software.amazon.awssdk.services.timestreamwrite.model.MeasureValue;
import software.amazon.awssdk.services.timestreamwrite.model.MeasureValueType;
import software.amazon.awssdk.services.timestreamwrite.model.Record;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsRequest;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsResponse;

@Service
public class TimestreamServiceImpl implements TimestreamService {

  @Autowired
  private TimestreamWriteClient writeClient;

  @Value("${amazon.timestream.database}")
  private String database;

  @Value("${amazon.timestream.table}")
  private String table;

  @Autowired
  private Logger logger;

  private static final String ESERVICE_RECORD_ID_DIMENSION = "eservice_record_id";

  private static final String KO_REASON_DIMENSION = "ko_reason";

  private static final String RESPONSE_TIME_MEASURE = "response_time";

  private static final String STATUS_MEASURE = "status";

  private static final String TELEMETRY_MEASURE_NAME = "telemetry";


  @Override
  public void writeRecord(TelemetryDto telemetry) {
    logger.logRequest(telemetry.getEserviceRecordId(), telemetry.getStatus(),
        telemetry.getResponseTime(), telemetry.getKoReason(), telemetry.getCheckTime());

    WriteRecordsRequest writeRecordsRequest = WriteRecordsRequest.builder().databaseName(database)
        .tableName(table)
        .commonAttributes(Record.builder()
            .dimensions(buildDimensions(telemetry.getEserviceRecordId(), telemetry.getKoReason()))
            .build())
        .records(buildMeasures(telemetry.getStatus(), telemetry.getResponseTime(),
            telemetry.getCheckTime()))
        .build();

    WriteRecordsResponse writeRecordsResponse = writeClient.writeRecords(writeRecordsRequest);
    logger.logWriteRecordStatus(database, table,
        writeRecordsResponse.sdkHttpResponse().statusCode());

  }

  private List<Dimension> buildDimensions(Long eserviceRecordId, String koReason) {
    return Objects.nonNull(koReason)
        ? List.of(
            Dimension.builder().name(ESERVICE_RECORD_ID_DIMENSION)
                .value(eserviceRecordId.toString()).build(),
            Dimension.builder().name(KO_REASON_DIMENSION).value(koReason).build())
        : List.of(Dimension.builder().name(ESERVICE_RECORD_ID_DIMENSION)
            .value(eserviceRecordId.toString()).build());
  }

  private List<Record> buildMeasures(EserviceStatus status, Long responseTime, String checkTime) {
    MeasureValue statusMeasure = MeasureValue.builder().name(STATUS_MEASURE)
        .type(MeasureValueType.VARCHAR).value(status.getValue()).build();

    List<Record> records = List.of();
    if (Objects.nonNull(responseTime)) {
      MeasureValue responseTimeMeasure = MeasureValue.builder().name(RESPONSE_TIME_MEASURE)
          .type(MeasureValueType.BIGINT).value(String.valueOf(responseTime)).build();
      records = List.of(Record.builder().measureName(TELEMETRY_MEASURE_NAME).time(checkTime)
          .measureValues(responseTimeMeasure, statusMeasure)
          .measureValueType(MeasureValueType.MULTI).build());
    } else {
      records = List.of(Record.builder().measureName(TELEMETRY_MEASURE_NAME).time(checkTime)
          .measureValues(statusMeasure).measureValueType(MeasureValueType.MULTI).build());
    }
    return records;
  }

}
