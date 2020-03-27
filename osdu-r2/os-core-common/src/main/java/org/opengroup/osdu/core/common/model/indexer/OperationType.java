/*
 * Copyright 2020 Google LLC
 * Copyright 2017-2019, Schlumberger
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

package org.opengroup.osdu.core.common.model.indexer;

public enum OperationType {
    /**
     * A post operation
     */
    create("create"),

    /**
     * A delete operation
     */
    delete("delete"),

    /**
     * A purge operation
     */
    purge("purge"),

    /**
     * A patch operation
     */
    update("update"),

    /*
    * create schema operation
    * */
    create_schema("create_schema"),

    /*
    * purge schema operation
    * */
    purge_schema("purge_schema");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
