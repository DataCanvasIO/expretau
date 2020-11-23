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

import io.github.datacanvasio.expretau.Expr;
import io.github.datacanvasio.expretau.exception.ExpretauCompileException;
import io.github.datacanvasio.expretau.runtime.CompileContext;
import io.github.datacanvasio.expretau.runtime.RtConst;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;
import io.github.datacanvasio.expretau.runtime.op.RtOp;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import javax.annotation.Nonnull;

@RequiredArgsConstructor
public abstract class Op implements Expr {
    @Setter
    protected Expr[] exprArray;

    @Nonnull
    protected final RtExpr[] compileExprArray(CompileContext ctx) throws ExpretauCompileException {
        RtExpr[] rtExprArray = new RtExpr[exprArray.length];
        int i = 0;
        for (Expr expr : exprArray) {
            rtExprArray[i++] = expr.compileIn(ctx);
        }
        return rtExprArray;
    }

    @Nonnull
    protected final RtExpr evalConst(RtExpr[] rtExprArray) throws ExpretauCompileException {
        try {
            RtOp rtOp = createRtOp(rtExprArray);
            if (Arrays.stream(rtExprArray).allMatch(e -> e instanceof RtConst)) {
                return new RtConst(rtOp.eval(null));
            }
            return rtOp;
        } catch (FailGetEvaluator e) {
            throw new ExpretauCompileException(e);
        }
    }

    @Nonnull
    @Override
    public RtExpr compileIn(CompileContext ctx) throws ExpretauCompileException {
        RtExpr[] rtExprArray = compileExprArray(ctx);
        return evalConst(rtExprArray);
    }

    protected abstract RtOp createRtOp(RtExpr[] rtExprArray) throws FailGetEvaluator;
}
