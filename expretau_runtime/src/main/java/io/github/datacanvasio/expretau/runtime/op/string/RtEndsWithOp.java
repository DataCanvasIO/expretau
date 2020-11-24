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

package io.github.datacanvasio.expretau.runtime.op.string;

import io.github.datacanvasio.expretau.runtime.EvalContext;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RtEndsWithOp extends RtStringRelationOp {
    private static final long serialVersionUID = 2481487321416281178L;

    /**
     * Create an RtEndsWithOp. RtEndsWithOp performs string comparing operation by {@code String::endsWith}.
     *
     * @param paras the parameters of the op
     */
    public RtEndsWithOp(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Nonnull
    @Override
    public Boolean eval(@Nullable EvalContext etx) throws FailGetEvaluator {
        return ((String) paras[0].eval(etx)).endsWith((String) paras[1].eval(etx));
    }
}
