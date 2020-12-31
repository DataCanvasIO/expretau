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
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorFactory;
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
import io.github.datacanvasio.expretau.runtime.evaluator.string.SubstringEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.type.DecimalTypeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.type.DoubleTypeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.type.IntTypeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.type.LongTypeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.type.StringTypeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.type.TimeEvaluatorFactory;
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
        registerEvaluator("abs", AbsEvaluatorFactory.INS);
        registerEvaluator("sin", SinEvaluatorFactory.INS);
        registerEvaluator("cos", CosEvaluatorFactory.INS);
        registerEvaluator("tan", TanEvaluatorFactory.INS);
        registerEvaluator("asin", AsinEvaluatorFactory.INS);
        registerEvaluator("acos", AcosEvaluatorFactory.INS);
        registerEvaluator("atan", AtanEvaluatorFactory.INS);
        registerEvaluator("cosh", CoshEvaluatorFactory.INS);
        registerEvaluator("sinh", SinhEvaluatorFactory.INS);
        registerEvaluator("tanh", TanhEvaluatorFactory.INS);
        registerEvaluator("log", LogEvaluatorFactory.INS);
        registerEvaluator("exp", ExpEvaluatorFactory.INS);
        // Type conversion
        registerEvaluator("int", IntTypeEvaluatorFactory.INS);
        registerEvaluator("long", LongTypeEvaluatorFactory.INS);
        registerEvaluator("double", DoubleTypeEvaluatorFactory.INS);
        registerEvaluator("decimal", DecimalTypeEvaluatorFactory.INS);
        registerEvaluator("string", StringTypeEvaluatorFactory.INS);
        registerEvaluator("time", TimeEvaluatorFactory.INS);
        // String
        registerUdf("toLowerCase", RtToLowerCaseOp::new);
        registerUdf("toUpperCase", RtToUpperCaseOp::new);
        registerUdf("trim", RtTrimOp::new);
        registerUdf("replace", RtReplaceOp::new);
        registerEvaluator("substring", SubstringEvaluatorFactory.INS);
    }

    private void registerEvaluator(
        String funName,
        final EvaluatorFactory factory
    ) {
        funSuppliers.put(funName, () -> new OpWithEvaluator(factory));
    }

    /**
     * Register a user defined function.
     *
     * @param funName     the name of the function
     * @param funSupplier a function to create the runtime function object
     */
    public void registerUdf(
        String funName,
        final Function<RtExpr[], RtOp> funSupplier
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
