package com.osdu.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osdu.exception.SrnMappingException;
import com.osdu.model.SchemaData;
import com.osdu.model.dto.SchemaDataDto;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SchemaDataMapperDecorator implements
    SchemaDataMapper {

  @Inject
  @Named("com.osdu.mapper.SchemaDataMapperImpl_")
  SchemaDataMapper schemaDataMapper;

  ObjectMapper objectMapper;

  public SchemaDataMapperDecorator() {
    objectMapper = new ObjectMapper();
  }

  @Override
  public SchemaDataDto schemaDataToSchemaDataDto(SchemaData schemaData) {
    log.debug("Request to map schemaData to schemaDataDto : {} ", schemaData);
    SchemaDataDto schemaDataDto = schemaDataMapper.schemaDataToSchemaDataDto(schemaData);
    try {
      schemaDataDto.setSchema(objectMapper.writeValueAsString(schemaData.getSchema()));
      log.debug("Result of mapping: {} ", schemaDataDto);
      return schemaDataDto;
    } catch (JsonProcessingException e) {
      throw new SrnMappingException(
          String.format("Failed to map schemaData to schemaDataDto : %s", schemaData), e);
    }

  }

  @Override
  public SchemaData schemaDataDtoToSchemaData(SchemaDataDto schemaDataDto) {
    log.debug("Request to map schemaDataDto to schemaData : {} ", schemaDataDto);
    SchemaData schemaData = schemaDataMapper.schemaDataDtoToSchemaData(schemaDataDto);
    try {
      schemaData.setSchema(objectMapper.readTree(schemaDataDto.getSchema()));
      log.debug("Result of mapping: {} ", schemaData);
      return schemaData;
    } catch (IOException e) {
      throw new SrnMappingException(
          String.format("Failed to map schemaDataDto to schemaData : %s", schemaDataDto), e);
    }
  }
}