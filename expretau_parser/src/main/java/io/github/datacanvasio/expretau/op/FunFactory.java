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

import io.github.datacanvasio.expretau.op.string.ReplaceOp;
import io.github.datacanvasio.expretau.op.string.ToLowerCaseOp;
import io.github.datacanvasio.expretau.op.string.ToUpperCaseOp;
import io.github.datacanvasio.expretau.op.string.TrimOp;
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
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class FunFactory {
    public static final FunFactory INS = new FunFactory();

    private final Map<String, Supplier<Op>> funSuppliers;

    private FunFactory() {
        funSuppliers = new HashMap<>(64);
        // String
        funSuppliers.put("toLowerCase", ToLowerCaseOp::new);
        funSuppliers.put("toUpperCase", ToUpperCaseOp::new);
        funSuppliers.put("trim", TrimOp::new);
        funSuppliers.put("replace", ReplaceOp::new);
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
    }

    @Nonnull
    public Op getFun(@Nonnull String funName) {
        Supplier<Op> supplier = funSuppliers.get(funName);
        if (supplier != null) {
            return supplier.get();
        }
        throw new ParseCancellationException("Invalid fun name: \"" + funName + "\".");
    }
}
