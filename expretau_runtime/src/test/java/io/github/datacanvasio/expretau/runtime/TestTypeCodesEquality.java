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

package io.github.datacanvasio.expretau.runtime;

import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class TestTypeCodesEquality {
    private final Class<?> clazz;
    private final int typeCode;

    @Nonnull
    @Parameterized.Parameters(name = "{index}: {0} == {1}")
    public static Object[][] getParameters() {
        return new Object[][]{
            {Boolean.class, TypeCode.BOOLEAN},
            {Integer.class, TypeCode.INTEGER},
            {Long.class, TypeCode.LONG},
            {Double.class, TypeCode.DOUBLE},
            {BigDecimal.class, TypeCode.DECIMAL},
            {String.class, TypeCode.STRING},
            {Object.class, TypeCode.OBJECT},
            {Boolean[].class, TypeCode.BOOLEAN_ARRAY},
            {Integer[].class, TypeCode.INTEGER_ARRAY},
            {Long[].class, TypeCode.LONG_ARRAY},
            {Double[].class, TypeCode.DOUBLE_ARRAY},
            {BigDecimal[].class, TypeCode.DECIMAL_ARRAY},
            {String[].class, TypeCode.STRING_ARRAY},
            {Object[].class, TypeCode.OBJECT_ARRAY},
            {List.class, TypeCode.LIST},
            {LinkedList.class, TypeCode.LIST},
            {ArrayList.class, TypeCode.LIST},
            {Map.class, TypeCode.MAP},
            {HashMap.class, TypeCode.MAP},
            {TreeMap.class, TypeCode.MAP},
            {LinkedHashMap.class, TypeCode.MAP},
        };
    }

    @Test
    public void testCodeEquality() {
        assertEquals(TypeCodes.getTypeCode(clazz), typeCode);
    }
}
