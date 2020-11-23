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

package io.github.datacanvasio.expretau.var;

import io.github.datacanvasio.expretau.Expr;
import io.github.datacanvasio.expretau.exception.ElementNotExists;
import io.github.datacanvasio.expretau.runtime.CompileContext;
import io.github.datacanvasio.expretau.runtime.RtConst;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.var.RtVar;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RequiredArgsConstructor
public class Var implements Expr {
    private final String name;

    @Nonnull
    public static RtExpr createVar(@Nonnull CompileContext ctx) {
        Object id = ctx.getId();
        if (id != null) {
            return new RtVar(id, ctx.getTypeCode());
        }
        return new VarStub(ctx);
    }

    @Nonnull
    @Override
    public RtExpr compileIn(@Nullable CompileContext ctx) throws ElementNotExists {
        RtConst rtConst = ConstFactory.INS.getConst(name);
        if (rtConst != null) {
            return rtConst;
        }
        if (ctx != null) {
            if (name.equals("$")) {
                return createVar(ctx);
            }
            CompileContext child = ctx.getChild(name);
            if (child != null) {
                return createVar(child);
            }
        }
        throw new ElementNotExists(name, ctx);
    }
}
