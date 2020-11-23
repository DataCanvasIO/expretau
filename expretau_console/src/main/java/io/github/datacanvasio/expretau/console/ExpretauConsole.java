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

package io.github.datacanvasio.expretau.console;

import io.github.datacanvasio.expretau.Expr;
import io.github.datacanvasio.expretau.exception.ExpretauCompileException;
import io.github.datacanvasio.expretau.exception.ExpretauParseException;
import io.github.datacanvasio.expretau.parser.ExpretauCompiler;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

public final class ExpretauConsole {
    private ExpretauConsole() {
    }

    public static void main(String[] args) {
        ResourceBundle config = ResourceBundle.getBundle("config");
        System.out.println(config.getString("hello"));
        String prompt = config.getString("prompt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(prompt);
            try {
                String input = reader.readLine();
                if (input.isEmpty()) {
                    break;
                }
                Expr expr = ExpretauCompiler.INS.parse(input);
                System.out.println(expr.compileIn(null).eval(null));
            } catch (IOException | ExpretauParseException | ExpretauCompileException | FailGetEvaluator e) {
                e.printStackTrace();
            }
        }
    }
}
