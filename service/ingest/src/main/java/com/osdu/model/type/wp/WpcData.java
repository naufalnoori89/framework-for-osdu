/*
 * Copyright 2019 Google LLC
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

package com.osdu.model.type.wp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.osdu.model.type.base.OsduObjectData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class WpcData extends OsduObjectData {

  @JsonIgnore
  WpcGroupTypeProperties groupTypeProperties;

  @JsonIgnore
  WpcIndividualTypeProperties individualTypeProperties;

  @Override
  @JsonProperty("GroupTypeProperties")
  public WpcGroupTypeProperties getGroupTypeProperties() {
    return groupTypeProperties;
  }

  @Override
  @JsonProperty("IndividualTypeProperties")
  public WpcIndividualTypeProperties getIndividualTypeProperties() {
    return individualTypeProperties;
  }

}
