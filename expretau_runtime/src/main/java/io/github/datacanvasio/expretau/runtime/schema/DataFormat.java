/*
 * Copyright 2020 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.datacanvasio.expretau.runtime.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public enum DataFormat {
    APPLICATION_JSON("application/json"),
    APPLICATION_YAML("application/yaml");

    @JsonValue
    private final String value;

    DataFormat(String value) {
        this.value = value;
    }

    /**
     * Get a DataFormat enum from a String.
     * {@code "application/json"} -> {@code APPLICATION_JSON}
     * {@code "application/yaml"} -> {@code APPLICATION_YAML}
     *
     * @param str the string
     * @return the DataFormat
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    @Nonnull
    public static DataFormat fromString(String str) {
        for (DataFormat dataFormat : DataFormat.values()) {
            if (str.equals(dataFormat.getValue())) {
                return dataFormat;
            }
        }
        throw new IllegalArgumentException("Invalid string value \"" + str
            + "\" for enum type \"" + DataFormat.class.getSimpleName() + "\".");
    }

    /**
     * Get a DataFormat enum according a fileName.
     * {@code "*.json"} -> {@code APPLICATION_JSON}
     * {@code "*.yaml"} -> {@code APPLICATION_YAML}
     * {@code "*.yml"} -> {@code APPLICATION_YAML}
     *
     * @param fileName the file name
     * @return the DataFormat
     */
    @Nonnull
    public static DataFormat fromExtension(@Nonnull String fileName) {
        if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            return APPLICATION_YAML;
        } else if (fileName.endsWith(".json")) {
            return APPLICATION_JSON;
        }
        throw new IllegalArgumentException("Invalid extension of file name \""
            + fileName + "\" for enum type \"" + DataFormat.class.getSimpleName() + "\".");
    }

    @Override
    public String toString() {
        return value;
    }
}
