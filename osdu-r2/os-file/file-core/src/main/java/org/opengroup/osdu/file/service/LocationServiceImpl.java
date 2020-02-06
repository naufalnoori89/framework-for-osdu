/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengroup.osdu.file.service;

import static java.lang.String.format;

import java.util.Date;
import java.util.UUID;
import javax.inject.Named;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opengroup.osdu.core.common.model.file.DriverType;
import org.opengroup.osdu.core.common.model.file.FileLocation;
import org.opengroup.osdu.core.common.model.file.FileLocationRequest;
import org.opengroup.osdu.core.common.model.file.FileLocationResponse;
import org.opengroup.osdu.core.common.model.file.LocationRequest;
import org.opengroup.osdu.core.common.model.file.LocationResponse;
import org.opengroup.osdu.file.exception.FileLocationNotFoundException;
import org.opengroup.osdu.file.exception.LocationAlreadyExistsException;
import org.opengroup.osdu.file.mapper.HeadersMapper;
import org.opengroup.osdu.file.mapper.LocationMapper;
import org.opengroup.osdu.file.model.Headers;
import org.opengroup.osdu.file.model.SignedUrl;
import org.opengroup.osdu.file.repository.FileLocationRepository;
import org.opengroup.osdu.file.validation.ValidationService;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

  @Named
  final HeadersMapper headersMapper;
  final LocationMapper locationMapper;
  final AuthenticationService authenticationService;
  final ValidationService validationService;
  final FileLocationRepository fileLocationRepository;
  final StorageService storageService;

  @Override
  public LocationResponse getLocation(LocationRequest request, MessageHeaders messageHeaders) {
    log.debug("Request to create location for file upload with parameters : {}, and headers, {}",
        request, messageHeaders);
    Headers headers = headersMapper.toHeaders(messageHeaders);

    authenticationService.checkAuthentication(headers.getAuthorizationToken(),
        headers.getPartitionID());
    validationService.validateLocationRequest(request);
    checkExisting(request);

    String fileID = getFileID(request);

    log.debug("Create the empty blob in bucket. FileID : {}", fileID);
    SignedUrl signedUrl = storageService.createSignedUrl(fileID, headers.getAuthorizationToken(),
        headers.getPartitionID());
    log.debug("Signed URL for fileID = {} : {}", fileID, signedUrl);

    FileLocation fileLocation = FileLocation.builder()
        .fileID(fileID)
        .driver(DriverType.GCS)
        .location(signedUrl.getUri().toString())
        .createdBy(signedUrl.getCreatedBy())
        .createdAt(Date.from(signedUrl.getCreatedAt()))
        .build();

    log.debug("Save file location document : {}", fileLocation);
    FileLocation saved = fileLocationRepository.save(fileLocation);

    LocationResponse response = locationMapper.buildLocationResponse(signedUrl, saved);
    log.debug("Location creation result : {}", response);
    return response;
  }

  @Override
  public FileLocationResponse getFileLocation(FileLocationRequest request,
      MessageHeaders messageHeaders) {
    log.debug("Request file location with parameters : {}, and headers, {}",
        request, messageHeaders);
    Headers headers = headersMapper.toHeaders(messageHeaders);

    authenticationService.checkAuthentication(headers.getAuthorizationToken(),
        headers.getPartitionID());
    validationService.validateFileLocationRequest(request);

    String fileID = request.getFileID();
    FileLocation fileLocation = fileLocationRepository.findByFileID(fileID);

    if (fileLocation == null) {
      throw new FileLocationNotFoundException("Not found location for fileID : " + fileID);
    }

    FileLocationResponse response = FileLocationResponse.builder()
        .driver(fileLocation.getDriver())
        .location(fileLocation.getLocation())
        .build();

    log.debug("File Location result : {}", response);
    return response;
  }

  private boolean exists(String fileID) {
    FileLocation fileLocation = fileLocationRepository.findByFileID(fileID);
    return fileLocation != null;
  }

  private String getFileID(LocationRequest request) {
    return request.getFileID() == null ? getUuidString() : request.getFileID();
  }

  private String getUuidString() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private void checkExisting(LocationRequest request) {
    String fileID = request.getFileID();
    if (exists(fileID)) {
      throw new LocationAlreadyExistsException(format("Location for fileID = %s already exists", fileID));
    }
  }

}
