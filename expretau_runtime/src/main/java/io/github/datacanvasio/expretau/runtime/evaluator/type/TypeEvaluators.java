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

package io.github.datacanvasio.expretau.runtime.evaluator.type;

import io.github.datacanvasio.expretau.annotations.Evaluators;
import io.github.datacanvasio.expretau.runtime.evaluator.base.Evaluator;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorKey;
import io.github.datacanvasio.expretau.runtime.evaluator.base.UniversalEvaluator;

import java.math.BigDecimal;
import javax.annotation.Nonnull;

@Evaluators(
    evaluatorKey = EvaluatorKey.class,
    evaluatorBase = Evaluator.class,
    evaluatorFactory = EvaluatorFactory.class,
    universalEvaluator = UniversalEvaluator.class,
    induceSequence = {}
)
final class TypeEvaluators {
    private TypeEvaluators() {
    }

    static int intType(int value) {
        return value;
    }

    static int intType(long value) {
        return (int) value;
    }

    static int intType(double value) {
        return (int) value;
    }

    static int intType(@Nonnull BigDecimal value) {
        return value.intValue();
    }

    static int intType(String value) {
        return Integer.parseInt(value);
    }

    static long longType(int value) {
        return value;
    }

    static long longType(long value) {
        return value;
    }

    static long longType(double value) {
        return (long) value;
    }

    static long longType(@Nonnull BigDecimal value) {
        return value.longValue();
    }

    static long longType(@Nonnull String value) {
        return Long.parseLong(value);
    }

    static double doubleType(int value) {
        return value;
    }

    static double doubleType(long value) {
        return value;
    }

    static double doubleType(double value) {
        return value;
    }

    static double doubleType(@Nonnull BigDecimal value) {
        return value.doubleValue();
    }

    static double doubleType(@Nonnull String value) {
        return Double.parseDouble(value);
    }

    @Nonnull
    static BigDecimal decimalType(int value) {
        return BigDecimal.valueOf(value);
    }

    @Nonnull
    static BigDecimal decimalType(long value) {
        return BigDecimal.valueOf(value);
    }

    @Nonnull
    static BigDecimal decimalType(double value) {
        return BigDecimal.valueOf(value);
    }

    @Nonnull
    static BigDecimal decimalType(BigDecimal value) {
        return value;
    }

    @Nonnull
    static BigDecimal decimalType(String value) {
        return new BigDecimal(value);
    }

    static String stringType(@Nonnull Object value) {
        return value.toString();
    }
}
