package it.pagopa.interop.probing.telemetry.writer.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import it.pagopa.interop.probing.telemetry.writer.dto.TelemetryDto;
import it.pagopa.interop.probing.telemetry.writer.service.TimestreamService;
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
  TimestreamWriteClient writeClient;

  @Value("${amazon.timestream.database}")
  private String database;

  @Value("${amazon.timestream.table}")
  private String table;

  @Autowired
  Logger logger;

  private static final String ID = "id";

  private static final String KO_REASON = "ko_reason";

  private static final String RESPONSE_TIME = "response_time";

  private static final String STATUS = "status";

  private static final String TELEMETRY = "telemetry";


  @Override
  public void writeRecord(TelemetryDto telemetry) {
    logger.logRequest(telemetry.getEserviceRecordId(), telemetry.getStatus(),
        telemetry.getResponseTime(), telemetry.getKoReason(), telemetry.getCheckTime());
    List<Record> records = new ArrayList<>();

    List<Dimension> dimensions = new ArrayList<>();
    final Dimension id =
        Dimension.builder().name(ID).value(telemetry.getEserviceRecordId().toString()).build();
    final Dimension koReason =
        Dimension.builder().name(KO_REASON).value(telemetry.getKoReason()).build();

    dimensions.add(id);
    dimensions.add(koReason);

    Record commonAttributes = Record.builder().dimensions(dimensions).build();

    MeasureValue responseTime = MeasureValue.builder().name(RESPONSE_TIME)
        .type(MeasureValueType.BIGINT).value(String.valueOf(telemetry.getResponseTime())).build();
    MeasureValue status = MeasureValue.builder().name(STATUS).type(MeasureValueType.VARCHAR)
        .value(telemetry.getStatus().getValue()).build();


    Record pollingMeasure = Record.builder().measureName(TELEMETRY).time(telemetry.getCheckTime())
        .measureValues(responseTime, status).measureValueType(MeasureValueType.MULTI).build();

    records.add(pollingMeasure);

    WriteRecordsRequest writeRecordsRequest = WriteRecordsRequest.builder().databaseName(database)
        .tableName(table).commonAttributes(commonAttributes).records(records).build();

    WriteRecordsResponse writeRecordsResponse = writeClient.writeRecords(writeRecordsRequest);
    logger.logWriteRecordStatus(database, table,
        writeRecordsResponse.sdkHttpResponse().statusCode());

  }

}
