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
import io.github.datacanvasio.expretau.runtime.RtConst;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class TestWithoutVar {
    private static final double TAU = Math.PI * 2;

    private final String exprString;
    private final Object value;

    @Parameterized.Parameters(name = "{index}: {0} ==> {1}")
    @Nonnull
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
            // value
            {"true", true},
            {"false", false},
            {"2", 2L},
            {"3.0", 3.0},
            {"'foo'", "foo"},
            {"\"bar\"", "bar"},
            {"'\\\\-\\/-\\b-\\n-\\r-\\t-\\u0020'", "\\-/-\b-\n-\r-\t- "},
            {"\"a\\\"b\"", "a\"b"},
            {"'a\"b'", "a\"b"},
            // arithmetic op
            {"1 + 2", 3L},
            {"1 + 2 * 3", 7L},
            {"(1 + 2) * 3", 9L},
            {"(1 + 2) * (5 - (3 + 4))", -6L},
            {"3 * 1.5 + 2.34", 6.84},
            {"2 * -3.14e2", -6.28e2},
            {"5e4 + 3e3", 53e3},
            {"1 / 100", 0L},
            {"1.0 / 100", 1e-2},
            {"1 + (2 * 3-4)", 3L},
            // relational & logical op
            {"3 < 4", true},
            {"4.0 == 4", true},
            {"5 != 6", true},
            {"1 <= 2 && 3 > 2", true},
            {"1 > 0.1 and 2 - 2 = 0", true},
            {"not (0.0 * 2 < 0 || 1 * 4 > 3 and 6 / 6 == 1)", false},
            // string op
            {"'abc' startsWith 'a'", true},
            {"'abc' startsWith 'c'", false},
            {"'abc' endsWith 'c'", true},
            {"'abc' endsWith 'b'", false},
            {"'abc' contains 'b'", true},
            {"'abc123' matches '\\\\w{3}\\\\d{3}'", true},
            {"'abc123' matches '.{5}'", false},
            {"\"abc\" + 'def'", "abcdef"},
            // mathematical fun
            {"abs(-1)", 1L},
            {"abs(-2.3)", 2.3},
            {"sin(0)", 0.0},
            {"sin(TAU / 12)", 0.5},
            {"sin(TAU / 4)", 1.0},
            {"sin(5 * TAU / 12)", 0.5},
            {"sin(TAU / 2)", 0.0},
            {"sin(7 * TAU / 12)", -0.5},
            {"sin(3 * TAU / 4)", -1.0},
            {"sin(11 * TAU / 12)", -0.5},
            {"sin(TAU)", 0.0},
            {"cos(0)", 1.0},
            {"cos(TAU / 6)", 0.5},
            {"cos(TAU / 4)", 0.0},
            {"cos(TAU / 3)", -0.5},
            {"cos(TAU / 2)", -1.0},
            {"cos(2 * TAU / 3)", -0.5},
            {"cos(3 * TAU / 4)", 0.0},
            {"cos(5 * TAU / 6)", 0.5},
            {"cos(TAU)", 1.0},
            {"tan(0)", 0.0},
            {"tan(TAU / 8)", 1.0},
            {"tan(3 * TAU / 8)", -1.0},
            {"tan(TAU / 2)", 0.0},
            {"tan(5 * TAU / 8)", 1.0},
            {"tan(7 * TAU / 8)", -1.0},
            {"tan(TAU)", 0.0},
            {"asin(-1)", -TAU / 4},
            {"asin(-0.5)", -TAU / 12},
            {"asin(0)", 0.0},
            {"asin(0.5)", TAU / 12},
            {"asin(1)", TAU / 4},
            {"acos(-1)", TAU / 2},
            {"acos(-0.5)", TAU / 3},
            {"acos(0)", TAU / 4},
            {"acos(0.5)", TAU / 6},
            {"acos(1)", 0.0},
            {"atan(-1)", -TAU / 8},
            {"atan(0)", 0.0},
            {"atan(1)", TAU / 8},
            {"sinh(0)", 0.0},
            {"cosh(0)", 1.0},
            {"tanh(0)", 0.0},
            {"cosh(2.5) + sinh(2.5)", Math.exp(2.5)},
            {"cosh(3.5) - sinh(3.5)", Math.exp(-3.5)},
            {"exp(0)", 1.0},
            {"exp(1)", Math.exp(1.0)},
            {"log(E)", 1.0},
            {"log(1.0 / E)", -1.0},
            // string fun
            {"toLowerCase('HeLlO')", "hello"},
            {"toUpperCase('HeLlO')", "HELLO"},
            {"trim(' HeLlO \\n\\t')", "HeLlO"},
            {"replace('I love $name', '$name', 'Lucia')", "I love Lucia"},
        });
    }

    @Test
    public void test() throws Exception {
        Expr expr = ExpretauCompiler.INS.parse(exprString);
        RtExpr rtExpr = expr.compileIn(null);
        assertThat(rtExpr, instanceOf(RtConst.class));
        Object result = rtExpr.eval(null);
        if (result instanceof Double) {
            assertThat(Math.abs((Double) result - (Double) value), lessThan(1e-10));
        } else {
            assertThat(result, is(value));
        }
    }
}
