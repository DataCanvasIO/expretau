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
import io.github.datacanvasio.expretau.runtime.evaluator.base.Evaluator;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorKey;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;
import io.github.datacanvasio.expretau.runtime.op.RtEvaluatorOp;
import io.github.datacanvasio.expretau.runtime.op.RtOp;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public class OpWithEvaluator extends Op {
    private final EvaluatorFactory factory;

    @Override
    protected RtOp createRtOp(RtExpr[] rtExprArray) throws FailGetEvaluator {
        int[] typeCodes = Arrays.stream(rtExprArray).mapToInt(RtExpr::typeCode).toArray();
        Evaluator evaluator = factory.getEvaluator(EvaluatorKey.of(typeCodes));
        return new RtEvaluatorOp(evaluator, rtExprArray);
    }
}
