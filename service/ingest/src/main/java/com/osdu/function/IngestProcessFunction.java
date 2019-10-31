package com.osdu.function;

import com.osdu.model.IngestProcessRequest;
import com.osdu.model.job.IngestJob;
import com.osdu.model.job.IngestJobStatus;
import com.osdu.model.job.IngestMessage;
import com.osdu.service.JobStatusService;
import com.osdu.service.processing.InnerIngestionProcess;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IngestProcessFunction implements
    Function<Message<IngestProcessRequest>, Message<Boolean>> {

  final InnerIngestionProcess ingestionProcess;
  final JobStatusService jobStatusService;

  @Override
  public Message<Boolean> apply(Message<IngestProcessRequest> request) {
    log.info("Ingest processing request received with following parameters: {}", request);

    IngestMessage ingestMessage = request.getPayload().getMessage().getIngestMessage();
    if (ingestMessage == null) {
      log.error("Ingest message is null. Abort processing.");
      return new GenericMessage<>(false);
    }
    String ingestJobId = ingestMessage.getIngestJobId();

    IngestJob ingestJob = jobStatusService.get(ingestJobId);
    if (ingestJob.getStatus() != IngestJobStatus.CREATED) {
      log.warn("Ingestion job (jobId: {}) is already processing (status: {}). Ignore this message",
          ingestJobId, ingestJob.getStatus());
    } else {
      ingestionProcess.process(ingestJobId, ingestMessage.getLoadManifest(),
          ingestMessage.getHeaders());
      log.debug("Finish ingest processing. JobId: {}", ingestJobId);
    }

    log.info("Ingest processing response ready. Request: {}, response: {}", request, null);
    return new GenericMessage<>(true);
  }
}
