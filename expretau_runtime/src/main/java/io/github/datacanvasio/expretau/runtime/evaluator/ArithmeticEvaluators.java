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

package io.github.datacanvasio.expretau.runtime.evaluator;

import io.github.datacanvasio.expretau.annotations.Evaluators;
import io.github.datacanvasio.expretau.runtime.evaluator.base.Evaluator;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorKey;
import io.github.datacanvasio.expretau.runtime.evaluator.base.UniversalEvaluator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.annotation.Nonnull;

@Evaluators(
    evaluatorKey = EvaluatorKey.class,
    evaluatorBase = Evaluator.class,
    evaluatorFactory = EvaluatorFactory.class,
    universalEvaluator = UniversalEvaluator.class,
    induceSequence = {BigDecimal.class, Double.class, Long.class, Integer.class}
)
public final class ArithmeticEvaluators {
    private ArithmeticEvaluators() {
    }

    //
    // Unary operators
    //

    public static int pos(int value) {
        return value;
    }

    public static long pos(long value) {
        return value;
    }

    public static double pos(double value) {
        return value;
    }

    public static BigDecimal pos(BigDecimal value) {
        return value;
    }

    public static int neg(int value) {
        return -value;
    }

    public static long neg(long value) {
        return -value;
    }

    public static double neg(double value) {
        return -value;
    }

    @Nonnull
    public static BigDecimal neg(@Nonnull BigDecimal value) {
        return value.negate();
    }

    //
    // Binary operators
    //

    public static int add(int value0, int value1) {
        return value0 + value1;
    }

    public static long add(long value0, long value1) {
        return value0 + value1;
    }

    public static double add(double value0, double value1) {
        return value0 + value1;
    }

    @Nonnull
    public static BigDecimal add(@Nonnull BigDecimal value0, BigDecimal value1) {
        return value0.add(value1);
    }

    // This is not arithmetic op, but put here to share the same evaluator factory.
    @Nonnull
    public static String add(String s0, String s1) {
        return s0 + s1;
    }

    public static int sub(int value0, int value1) {
        return value0 - value1;
    }

    public static long sub(long value0, long value1) {
        return value0 - value1;
    }

    public static double sub(double value0, double value1) {
        return value0 - value1;
    }

    @Nonnull
    public static BigDecimal sub(@Nonnull BigDecimal value0, BigDecimal value1) {
        return value0.subtract(value1);
    }

    public static int mul(int value0, int value1) {
        return value0 * value1;
    }

    public static long mul(long value0, long value1) {
        return value0 * value1;
    }

    public static double mul(double value0, double value1) {
        return value0 * value1;
    }

    @Nonnull
    public static BigDecimal mul(@Nonnull BigDecimal value0, BigDecimal value1) {
        return value0.multiply(value1);
    }

    public static int div(int value0, int value1) {
        return value0 / value1;
    }

    public static long div(long value0, long value1) {
        return value0 / value1;
    }

    public static double div(double value0, double value1) {
        return value0 / value1;
    }

    @Nonnull
    public static BigDecimal div(@Nonnull BigDecimal value0, BigDecimal value1) {
        return value0.divide(value1, RoundingMode.HALF_EVEN);
    }

    public static int abs(int num) {
        return Math.abs(num);
    }

    public static long abs(long num) {
        return Math.abs(num);
    }

    public static double abs(double num) {
        return Math.abs(num);
    }
}
