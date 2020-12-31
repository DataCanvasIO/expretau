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

package io.github.datacanvasio.expretau.runtime.evaluator.string;

import io.github.datacanvasio.expretau.annotations.Evaluators;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorKey;
import io.github.datacanvasio.expretau.runtime.evaluator.base.StringEvaluator;
import io.github.datacanvasio.expretau.runtime.evaluator.base.UniversalEvaluator;

import javax.annotation.Nonnull;

@Evaluators(
    evaluatorKey = EvaluatorKey.class,
    evaluatorBase = StringEvaluator.class,
    evaluatorFactory = EvaluatorFactory.class,
    universalEvaluator = UniversalEvaluator.class,
    induceSequence = {Integer.class, Long.class}
)
final class StringEvaluators {
    private StringEvaluators() {
    }

    @Nonnull
    static String substring(@Nonnull String str, int begin, int end) {
        return str.substring(begin, end);
    }

    @Nonnull
    static String substring(@Nonnull String str, int begin) {
        return str.substring(begin);
    }
}
