parser grammar ExpretauParser;

options { tokenVocab=ExpretauLexer; }

@header {
package io.github.datacanvasio.expretau.antlr4;
}

expr : INT                                                           # Int
     | REAL                                                          # Real
     | STR                                                           # Str
     | BOOL                                                          # Bool
     | ID                                                            # Var
     | '(' expr ')'                                                  # Pars
     | fun=ID '(' (expr (',' expr) *) ? ')'                          # Fun
     | expr '.' ID                                                   # StrIndex
     | expr '[' expr ']'                                             # Index
     | op=(ADD | SUB) expr                                           # PosNeg
     | expr op=(MUL | DIV) expr                                      # MulDiv
     | expr op=(ADD | SUB) expr                                      # AddSub
     | expr op=(LT | LE | EQ | GT | GE | NE) expr                    # Relation
     | expr op=(STARTSWITH | ENDSWITH | CONTAINS | MATCHES) expr     # StringOp
     | op=NOT expr                                                   # Not
     | expr op=AND expr                                              # And
     | expr op=OR expr                                               # Or
     ;
