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

import io.github.datacanvasio.expretau.runtime.EvalContext;

public class RtData implements EvalContext {
    private static final long serialVersionUID = -1735756800219588237L;

    private final Object[] varSlots;

    public RtData(int numIndexedVars) {
        varSlots = new Object[numIndexedVars];
    }

    @Override
    public String toString() {
        if (varSlots.length == 0) {
            return "(empty)";
        }
        if (varSlots.length == 1) {
            return varSlots[0].toString();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < varSlots.length; i++) {
            sb.append(String.format("%03d", i));
            sb.append(": ");
            sb.append(varSlots[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Object get(Object id) {
        return varSlots[(int) id];
    }

    @Override
    public void set(Object id, Object value) {
        varSlots[(int) id] = value;
    }
}
