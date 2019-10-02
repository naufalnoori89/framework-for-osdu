package com.osdu.model.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.osdu.model.BaseJsonObject;
import lombok.Data;

@Data
public class File extends BaseJsonObject {

  @JsonProperty(value = "Data")
  FileDataObject data;
}
