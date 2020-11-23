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

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestTypeCodesUniqueness {
    @Test
    public void testCodeUniqueness() {
        Class<?>[] classes = new Class[]{
            Boolean.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            BigDecimal.class,
            String.class,
            Object.class,
            Boolean[].class,
            Integer[].class,
            Long[].class,
            Float[].class,
            Double[].class,
            BigDecimal[].class,
            String[].class,
            Object[].class,
            List.class,
            Map.class,
        };
        Set<Integer> codeSet = new HashSet<>();
        for (Class<?> clazz : classes) {
            assertTrue(codeSet.add(TypeCodes.getTypeCode(clazz)));
        }
        assertTrue(codeSet.add(TypeCode.TUPLE));
        assertTrue(codeSet.add(TypeCode.DICT));
    }
}