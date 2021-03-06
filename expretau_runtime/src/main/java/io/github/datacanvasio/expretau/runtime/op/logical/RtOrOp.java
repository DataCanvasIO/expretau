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

import io.github.datacanvasio.expretau.runtime.EvalContext;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RtOrOp extends RtLogicalOp {
    private static final long serialVersionUID = -1477334212197197621L;

    /**
     * Create an RtOrOp. RtNotOp performs logical OR operation.
     *
     * @param paras the parameters of the op
     */
    public RtOrOp(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Override
    public Object eval(@Nullable EvalContext etx) throws FailGetEvaluator {
        return (boolean) paras[0].eval(etx) || (boolean) paras[1].eval(etx);
    }
}
