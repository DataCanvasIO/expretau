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

package io.github.datacanvasio.expretau.op;

import io.github.datacanvasio.expretau.exception.ExpretauCompileException;
import io.github.datacanvasio.expretau.exception.InvalidIndex;
import io.github.datacanvasio.expretau.runtime.CompileContext;
import io.github.datacanvasio.expretau.runtime.RtConst;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.evaluator.index.IndexEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;
import io.github.datacanvasio.expretau.runtime.op.RtOp;
import io.github.datacanvasio.expretau.var.VarStub;

import javax.annotation.Nonnull;

public class IndexOp extends OpWithEvaluator {
    public IndexOp() {
        super(IndexEvaluatorFactory.INS);
    }

    @Nonnull
    @Override
    public RtExpr compileIn(CompileContext ctx) throws ExpretauCompileException {
        RtExpr[] rtExprArray = compileExprArray(ctx);
        if (rtExprArray[0] instanceof VarStub) {
            VarStub stub = (VarStub) rtExprArray[0];
            if (rtExprArray[1] instanceof RtConst) {
                Object index = ((RtConst) rtExprArray[1]).eval(null);
                if (index instanceof Number) {
                    return stub.getElement(((Number) index).intValue());
                } else if (index instanceof String) {
                    return stub.getElement(index);
                }
            }
            throw new InvalidIndex(rtExprArray[1]);
        }
        return evalConst(rtExprArray);
    }

    @Override
    protected RtOp createRtOp(RtExpr[] rtExprArray) throws FailGetEvaluator {
        return super.createRtOp(rtExprArray);
    }
}
