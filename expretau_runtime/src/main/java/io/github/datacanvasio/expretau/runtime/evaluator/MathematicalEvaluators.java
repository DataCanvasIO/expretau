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
import io.github.datacanvasio.expretau.runtime.evaluator.base.DoubleEvaluator;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorKey;
import io.github.datacanvasio.expretau.runtime.evaluator.base.UniversalEvaluator;

import java.math.BigDecimal;

@Evaluators(
    evaluatorKey = EvaluatorKey.class,
    evaluatorBase = DoubleEvaluator.class,
    evaluatorFactory = EvaluatorFactory.class,
    universalEvaluator = UniversalEvaluator.class,
    induceSequence = {Double.class, BigDecimal.class, Long.class, Integer.class}
)
public final class MathematicalEvaluators {
    private MathematicalEvaluators() {
    }

    public static double sin(double num) {
        return Math.sin(num);
    }

    public static double cos(double num) {
        return Math.cos(num);
    }

    public static double tan(double num) {
        return Math.tan(num);
    }

    public static double asin(double num) {
        return Math.asin(num);
    }

    public static double acos(double num) {
        return Math.acos(num);
    }

    public static double atan(double num) {
        return Math.atan(num);
    }

    public static double cosh(double num) {
        return Math.cosh(num);
    }

    public static double sinh(double num) {
        return Math.sinh(num);
    }

    public static double tanh(double num) {
        return Math.tanh(num);
    }

    public static double log(double num) {
        return Math.log(num);
    }

    public static double exp(double num) {
        return Math.exp(num);
    }
}
