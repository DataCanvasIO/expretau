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

package io.github.datacanvasio.expretau.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.datacanvasio.expretau.runtime.schema.DataFormat;
import io.github.datacanvasio.expretau.runtime.schema.ParserFactory;
import io.github.datacanvasio.expretau.runtime.schema.RtSchemaRoot;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;

public class SchemaParser extends ParserFactory {
    public static final SchemaParser JSON = new SchemaParser(DataFormat.APPLICATION_JSON);
    public static final SchemaParser YAML = new SchemaParser(DataFormat.APPLICATION_YAML);

    private static final long serialVersionUID = -99489992589189349L;

    private SchemaParser(@Nonnull DataFormat format) {
        super(format);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Get the SchemaParser of the specified DataFormat.
     *
     * @param format the DataFormat
     * @return the SchemaParser
     */
    @Nonnull
    public static SchemaParser get(@Nonnull DataFormat format) {
        switch (format) {
            case APPLICATION_JSON:
                return JSON;
            case APPLICATION_YAML:
                return YAML;
            default:
                throw new IllegalArgumentException("Unsupported format \"" + format + "\".");
        }
    }

    /**
     * Parse a input String into an RtSchemaRoot.
     *
     * @param json the input String
     * @return the RtSchemaRoot
     * @throws JsonProcessingException if something is wrong
     */
    public RtSchemaRoot parse(String json) throws JsonProcessingException {
        Schema schema = mapper.readValue(json, Schema.class);
        return new RtSchemaRoot(schema.createRtSchema());
    }

    /**
     * Read an given InputStream and parse the contents into an RtSchemaRoot.
     *
     * @param is the input String
     * @return the RtSchemaRoot
     * @throws IOException if something is wrong
     */
    public RtSchemaRoot parse(InputStream is) throws IOException {
        Schema schema = mapper.readValue(is, Schema.class);
        return new RtSchemaRoot(schema.createRtSchema());
    }

    /**
     * Serialize (the RtSchema of) an RtSchemaRoot into a String.
     *
     * @param rtSchemaRoot the RtSchemaRoot
     * @return the String
     * @throws JsonProcessingException if something is wrong
     */
    public String serialize(RtSchemaRoot rtSchemaRoot) throws JsonProcessingException {
        return mapper.writeValueAsString(rtSchemaRoot.getSchema());
    }
}
