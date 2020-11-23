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

import io.github.datacanvasio.expretau.annotations.GenerateTypeCodes;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

@GenerateTypeCodes({
    @GenerateTypeCodes.TypeCode(name = "BOOLEAN", type = "java.lang.Boolean"),
    @GenerateTypeCodes.TypeCode(name = "INTEGER", type = "java.lang.Integer"),
    @GenerateTypeCodes.TypeCode(name = "LONG", type = "java.lang.Long"),
    @GenerateTypeCodes.TypeCode(name = "DOUBLE", type = "java.lang.Double"),
    @GenerateTypeCodes.TypeCode(name = "STRING", type = "java.lang.String"),
    @GenerateTypeCodes.TypeCode(name = "DECIMAL", type = "java.math.BigDecimal"),
    @GenerateTypeCodes.TypeCode(name = "OBJECT", type = "java.lang.Object"),
    @GenerateTypeCodes.TypeCode(name = "BOOLEAN_ARRAY", type = "java.lang.Boolean[]"),
    @GenerateTypeCodes.TypeCode(name = "INTEGER_ARRAY", type = "java.lang.Integer[]"),
    @GenerateTypeCodes.TypeCode(name = "LONG_ARRAY", type = "java.lang.Long[]"),
    @GenerateTypeCodes.TypeCode(name = "DOUBLE_ARRAY", type = "java.lang.Double[]"),
    @GenerateTypeCodes.TypeCode(name = "STRING_ARRAY", type = "java.lang.String[]"),
    @GenerateTypeCodes.TypeCode(name = "DECIMAL_ARRAY", type = "java.math.BigDecimal[]"),
    @GenerateTypeCodes.TypeCode(name = "OBJECT_ARRAY", type = "java.lang.Object[]"),
    @GenerateTypeCodes.TypeCode(name = "LIST", type = "java.util.List"),
    @GenerateTypeCodes.TypeCode(name = "MAP", type = "java.util.Map"),
    // pseudo types
    @GenerateTypeCodes.TypeCode(name = "TUPLE", type = "tuple"),
    @GenerateTypeCodes.TypeCode(name = "DICT", type = "dict"),
})
public final class TypeCodes implements Serializable {
    private static final long serialVersionUID = 5276659721959556203L;

    public static int getTypeCode(@Nonnull Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return TypeCode.LIST;
        } else if (Map.class.isAssignableFrom(type)) {
            return TypeCode.MAP;
        }
        return type.getCanonicalName().hashCode();
    }

    public static int getTypeCode(Object value) {
        if (value != null) {
            return getTypeCode(value.getClass());
        }
        return getTypeCode(Void.class);
    }

    @Nonnull
    public static int[] getTypeCodes(@Nonnull Object[] values) {
        int[] typeCodes = new int[values.length];
        int i = 0;
        for (Object para : values) {
            typeCodes[i++] = TypeCodes.getTypeCode(para);
        }
        return typeCodes;
    }
}
