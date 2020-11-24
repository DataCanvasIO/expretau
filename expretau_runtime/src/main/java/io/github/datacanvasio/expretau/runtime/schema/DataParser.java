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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.datacanvasio.expretau.runtime.TypeCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DataParser extends ParserFactory {
    private static final long serialVersionUID = -6849693677072717377L;

    private RtSchemaRoot schemaRoot;

    private DataParser(@Nonnull DataFormat format) {
        super(format);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Create a DataParser of json format.
     *
     * @return a DataParser
     */
    @Nonnull
    public static DataParser json() {
        return new DataParser(DataFormat.APPLICATION_JSON);
    }

    /**
     * Create a DataParser of yaml format.
     *
     * @return a new DataParser
     */
    @Nonnull
    public static DataParser yaml() {
        return new DataParser(DataFormat.APPLICATION_YAML);
    }

    /**
     * Create a DataParser of a specified format.
     *
     * @param format the DataFormat
     * @return a new DataParser
     */
    @Nonnull
    public static DataParser get(@Nonnull DataFormat format) {
        switch (format) {
            case APPLICATION_JSON:
                return json();
            case APPLICATION_YAML:
                return yaml();
            default:
                throw new IllegalArgumentException("Unsupported format \"" + format + "\".");
        }
    }

    /**
     * Set the schema for this DataParser.
     *
     * @param schemaRoot a RtSchemaRoot
     * @return this DataParser
     */
    public DataParser schema(RtSchemaRoot schemaRoot) {
        this.schemaRoot = schemaRoot;
        return this;
    }

    /**
     * Set this DataParser to serialize data with indentations (pretty format).
     *
     * @return this DataParser
     */
    public DataParser pretty() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return this;
    }

    /**
     * Parse a given String into a RtData.
     *
     * @param text the given String
     * @return the RtData
     * @throws JsonProcessingException if something is wrong
     */
    public RtData parse(String text) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(text);
        return jsonNodeToData(jsonNode);
    }

    /**
     * Read from a given InputStream and parse the contents into a RtData.
     *
     * @param is the given InputStream
     * @return the RtData
     * @throws IOException if something is wrong
     */
    public RtData parse(InputStream is) throws IOException {
        JsonNode jsonNode = mapper.readTree(new InputStreamReader(is));
        return jsonNodeToData(jsonNode);
    }

    /**
     * Serialize a RtData into a String.
     *
     * @param data the RtData
     * @return the serialized String
     * @throws JsonProcessingException if something is wrong
     */
    public String serialize(RtData data) throws JsonProcessingException {
        Object object = toListMapAccordingSchema(data, schemaRoot.getSchema());
        return mapper.writeValueAsString(object);
    }

    @Nonnull
    private RtData jsonNodeToData(JsonNode jsonNode) {
        RtData data = new RtData(schemaRoot.getMaxIndex());
        parseAccordingSchema(data, jsonNode, schemaRoot.getSchema());
        return data;
    }

    private void parseAccordingSchema(RtData data, JsonNode jsonNode, @Nonnull RtSchema rtSchema) {
        switch (rtSchema.getTypeCode()) {
            case TypeCode.TUPLE:
                RtSchemaTuple schemaTuple = (RtSchemaTuple) rtSchema;
                for (int i = 0; i < schemaTuple.getChildren().length; i++) {
                    JsonNode item = jsonNode.get(i);
                    if (item != null) {
                        parseAccordingSchema(data, jsonNode.get(i), schemaTuple.getChild(i));
                    }
                }
                return;
            case TypeCode.DICT:
                RtSchemaDict schemaDict = (RtSchemaDict) rtSchema;
                for (Map.Entry<String, RtSchema> entry : schemaDict.getChildren().entrySet()) {
                    String key = entry.getKey();
                    JsonNode child = jsonNode.get(key);
                    if (child != null) {
                        parseAccordingSchema(data, jsonNode.get(key), entry.getValue());
                    }
                }
                return;
            case TypeCode.INTEGER:
                data.set(rtSchema.getIndex(), jsonNode.asInt());
                return;
            case TypeCode.LONG:
                data.set(rtSchema.getIndex(), jsonNode.asLong());
                return;
            case TypeCode.DOUBLE:
                data.set(rtSchema.getIndex(), jsonNode.asDouble());
                return;
            case TypeCode.STRING:
                data.set(rtSchema.getIndex(), jsonNode.asText());
                return;
            case TypeCode.BOOLEAN:
                data.set(rtSchema.getIndex(), jsonNode.asBoolean());
                return;
            case TypeCode.DECIMAL:
                data.set(rtSchema.getIndex(), jsonNode.decimalValue());
                return;
            case TypeCode.INTEGER_ARRAY:
                Integer[] integerArray = new Integer[jsonNode.size()];
                for (int i = 0; i < jsonNode.size(); i++) {
                    integerArray[i] = jsonNode.get(i).asInt();
                }
                data.set(rtSchema.getIndex(), integerArray);
                return;
            case TypeCode.LONG_ARRAY:
                Long[] longArray = new Long[jsonNode.size()];
                for (int i = 0; i < jsonNode.size(); i++) {
                    longArray[i] = jsonNode.get(i).asLong();
                }
                data.set(rtSchema.getIndex(), longArray);
                return;
            case TypeCode.DOUBLE_ARRAY:
                Double[] doubleArray = new Double[jsonNode.size()];
                for (int i = 0; i < jsonNode.size(); i++) {
                    doubleArray[i] = jsonNode.get(i).asDouble();
                }
                data.set(rtSchema.getIndex(), doubleArray);
                return;
            case TypeCode.STRING_ARRAY:
                String[] stringArray = new String[jsonNode.size()];
                for (int i = 0; i < jsonNode.size(); i++) {
                    stringArray[i] = jsonNode.get(i).asText();
                }
                data.set(rtSchema.getIndex(), stringArray);
                return;
            case TypeCode.BOOLEAN_ARRAY:
                Boolean[] booleanArray = new Boolean[jsonNode.size()];
                for (int i = 0; i < jsonNode.size(); i++) {
                    booleanArray[i] = jsonNode.get(i).asBoolean();
                }
                data.set(rtSchema.getIndex(), booleanArray);
                return;
            case TypeCode.LIST:
                if (jsonNode.isArray()) {
                    data.set(rtSchema.getIndex(), jsonNodeValue(jsonNode));
                    return;
                }
                break;
            case TypeCode.MAP:
                if (jsonNode.isObject()) {
                    data.set(rtSchema.getIndex(), jsonNodeValue(jsonNode));
                    return;
                }
                break;
            default:
                break;
        }
    }

    @Nullable
    private Object jsonNodeValue(@Nonnull JsonNode jsonNode) {
        JsonNodeType type = jsonNode.getNodeType();
        switch (type) {
            case NUMBER:
                if (jsonNode.isInt()) {
                    return jsonNode.asLong();
                }
                return jsonNode.asDouble();
            case STRING:
                return jsonNode.asText();
            case BOOLEAN:
                return jsonNode.asBoolean();
            case ARRAY:
                List<Object> list = new LinkedList<>();
                for (int i = 0; i < jsonNode.size(); i++) {
                    list.add(jsonNodeValue(jsonNode.get(i)));
                }
                return list;
            case OBJECT:
                Map<String, Object> map = new HashMap<>(jsonNode.size());
                Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    map.put(entry.getKey(), jsonNodeValue(entry.getValue()));
                }
                return map;
            case NULL:
                return null;
            default:
                break;
        }
        throw new IllegalArgumentException("Unsupported json node type \"" + type + "\".");
    }

    private Object toListMapAccordingSchema(RtData data, @Nonnull RtSchema rtSchema) {
        int typeCode = rtSchema.getTypeCode();
        if (typeCode == TypeCode.TUPLE) {
            List<Object> list = new LinkedList<>();
            RtSchemaTuple schemaTuple = (RtSchemaTuple) rtSchema;
            for (int i = 0; i < schemaTuple.getChildren().length; i++) {
                list.add(toListMapAccordingSchema(data, schemaTuple.getChild(i)));
            }
            return list;
        } else if (typeCode == TypeCode.DICT) {
            Map<String, Object> map = new LinkedHashMap<>();
            RtSchemaDict schemaDict = (RtSchemaDict) rtSchema;
            for (Map.Entry<String, RtSchema> entry : schemaDict.getChildren().entrySet()) {
                map.put(entry.getKey(), toListMapAccordingSchema(data, entry.getValue()));
            }
            return map;
        } else {
            return data.get(rtSchema.getIndex());
        }
    }
}
