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
import io.github.datacanvasio.expretau.exception.ExpretauParseException;
import io.github.datacanvasio.expretau.parser.ExpretauCompiler;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.TypeCode;
import io.github.datacanvasio.expretau.runtime.op.RtFun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestFunFactory {
    @Test
    public void testCallUnregistered() throws Exception {
        assertThrows(ExpretauParseException.class,
            () -> ExpretauCompiler.INS.parse("hello('world')"));
    }

    @Test
    public void testRegisterUdf() throws Exception {
        FunFactory.INS.registerUdf("hello", HelloOp::new);
        Expr expr = ExpretauCompiler.INS.parse("hello('world')");
        RtExpr rtExpr = expr.compileIn(null);
        assertThat(rtExpr.eval(null)).isEqualTo("Hello world");
    }

    static class HelloOp extends RtFun {
        private static final long serialVersionUID = -8060697833705004059L;

        protected HelloOp(@Nonnull RtExpr[] paras) {
            super(paras);
        }

        @Override
        protected Object fun(@Nonnull Object[] values) {
            return "Hello " + values[0];
        }

        @Override
        public int typeCode() {
            return TypeCode.STRING;
        }
    }
}
