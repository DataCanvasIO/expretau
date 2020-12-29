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

import io.github.datacanvasio.expretau.antlr4.ExpretauParser;
import io.github.datacanvasio.expretau.runtime.evaluator.arithmetic.AddEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.arithmetic.DivEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.arithmetic.MulEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.arithmetic.NegEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.arithmetic.PosEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.arithmetic.SubEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.relational.EqEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.relational.GeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.relational.GtEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.relational.LeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.relational.LtEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.evaluator.relational.NeEvaluatorFactory;
import io.github.datacanvasio.expretau.runtime.op.logical.RtAndOp;
import io.github.datacanvasio.expretau.runtime.op.logical.RtNotOp;
import io.github.datacanvasio.expretau.runtime.op.logical.RtOrOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtContainsOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtEndsWithOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtMatchesOp;
import io.github.datacanvasio.expretau.runtime.op.string.RtStartsWithOp;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import javax.annotation.Nonnull;

public final class OpFactory {
    private OpFactory() {
    }

    /**
     * Get an unary Op by its type.
     *
     * @param type the type
     * @return the Op
     */
    @Nonnull
    public static Op getUnary(int type) {
        switch (type) {
            case ExpretauParser.ADD:
                return new OpWithEvaluator(PosEvaluatorFactory.INS);
            case ExpretauParser.SUB:
                return new OpWithEvaluator(NegEvaluatorFactory.INS);
            case ExpretauParser.NOT:
                return new RtOpWrapper(RtNotOp::new);
            default:
                throw new ParseCancellationException("Invalid operator type: " + type);
        }
    }

    /**
     * Get a binary Op by its type.
     *
     * @param type the type
     * @return the Op
     */
    @Nonnull
    public static Op getBinary(int type) {
        switch (type) {
            case ExpretauParser.ADD:
                return new OpWithEvaluator(AddEvaluatorFactory.INS);
            case ExpretauParser.SUB:
                return new OpWithEvaluator(SubEvaluatorFactory.INS);
            case ExpretauParser.MUL:
                return new OpWithEvaluator(MulEvaluatorFactory.INS);
            case ExpretauParser.DIV:
                return new OpWithEvaluator(DivEvaluatorFactory.INS);
            case ExpretauParser.LT:
                return new OpWithEvaluator(LtEvaluatorFactory.INS);
            case ExpretauParser.LE:
                return new OpWithEvaluator(LeEvaluatorFactory.INS);
            case ExpretauParser.EQ:
                return new OpWithEvaluator(EqEvaluatorFactory.INS);
            case ExpretauParser.GT:
                return new OpWithEvaluator(GtEvaluatorFactory.INS);
            case ExpretauParser.GE:
                return new OpWithEvaluator(GeEvaluatorFactory.INS);
            case ExpretauParser.NE:
                return new OpWithEvaluator(NeEvaluatorFactory.INS);
            case ExpretauParser.AND:
                return new RtOpWrapper(RtAndOp::new);
            case ExpretauParser.OR:
                return new RtOpWrapper(RtOrOp::new);
            case ExpretauParser.STARTSWITH:
                return new RtOpWrapper(RtStartsWithOp::new);
            case ExpretauParser.ENDSWITH:
                return new RtOpWrapper(RtEndsWithOp::new);
            case ExpretauParser.CONTAINS:
                return new RtOpWrapper(RtContainsOp::new);
            case ExpretauParser.MATCHES:
                return new RtOpWrapper(RtMatchesOp::new);
            default:
                throw new ParseCancellationException("Invalid operator type: " + type);
        }
    }
}
