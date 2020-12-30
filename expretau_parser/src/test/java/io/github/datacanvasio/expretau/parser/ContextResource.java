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

package io.github.datacanvasio.expretau.parser;

import io.github.datacanvasio.expretau.runtime.schema.DataFormat;
import io.github.datacanvasio.expretau.runtime.schema.DataParser;
import io.github.datacanvasio.expretau.runtime.schema.RtData;
import io.github.datacanvasio.expretau.runtime.schema.RtSchema;
import io.github.datacanvasio.expretau.runtime.schema.RtSchemaRoot;
import io.github.datacanvasio.expretau.schema.SchemaParser;
import lombok.Getter;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ContextResource implements BeforeAllCallback {
    private final String ctxFileName;
    private final String[] etxStrings;

    @Getter
    private RtSchemaRoot schemaRoot;
    private RtData[] datum;

    /**
     * Create a ContextResource.
     *
     * @param ctxFileName the file name of the schema
     * @param etxStrings  several datum in YAML format
     */
    public ContextResource(String ctxFileName, String... etxStrings) {
        this.ctxFileName = ctxFileName;
        this.etxStrings = etxStrings;
    }

    /**
     * Get the RtSchema corresponding the schema file.
     *
     * @return the RtSchema
     */
    public RtSchema getCtx() {
        return schemaRoot.getSchema();
    }

    /**
     * Get the RtData corresponding to the data.
     *
     * @param index the index of the data
     * @return the RtData
     */
    public RtData getEtx(int index) {
        return datum[index];
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        schemaRoot = SchemaParser.get(DataFormat.fromExtension(ctxFileName))
            .parse(ContextResource.class.getResourceAsStream(ctxFileName));
        DataParser parser = DataParser.yaml().schema(schemaRoot);
        datum = new RtData[etxStrings.length];
        for (int i = 0; i < datum.length; i++) {
            datum[i] = parser.parse(etxStrings[i]);
        }
    }
}
