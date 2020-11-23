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

package io.github.datacanvasio.expretau.runtime.var;

import io.github.datacanvasio.expretau.runtime.EvalContext;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import javax.annotation.Nullable;

@RequiredArgsConstructor
public class RtVar implements RtExpr {
    private static final long serialVersionUID = -7434384449038456900L;
    private final Object id;
    private final int typeCode;

    @Override
    public Object eval(@Nullable EvalContext etx) {
        return Objects.requireNonNull(etx).get(id);
    }

    @Override
    public int typeCode() {
        return typeCode;
    }

    @SuppressWarnings("unused")
    public void set(@Nullable EvalContext etx, Object value) {
        Objects.requireNonNull(etx).set(id, value);
    }
}
