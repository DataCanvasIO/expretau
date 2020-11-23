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
import io.github.datacanvasio.expretau.antlr4.ExpretauLexer;
import io.github.datacanvasio.expretau.antlr4.ExpretauParser;
import io.github.datacanvasio.expretau.exception.ExprSyntaxError;
import io.github.datacanvasio.expretau.exception.ExpretauParseException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import javax.annotation.Nonnull;

public class ExpretauCompiler {
    public static final ExpretauCompiler INS = new ExpretauCompiler();

    private final ExpretauParserVisitorImpl visitor;

    private ExpretauErrorListener errorListener;

    private ExpretauCompiler() {
        visitor = new ExpretauParserVisitorImpl();
    }

    @Nonnull
    private ExpretauParser getParser(String input) {
        CharStream stream = CharStreams.fromString(input);
        ExpretauLexer lexer = new ExpretauLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExpretauParser parser = new ExpretauParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        return parser;
    }

    private void collectParseError() throws ExprSyntaxError {
        List<String> errorMessages = errorListener.getErrorMessages();
        if (!errorMessages.isEmpty()) {
            throw new ExprSyntaxError(errorMessages);
        }
    }

    public Expr parse(String input) throws ExpretauParseException {
        errorListener = new ExpretauErrorListener();
        ExpretauParser parser = getParser(input);
        ParseTree tree = parser.expr();
        collectParseError();
        try {
            return visitor.visit(tree);
        } catch (ParseCancellationException e) {
            throw new ExpretauParseException(e);
        }
    }
}
