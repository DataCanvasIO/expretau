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

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

@Evaluators(
    evaluatorKey = EvaluatorKey.class,
    evaluatorBase = Evaluator.class,
    evaluatorFactory = EvaluatorFactory.class,
    universalEvaluator = UniversalEvaluator.class,
    induceSequence = {Integer.class, Long.class}
)
public final class IndexEvaluators {
    private IndexEvaluators() {
    }

    public static Integer index(@Nonnull Integer[] array, int index) {
        return array[index];
    }

    public static Long index(@Nonnull Long[] array, int index) {
        return array[index];
    }

    public static Boolean index(@Nonnull Boolean[] array, int index) {
        return array[index];
    }

    public static String index(@Nonnull String[] array, int index) {
        return array[index];
    }

    public static Object index(@Nonnull Object[] array, int index) {
        return array[index];
    }

    public static Object index(@Nonnull List<?> array, int index) {
        return array.get(index);
    }

    public static Object index(@Nonnull Map<String, ?> map, String index) {
        return map.get(index);
    }
}
