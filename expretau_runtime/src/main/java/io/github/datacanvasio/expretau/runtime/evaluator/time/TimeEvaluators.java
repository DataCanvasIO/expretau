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

package io.github.datacanvasio.expretau.runtime.evaluator.time;

import io.github.datacanvasio.expretau.annotations.Evaluators;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.base.EvaluatorKey;
import io.github.datacanvasio.expretau.runtime.evaluator.base.TimeEvaluator;
import io.github.datacanvasio.expretau.runtime.evaluator.base.UniversalEvaluator;
import io.github.datacanvasio.expretau.runtime.exception.FailParseTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nonnull;

@Evaluators(
    evaluatorKey = EvaluatorKey.class,
    evaluatorBase = TimeEvaluator.class,
    evaluatorFactory = EvaluatorFactory.class,
    universalEvaluator = UniversalEvaluator.class,
    induceSequence = {}
)
final class TimeEvaluators {
    private TimeEvaluators() {
    }

    @Nonnull
    static Date time() {
        return new Date();
    }

    @Nonnull
    static Date time(long timestamp) {
        return new Date(timestamp);
    }

    @Nonnull
    static Date time(String str) {
        return time(str, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    @Nonnull
    static Date time(String str, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new FailParseTime(str, fmt);
        }
    }
}
