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

import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import javax.annotation.Nonnull;

public final class EvaluatorKey implements Serializable {
    public static final EvaluatorKey UNIVERSAL = new EvaluatorKey(new int[0]);
    private static final long serialVersionUID = 3094073337324796122L;

    @Getter
    private final int[] paraTypeCodes;

    private EvaluatorKey(@Nonnull int[] paraTypeCodes) {
        this.paraTypeCodes = paraTypeCodes;
    }

    /**
     * Create a EvaluatorKey of specified type codes.
     *
     * @param paraTypeCodes the type codes
     * @return the EvaluatorKey
     */
    @Nonnull
    public static EvaluatorKey of(int... paraTypeCodes) {
        return new EvaluatorKey(paraTypeCodes);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(paraTypeCodes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EvaluatorKey) {
            return Arrays.equals(paraTypeCodes, ((EvaluatorKey) obj).paraTypeCodes);
        }
        return false;
    }
}
