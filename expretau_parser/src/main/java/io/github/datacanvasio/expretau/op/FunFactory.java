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

import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.evaluator.arithmetic.AbsEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.AcosEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.AsinEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.AtanEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.CosEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.CoshEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.ExpEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.LogEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.SinEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.SinhEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.TanEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.mathematical.TanhEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.op.RtOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtReplaceOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtToLowerCaseOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtToUpperCaseOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtTrimOp;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class FunFactory {
    public static final FunFactory INS = new FunFactory();

    private final Map<String, Supplier<Op>> funSuppliers;

    private FunFactory() {
        funSuppliers = new HashMap<>(64);
        // Mathematical
        funSuppliers.put("abs", () -> new OpWithEvaluator(AbsEvaluatorFactory.INS));
        funSuppliers.put("sin", () -> new OpWithEvaluator(SinEvaluatorFactory.INS));
        funSuppliers.put("cos", () -> new OpWithEvaluator(CosEvaluatorFactory.INS));
        funSuppliers.put("tan", () -> new OpWithEvaluator(TanEvaluatorFactory.INS));
        funSuppliers.put("asin", () -> new OpWithEvaluator(AsinEvaluatorFactory.INS));
        funSuppliers.put("acos", () -> new OpWithEvaluator(AcosEvaluatorFactory.INS));
        funSuppliers.put("atan", () -> new OpWithEvaluator(AtanEvaluatorFactory.INS));
        funSuppliers.put("cosh", () -> new OpWithEvaluator(CoshEvaluatorFactory.INS));
        funSuppliers.put("sinh", () -> new OpWithEvaluator(SinhEvaluatorFactory.INS));
        funSuppliers.put("tanh", () -> new OpWithEvaluator(TanhEvaluatorFactory.INS));
        funSuppliers.put("log", () -> new OpWithEvaluator(LogEvaluatorFactory.INS));
        funSuppliers.put("exp", () -> new OpWithEvaluator(ExpEvaluatorFactory.INS));
        // String
        registerUdf("toLowerCase", RtToLowerCaseOp::new);
        registerUdf("toUpperCase", RtToUpperCaseOp::new);
        registerUdf("trim", RtTrimOp::new);
        registerUdf("replace", RtReplaceOp::new);
    }

    /**
     * Register a user defined funtion.
     *
     * @param funName     the name of the function
     * @param funSupplier a function to create the runtime function object
     */
    public void registerUdf(
        String funName,
        Function<RtExpr[], RtOp> funSupplier
    ) {
        funSuppliers.put(funName, () -> new RtOpWrapper(funSupplier));
    }

    /**
     * Get the function (Op) by its name.
     *
     * @param funName the name of the function
     * @return the function (Op)
     */
    @Nonnull
    public Op getFun(@Nonnull String funName) {
        Supplier<Op> supplier = funSuppliers.get(funName);
        if (supplier != null) {
            return supplier.get();
        }
        throw new ParseCancellationException("Invalid fun name: \"" + funName + "\".");
    }
}
