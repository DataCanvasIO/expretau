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
public class TestIndexOp {
    @ClassRule
    public static ContextResource res = new ContextResource(
        "/composite_vars.yml",
        "{"
            + "arrA: [1, 2, 3],"
            + "arrB: [foo, bar],"
            + "arrC: [1, abc],"
            + "arrD: [10, tuple],"
            + "mapA: {a: 1, b: abc},"
            + "mapB: {foo: 2.5, bar: TOM}"
            + "}",
        "{"
            + "arrA: [4, 5, 6],"
            + "arrB: [a, b],"
            + "arrC: [def, 1],"
            + "arrD: [20, TUPLE],"
            + "mapA: {a: def, b: 1},"
            + "mapB: {foo: 3.4, bar: JERRY}"
            + "}"
    );

    private final String exprString;
    private final Object value0;
    private final Object value1;

    @Parameterized.Parameters(name = "{index}: {0} ==> {1}, {2}")
    @Nonnull
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
            {"arrA[0]", 1L, 4L},
            {"arrB[1]", "bar", "b"},
            {"arrA[0] + arrA[1]", 3L, 9L},
            {"arrB[0] + arrB[1]", "foobar", "ab"},
            {"arrC[0]", 1L, "def"},
            {"arrD[0]", 10L, 20L},
            {"arrD[1]", "tuple", "TUPLE"},
            {"mapA.a", 1L, "def"},
            {"mapB.foo", 2.5, 3.4},
            {"mapB['bar']", "TOM", "JERRY"},
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
