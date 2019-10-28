package com.osdu.service.google;

import com.google.common.collect.ImmutableMap;
import com.osdu.model.job.IngestJob;
import com.osdu.model.job.IngestJobStatus;
import com.osdu.model.job.IngestJobStatusDto;
import com.osdu.repository.IngestJobRepository;
import com.osdu.service.JobStatusService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GcpJobStatusService implements JobStatusService {

  final IngestJobRepository ingestJobRepository;

  @Override
  public IngestJobStatusDto getStatus(String jobId, MessageHeaders headers) {
    log.info("Request for getting a injection job status. JobId: {}, headers: {}", jobId, headers);
    IngestJob job = ingestJobRepository.findById(jobId);

    log.info("Found the injection job: {}", job);
    return IngestJobStatusDto.fromIngestJob(job);
  }

  @Override
  public IngestJob get(String jobId) {
    log.info("Request for getting a injection job. JobId: {}", jobId);
    return ingestJobRepository.findById(jobId);
  }

  @Override
  public String initInjectJob() {
    log.info("Initiating a new injection job");
    String jobId = UUID.randomUUID().toString();
    ingestJobRepository.save(IngestJob.builder()
        .id(jobId)
        .status(IngestJobStatus.CREATED)
        .build());

    log.info("Created a new running injection job. JobId: {}", jobId);
    return jobId;
  }

  @Override
  public void updateJobStatus(String jobId, IngestJobStatus status) {
    log.info("Update the injection job status. JobId: {}, status: {}", jobId, status);
    ingestJobRepository.updateFields(jobId, ImmutableMap.of("status", status.name()));
    log.info("Updated the injection job status. Status: {}", status);
  }

  @Override
  public void save(IngestJob ingestJob) {
    log.info("Update the ingestion job. Job: {}", ingestJob);
    ingestJobRepository.save(ingestJob);
  }
}