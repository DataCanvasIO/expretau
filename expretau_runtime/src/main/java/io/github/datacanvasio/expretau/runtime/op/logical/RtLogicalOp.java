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

package io.github.datacanvasio.expretau.runtime.op.logical;

import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.TypeCode;
import io.github.datacanvasio.expretau.runtime.op.RtOp;

import javax.annotation.Nonnull;

public abstract class RtLogicalOp extends RtOp {
    private static final long serialVersionUID = 5800304351907769891L;

    protected RtLogicalOp(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Override
    public final int typeCode() {
        return TypeCode.BOOLEAN;
    }
}
