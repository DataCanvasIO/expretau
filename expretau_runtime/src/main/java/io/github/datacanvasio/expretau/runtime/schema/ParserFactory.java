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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.Serializable;
import javax.annotation.Nonnull;

public abstract class ParserFactory implements Serializable {
    private static final long serialVersionUID = 3619076300814598699L;

    protected final ObjectMapper mapper;

    public ParserFactory(@Nonnull DataFormat format) {
        switch (format) {
            case APPLICATION_JSON:
                mapper = new JsonMapper();
                break;
            case APPLICATION_YAML:
                YAMLFactory yamlFactory = new YAMLFactory()
                    .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
                mapper = new ObjectMapper(yamlFactory);
                break;
            default:
                throw new IllegalArgumentException("Invalid DataFormat value \"" + format
                    + "\" for ParserFactory.");
        }
    }
}