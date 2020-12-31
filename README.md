# ExpreTau

![Build with Maven](https://github.com/DataCanvasIO/expretau/workflows/Build%20with%20Maven/badge.svg)

ExpreTau is a simple expression engine written in Java, of which the runtime codes are split from parsing and compiling
codes. The classes in runtime are serializable so that they are suitable for runtime of distributed computing system,
like [Apache Flink](https://flink.apache.org/).

ExpreTau is just "Expression" and "TAU". The idea of "TAU" is coming
from [The Tau Manifesto](https://tauday.com/tau-manifesto).

## Getting Started

```java
// The original expression string.
String exprString = "(1 + 2) * (5 - (3 + 4))";
// parse it into an Expr object.
Expr expr = ExpretauCompiler.INS.parse(exprString);
// Compile in a CompileContext (can be null without variables in the expression)
// and get an RtExpr object.
RtExpr rtExpr = expr.compileIn(null);
// Evaluate it in an EvalContext (can be null without variables in the expression).
Object result = rtExpr.eval(null);
```

The `RtExpr` object can do `eval` multiple times in different `EvalContext` after generated by `compileIn`.

Module `expretau_console` can be simply used as a command line calculator, which is based on ExpreTau.

## Dependencies

```xml
<dependencies>
    <!-- Required if you want to do parsing and compiling -->
    <dependency>
        <groupId>io.github.datacanvasio.expretau</groupId>
        <artifactId>expretau-parser</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- Required if you want to do evaluating -->
    <dependency>
        <groupId>io.github.datacanvasio.expretau</groupId>
        <artifactId>expretau-runtime</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Variables and Context

Variables can be used in expressions, but a `CompileContext` must be provided to define the types of variables.

A [JSON Schema](http://json-schema.org/) definition can be used as a source of `CompileContext`. For example (in YAML
format for simplicity, but you can surely use JSON format)

```yaml
type: object
properties:
  a:
    type: integer
  b:
    type: number
  c:
    type: boolean
  d:
    type: string
additionalProperties: false
```

where variables `a`, `b`, `c`, `d` are defined with specified types. The a `RtExpr` can be compiled as following,

```java
// jsonSchemaInYamlFormat can be a String/InputStream contains the JSON Schema definition.
RtSchemaRoot schemaRoot = SchemaParser.YAML.parse(jsonSchemaInYamlFormat);
Expr expr = ExpretauCompiler.INS.parse("a + b");
RtExpr rtExpr = expr.compileIn(schemaRoot.getSchema());
```

You can also create a parser with the `RtSchemaRoot` object to parse a JSON/YAML source into a `EvalContext` object.

```java
DataParser parser = DataParser.yaml().schema(schemaRoot);
// RtData implements EvalContext
RtData data = parser.parse("{a: 2, b: 3.0, c: true, d: foo}");
// The result should be a Double 5.0
Object result = rtExpr.eval(data);
```

## Nested Context

In a JSON Schema definition, objects and arrays can be nested into each other, for example,

```yaml
type: object
properties:
  a:
    type: object
    properties:
      b:
        type: number
      c:
        type: boolean
    additionalProperties: false
  d:
    type: array
    items:
      - type: integer
      - type: string
    additionalItems: false
additionalProperties: false
```

In this context, you can use `a.b` and `a.c` to access the `number` and the `boolean` variables. The syntax looks the
same as map index, but they are really separate variables. On the contrary, `a` is not an existing variable. Also, you
can use `d[0]` and `d[1]` to access the `integer` and the `string` variables and `d` is not an existing variable.

The `additionalProperties` and `additionalItems` are crucial. If they are set to `true` (which is default in JSON Schema
Specification), `a` becomes a variable of `Map` type and `d` of `List` type, which can be accessed by the same syntax,
but the operating is a runtime indexing, not a var identifying in compiling time.

The special variable `$` can be used to access the whole context, so `$.a` is the same as `a`. `$` is useful for a
context with an array as root. The parser also looks on `a.b` as `a['b']`, so the syntax to access variables is much
like JSONPath.

## Operators

| Category       | Operator   | Associativity |
| :------------- | :--------- | :------------ |
| Parenthesis    | `( )`      |               |
| Function Call  | `( )`      | Left to right |
| Name Index     | `.`        | Left to right |
| Array Index    | `[ ]`      | Left to right |
| Unary          | `+` `-`    | Right to left |
| Multiplicative | `*` `/`    | Left to right |
| Additive       | `+` `-`    | Left to right |
| Relational     | `<` `<=` `>` `>=` `==` `=` `!=` `<>` | Left to right |
| String         | `startsWith` `endsWith` `contains` `matches` | Left to right |
| Logical NOT    | `!` `not`  | Left to right |
| Logical AND    | `&&` `and` | Left to right |
| Logical OR     | <code>&#x7c;&#x7c;</code> `or` | Left to right |

## Data Types

| Type Name    | JSON Schema Type | Hosting Java Type      | Literal in Expression |
| :----------- | :--------------- | :--------------------- | :-------------------- |
| Integer      |                  | java.lang.Integer      |                       |
| Long         | integer          | java.lang.Long         | `0` `20` `-375`       |
| Double       | number           | java.lang.Double       | `2.0` `-6.28` `3e-4`  |
| Boolean      | boolean          | java.lang.Boolean      | `true` `false`        |
| String       | string           | java.lang.String       | `"hello"` `'world'`   |
| Decimal      |                  | java.math.BigDecimal   |
| Time         |                  | java.util.Date         |
| IntegerArray |                  | java.lang.Integer[]    |
| LongArray    | array            | java.lang.Long[]       |
| DoubleArray  | array            | java.lang.Double[]     |
| BooleanArray | array            | java.lang.Boolean[]    |
| StringArray  | array            | java.lang.String[]     |
| DecimalArray |                  | java.math.BigDecimal[] |
| ObjectArray  | array            | java.lang.Object[]     |
| List         | array            | java.util.List         |
| Map          | object           | java.util.Map          |
| Object       | object           | java.lang.Object       |

For JSON Schema of type `array`, the final type is determined as in the following table.

| Value of `additionalItems` | Value of `items.type` | Type Name            |
| :------------------------- | :-------------------- | :------------------- |
| `false`                    |                       | split into variables |
| `true`                     | integer               | LongArray            |
| `true`                     | number                | DoubleArray          |
| `true`                     | boolean               | BooleanArray         |
| `true`                     | string                | StringArray          |
| `true`                     | object                | ObjectArray          |
| `true`                     |                       | List                 |

For JSON Schema of type `object`, the final type is determined as in the following table.

| Value of `additionalProperties` | Value of `properties` | Type Name            |
| :------------------------------ | :-------------------- | :------------------- |
| `false`                         |                       | split into variables |
| `true`                          | not null              | Map                  |
| `true`                          | null                  | Object               |

**NOTE**: Some types cannot be written literally in expressions, but they do exist in the engine. They can be got by
pre-defined constants, variables or intermediate results.

## Constants

| Name | Value                   |
| :--- | ----------------------: |
| TAU  | 6.283185307179586476925 |
| E    | 2.7182818284590452354   |

There is not "3.14159265" but "TAU". :smile:

## Functions

### Mathematical

See [Math (Java Platform SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html).

| Function  | Java function based on | Description |
| :-------- | :--------------------- | :---------- |
| `abs(x)`  | `java.lang.Math.abs`   |             |
| `sin(x)`  | `java.lang.Math.sin`   |             |
| `cos(x)`  | `java.lang.Math.cos`   |             |
| `tan(x)`  | `java.lang.Math.tan`   |             |
| `asin(x)` | `java.lang.Math.asin`  |             |
| `acos(x)` | `java.lang.Math.acos`  |             |
| `atan(x)` | `java.lang.Math.atan`  |             |
| `cosh(x)` | `java.lang.Math.cosh`  |             |
| `sinh(x)` | `java.lang.Math.sinh`  |             |
| `tanh(x)` | `java.lang.Math.tanh`  |             |
| `log(x)`  | `java.lang.Math.log`   |             |
| `exp(x)`  | `java.lang.Math.exp`   |             |

### Type conversion

| Function       | Java function based on | Description            |
| :------------- | :--------------------- | :--------------------- |
| `int(x)`       |                        | Convert `x` to Integer |
| `long(x)`      |                        | Convert `x` to Long    |
| `double(x)`    |                        | Convert `x` to Double  |
| `decimal(x)`   |                        | Convert `x` to Decimal |
| `string(x)`    |                        | Convert `x` to String  |
| `time(x)`      |                        | Convert `x` to Time    |
| `time(x, fmt)` |                        | Convert `x` to Time    |

### String

See [String (Java Platform SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html).

| Function             | Java function based on | Description |
| :------------------- | :--------------------- | :---------- |
| `toLowerCase(x)`     | `String::toLowerCase`  |             |
| `toUpperCase(x)`     | `String::toUpperCase`  |             |
| `trim(x)`            | `String::trim`         |             |
| `replace(x, a, b)`   | `String::replace`      |             |
| `substring(x, s)`    | `String::substring`    |             |
| `substring(x, s, e)` | `String::substring`    |             |

## User defined functions

It is simple to add an user defined function to ExpreTau.

First, define a class like

```java
public class HelloOp extends RtFun {
    private static final long serialVersionUID = -8060697833705004059L;

    protected HelloOp(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Override
    protected Object fun(@Nonnull Object[] values) {
        return "Hello " + values[0];
    }

    @Override
    public int typeCode() {
        return TypeCode.STRING;
    }
}
```

Then register it to the `FunFactory`

```java
FunFactory.INS.registerUdf("hello", HelloOp::new);
```

Now you can use the `hello` function

```java
Expr expr = ExpretauCompiler.INS.parse("hello('world')");
RtExpr rtExpr = expr.compileIn(null);
System.out.println(rtExpr.eval(null));
```

## Modules

| Module | Description | Documentation |
| :----- | :---------- | :------------ |
| `expretau_annotations` | An annotation processor to help generating some runtime code. This module is not required to using ExpreTau library. | [![javadoc](https://javadoc.io/badge2/io.github.datacanvasio.expretau/expretau-annotations/javadoc.svg)](https://javadoc.io/doc/io.github.datacanvasio.expretau/expretau-annotations) |
| `expretau_console` | An command line application to parse and evaluate expressions inputted from console. | [![javadoc](https://javadoc.io/badge2/io.github.datacanvasio.expretau/expretau-console/javadoc.svg)](https://javadoc.io/doc/io.github.datacanvasio.expretau/expretau-console) |
| `expretau_parser` | The ExpreTau parser, required to parse expression string. | [![javadoc](https://javadoc.io/badge2/io.github.datacanvasio.expretau/expretau-parser/javadoc.svg)](https://javadoc.io/doc/io.github.datacanvasio.expretau/expretau-parser) |
| `expretau_runtime` | The ExpreTau runtime, required to evaluate the compiled runtime object. | [![javadoc](https://javadoc.io/badge2/io.github.datacanvasio.expretau/expretau-runtime/javadoc.svg)](https://javadoc.io/doc/io.github.datacanvasio.expretau/expretau-runtime) |
