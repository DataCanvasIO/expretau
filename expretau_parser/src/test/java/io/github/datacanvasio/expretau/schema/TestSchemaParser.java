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

import io.github.datacanvasio.expretau.runtime.TypeCode;
import io.github.datacanvasio.expretau.runtime.schema.RtSchema;
import io.github.datacanvasio.expretau.runtime.schema.RtSchemaRoot;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestSchemaParser {
    @Test
    public void testSimpleVars() throws Exception {
        RtSchemaRoot root = SchemaParser.YAML.parse(
            TestSchemaParser.class.getResourceAsStream("/simple_vars.yml")
        );
        assertThat(root.getMaxIndex(), is(4));
        RtSchema schema = root.getSchema();
        assertThat(schema.getChild("a").getTypeCode(), CoreMatchers.is(TypeCode.LONG));
        assertThat(schema.getChild("b").getTypeCode(), is(TypeCode.DOUBLE));
        assertThat(schema.getChild("c").getTypeCode(), is(TypeCode.BOOLEAN));
        assertThat(schema.getChild("d").getTypeCode(), is(TypeCode.STRING));
    }

    @Test
    public void testCompositeVars() throws Exception {
        RtSchemaRoot root = SchemaParser.YAML.parse(
            TestSchemaParser.class.getResourceAsStream("/composite_vars.yml")
        );
        assertThat(root.getMaxIndex(), is(8));
        RtSchema schema = root.getSchema();
        assertThat(schema.getChild("arrA").getTypeCode(), is(TypeCode.LONG_ARRAY));
        assertThat(schema.getChild("arrB").getTypeCode(), is(TypeCode.STRING_ARRAY));
        assertThat(schema.getChild("arrC").getTypeCode(), is(TypeCode.LIST));
        assertThat(schema.getChild("arrD").getTypeCode(), is(TypeCode.TUPLE));
        assertThat(schema.getChild("arrD").getChild(0).getTypeCode(), is(TypeCode.LONG));
        assertThat(schema.getChild("arrD").getChild(1).getTypeCode(), is(TypeCode.STRING));
        assertThat(schema.getChild("mapA").getTypeCode(), is(TypeCode.MAP));
        assertThat(schema.getChild("mapB").getTypeCode(), is(TypeCode.DICT));
        assertThat(schema.getChild("mapB").getChild("foo").getTypeCode(), is(TypeCode.DOUBLE));
        assertThat(schema.getChild("mapB").getChild("bar").getTypeCode(), is(TypeCode.STRING));
    }
}
