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

import io.github.datacanvasio.expretau.runtime.RtExpr;

import javax.annotation.Nonnull;

public final class RtContainsOp extends RtStringRelationOp {
    private static final long serialVersionUID = 140470969300795559L;

    /**
     * Create an RtContainsOp. RtContainsOp performs string comparing operation by {@code String::contains}.
     *
     * @param paras the parameters of the op
     */
    public RtContainsOp(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Nonnull
    @Override
    protected Object fun(@Nonnull Object[] values) {
        return ((String) values[0]).contains((String) values[1]);
    }
}
