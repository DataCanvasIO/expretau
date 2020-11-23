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
import io.github.datacanvasio.expretau.op.logical.AndOp;
import io.github.datacanvasio.expretau.op.logical.NotOp;
import io.github.datacanvasio.expretau.op.logical.OrOp;
import io.github.datacanvasio.expretau.op.string.ContainsOp;
import io.github.datacanvasio.expretau.op.string.EndsWithOp;
import io.github.datacanvasio.expretau.op.string.MatchesOp;
import io.github.datacanvasio.expretau.op.string.StartsWithOp;
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
import org.antlr.v4.runtime.misc.ParseCancellationException;

import javax.annotation.Nonnull;

public final class OpFactory {
    private OpFactory() {
    }

    @Nonnull
    public static Op getUnary(int type) {
        switch (type) {
            case ExpretauParser.ADD:
                return new OpWithEvaluator(PosEvaluatorFactory.INS);
            case ExpretauParser.SUB:
                return new OpWithEvaluator(NegEvaluatorFactory.INS);
            case ExpretauParser.NOT:
                return new NotOp();
            default:
                throw new ParseCancellationException("Invalid operator type: " + type);
        }
    }

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
                return new AndOp();
            case ExpretauParser.OR:
                return new OrOp();
            case ExpretauParser.STARTSWITH:
                return new StartsWithOp();
            case ExpretauParser.ENDSWITH:
                return new EndsWithOp();
            case ExpretauParser.CONTAINS:
                return new ContainsOp();
            case ExpretauParser.MATCHES:
                return new MatchesOp();
            default:
                throw new ParseCancellationException("Invalid operator type: " + type);
        }
    }
}
