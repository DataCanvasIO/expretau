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

package io.github.datacanvasio.expretau.runtime.evaluator.base;

import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public abstract class EvaluatorFactory implements Serializable {
    private static final long serialVersionUID = 5023860384673216809L;

    protected final Map<EvaluatorKey, Evaluator> evaluators;

    protected EvaluatorFactory() {
        evaluators = new HashMap<>();
    }

    @Nonnull
    public final Evaluator getEvaluator(@Nonnull EvaluatorKey key) throws FailGetEvaluator {
        Evaluator evaluator = evaluators.get(key);
        if (evaluator != null) {
            return evaluator;
        }
        evaluator = evaluators.get(EvaluatorKey.UNIVERSAL);
        if (evaluator != null) {
            return evaluator;
        }
        throw new FailGetEvaluator(this, key.getParaTypeCodes());
    }
}