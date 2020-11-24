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

package io.github.datacanvasio.expretau.parser;

import io.github.datacanvasio.expretau.Expr;
import io.github.datacanvasio.expretau.antlr4.ExpretauParser;
import io.github.datacanvasio.expretau.antlr4.ExpretauParserBaseVisitor;
import io.github.datacanvasio.expretau.op.FunFactory;
import io.github.datacanvasio.expretau.op.IndexOp;
import io.github.datacanvasio.expretau.op.Op;
import io.github.datacanvasio.expretau.value.Bool;
import io.github.datacanvasio.expretau.value.Int;
import io.github.datacanvasio.expretau.value.Real;
import io.github.datacanvasio.expretau.value.Str;
import io.github.datacanvasio.expretau.var.Var;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import javax.annotation.Nonnull;

public final class ExpretauParserVisitorImpl extends ExpretauParserBaseVisitor<Expr> {
    private void setParaList(@Nonnull Op op, @Nonnull List<ExpretauParser.ExprContext> exprList) {
        op.setExprArray(
            exprList.stream()
                .map(this::visit)
                .toArray(Expr[]::new)
        );
    }

    @Nonnull
    private Expr internalVisitBinaryOp(
        int type,
        @Nonnull List<ExpretauParser.ExprContext> exprList
    ) {
        Op op = OpFactory.getBinary(type);
        setParaList(op, exprList);
        return op;
    }

    @Nonnull
    private Expr internalVisitUnaryOp(
        int type,
        ExpretauParser.ExprContext expr
    ) {
        Op op = OpFactory.getUnary(type);
        op.setExprArray(new Expr[]{visit(expr)});
        return op;
    }

    @Override
    public Expr visitInt(ExpretauParser.IntContext ctx) {
        return Int.fromString(ctx.INT().getText());
    }

    @Override
    public Expr visitReal(ExpretauParser.RealContext ctx) {
        return Real.fromString(ctx.REAL().getText());
    }

    @Override
    public Expr visitStr(ExpretauParser.StrContext ctx) {
        String str = ctx.STR().getText();
        return Str.fromString(StringEscapeUtils.unescapeJson(str.substring(1, str.length() - 1)));
    }

    @Override
    public Expr visitBool(ExpretauParser.BoolContext ctx) {
        return Bool.fromString(ctx.BOOL().getText());
    }

    @Override
    public Expr visitVar(ExpretauParser.VarContext ctx) {
        return new Var(ctx.ID().getText());
    }

    @Override
    public Expr visitPars(ExpretauParser.ParsContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Expr visitPosNeg(ExpretauParser.PosNegContext ctx) {
        return internalVisitUnaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitMulDiv(ExpretauParser.MulDivContext ctx) {
        return internalVisitBinaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitAddSub(ExpretauParser.AddSubContext ctx) {
        return internalVisitBinaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitRelation(ExpretauParser.RelationContext ctx) {
        return internalVisitBinaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitNot(ExpretauParser.NotContext ctx) {
        return internalVisitUnaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitAnd(ExpretauParser.AndContext ctx) {
        return internalVisitBinaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitOr(ExpretauParser.OrContext ctx) {
        return internalVisitBinaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitIndex(ExpretauParser.IndexContext ctx) {
        Op op = new IndexOp();
        setParaList(op, ctx.expr());
        return op;
    }

    @Override
    public Expr visitStrIndex(ExpretauParser.StrIndexContext ctx) {
        Op op = new IndexOp();
        op.setExprArray(new Expr[]{visit(ctx.expr()), new Str(ctx.ID().getText())});
        return op;
    }

    @Override
    public Expr visitStringOp(ExpretauParser.StringOpContext ctx) {
        return internalVisitBinaryOp(ctx.op.getType(), ctx.expr());
    }

    @Override
    public Expr visitFun(ExpretauParser.FunContext ctx) {
        String funName = ctx.ID().getText();
        Op op = FunFactory.INS.getFun(funName);
        setParaList(op, ctx.expr());
        return op;
    }
}
