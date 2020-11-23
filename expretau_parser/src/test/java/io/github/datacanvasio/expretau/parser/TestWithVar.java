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

package io.github.datacanvasio.expretau.parser;

import io.github.datacanvasio.expretau.Expr;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import lombok.RequiredArgsConstructor;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class TestWithVar {
    @ClassRule
    public static ContextResource res = new ContextResource(
        "/simple_vars.yml",
        "{a: 2, b: 3.0, c: true, d: foo}",
        "{a: 3, b: 4.0, c: false, d: bar}"
    );

    private final String exprString;
    private final Object value0;
    private final Object value1;

    @Parameterized.Parameters(name = "{index}: {0} ==> {1}, {2}")
    @Nonnull
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
            {"a", 2L, 3L},
            {"b", 3.0, 4.0},
            {"c", true, false},
            {"d", "foo", "bar"},
            {"1 + a", 3L, 4L},
            {"1 + 2 * b", 7.0, 9.0},
            {"$.a * $.b", 6.0, 12.0},
            {"$['a'] - $[\"b\"]", -1.0, -1.0},
            // short-circuit, there must be a var to prevent const optimization
            {"false and a/0", false, false},
            {"true or a/0", true, true},
            // functions
            {"abs(a)", 2L, 3L},
        });
    }

    @Test
    public void test() throws Exception {
        Expr expr = ExpretauCompiler.INS.parse(exprString);
        RtExpr rtExpr = expr.compileIn(res.getCtx());
        assertThat(rtExpr.eval(res.getEtx(0)), is(value0));
        assertThat(rtExpr.eval(res.getEtx(1)), is(value1));
    }
}
