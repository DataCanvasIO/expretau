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

import io.github.datacanvasio.expretau.runtime.TypeCodes;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniversalEvaluator extends ObjectEvaluator {
    private static final long serialVersionUID = 8115905605402311713L;
    private final EvaluatorFactory factory;

    @Override
    public Object eval(Object[] paras) throws FailGetEvaluator {
        int[] typeCodes = TypeCodes.getTypeCodes(paras);
        Evaluator evaluator = factory.getEvaluator(EvaluatorKey.of(typeCodes));
        if (evaluator != this) {
            return evaluator.eval(paras);
        }
        throw new FailGetEvaluator(factory, typeCodes);
    }
}
