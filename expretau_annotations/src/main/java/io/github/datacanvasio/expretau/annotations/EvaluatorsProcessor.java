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

package io.github.datacanvasio.expretau.annotations;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EvaluatorsProcessor extends AbstractProcessor {
    private static final String EVALUATOR_EVAL_METHOD = "eval";
    private static final String EVALUATOR_TYPE_CODE_METHOD = "typeCode";
    private static final String EVALUATORS_VAR = "evaluators";

    @Nonnull
    private static String getSimpleName(@Nonnull TypeName type) {
        String name = type.toString()
            .replaceAll("<.*>", "")
            .replace("[]", "Array");
        return name.substring(name.lastIndexOf('.') + 1);
    }

    @Nonnull
    private static String getFactoryClassName(String methodName) {
        return StringUtils.capitalize(methodName) + "EvaluatorFactory";
    }

    @Nonnull
    private static String getClassName(String methodName, @Nullable List<TypeName> paraTypes) {
        StringBuilder b = new StringBuilder();
        b.append(StringUtils.capitalize(methodName));
        if (paraTypes != null) {
            for (TypeName type : paraTypes) {
                b.append(getSimpleName(type));
            }
            return b.toString();
        } else {
            b.append("Universal");
        }
        return b.toString();
    }

    @Nonnull
    private static String getSubPackageName(@Nonnull Element element) {
        String name = element.getSimpleName().toString();
        int pos = name.indexOf("Evaluators");
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        return name.toLowerCase();
    }

    private static TypeName getBoxedType(@Nonnull TypeName type) {
        return type.isPrimitive() ? type.box() : type;
    }

    private static TypeName getBoxedType(TypeMirror type) {
        return getBoxedType(TypeName.get(type));
    }

    private static List<TypeName> getParaTypeList(@Nonnull ExecutableElement element) {
        return element.getParameters().stream()
            .map(VariableElement::asType)
            .map(EvaluatorsProcessor::getBoxedType)
            .collect(Collectors.toList());
    }

    @Nonnull
    private static String getEvaluatorKey(@Nullable List<TypeName> paraTypes) {
        StringBuilder b = new StringBuilder();
        if (paraTypes != null) {
            for (TypeName type : paraTypes) {
                b.append(getSimpleName(type));
            }
            return b.toString();
        }
        return "Universal";
    }

    @Nonnull
    private static FieldSpec serialVersionUid() {
        return FieldSpec.builder(TypeName.LONG, "serialVersionUID")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$LL", new Random().nextLong())
            .build();
    }

    // Helper to get annotation value of type `Class<?>`
    @Nullable
    private static AnnotationValue getAnnotationValue(
        @Nonnull AnnotationMirror annotationMirror,
        String methodName
    ) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
            : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(methodName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    private static ExecutableElement getMethodByNameAndParaTypes(
        @Nonnull TypeElement element,
        String name,
        List<TypeName> paraTypes
    ) {
        List<ExecutableElement> methods = ElementFilter.methodsIn(element.getEnclosedElements());
        for (ExecutableElement m : methods) {
            if (m.getSimpleName().toString().equals(name)) {
                if (paraTypes == null) {
                    return m;
                }
                List<TypeName> types = m.getParameters().stream()
                    .map(Element::asType)
                    .map(TypeName::get)
                    .collect(Collectors.toList());
                if (types.equals(paraTypes)) {
                    return m;
                }
            }
        }
        return null;
    }

    @Nonnull
    private static CodeBlock codeConvertPara(
        String paraName,
        int paraIndex,
        TypeName required,
        TypeName actual
    ) {
        CodeBlock.Builder builder = CodeBlock.builder();
        boolean converted = false;
        if (actual != null) {
            if (required.equals(TypeName.get(BigDecimal.class))) {
                if (actual.equals(TypeName.get(Double.class))
                    || actual.equals(TypeName.get(Long.class))
                    || actual.equals(TypeName.get(Integer.class))
                ) {
                    builder.add("$T.valueOf(($T) $L[$L])", BigDecimal.class, actual, paraName, paraIndex);
                    converted = true;
                }
            } else if (required.equals(TypeName.get(Double.class))) {
                if (actual.equals(TypeName.get(BigDecimal.class))
                    || actual.equals(TypeName.get(Long.class))
                    || actual.equals(TypeName.get(Integer.class))
                ) {
                    builder.add("(($T) $L[$L]).doubleValue()", actual, paraName, paraIndex);
                    converted = true;
                }
            } else if (required.equals(TypeName.get(Long.class))) {
                if (actual.equals((TypeName.get(BigDecimal.class)))
                    || actual.equals(TypeName.get(Integer.class))
                    || actual.equals(TypeName.get(Double.class))
                ) {
                    builder.add("(($T) $L[$L]).longValue()", actual, paraName, paraIndex);
                    converted = true;
                }
            } else if (required.equals(TypeName.get(Integer.class))) {
                if (actual.equals(TypeName.get(BigDecimal.class))
                    || actual.equals(TypeName.get(Double.class))
                    || actual.equals(TypeName.get(Long.class))
                ) {
                    builder.add("(($T) $L[$L]).intValue()", actual, paraName, paraIndex);
                    converted = true;
                }
            }
        }
        if (!converted) {
            builder.add("($T) $L[$L]", required, paraName, paraIndex);
        }
        return builder.build();
    }

    @Nonnull
    private static CodeBlock codeEvalParas(
        @Nonnull EvaluatorsInfo info,
        String methodName,
        String evalMethodParaName,
        @Nonnull List<TypeName> paras,
        List<TypeName> newParas
    ) {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add("return $T.$L(", info.getOriginClassName(), methodName);
        boolean addComma = false;
        for (int i = 0; i < paras.size(); i++) {
            if (addComma) {
                codeBuilder.add(", ");
            }
            codeBuilder.add(codeConvertPara(evalMethodParaName, i, paras.get(i), newParas.get(i)));
            addComma = true;
        }
        codeBuilder.add(");\n");
        return codeBuilder.build();
    }

    @Nonnull
    private static CodeBlock codeCreateEvaluatorKey(
        TypeElement evaluatorKey,
        @Nonnull List<TypeName> paraTypeNames
    ) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("$T.of(", evaluatorKey);
        boolean addComma = false;
        for (TypeName paraTypeName : paraTypeNames) {
            if (addComma) {
                builder.add(", ");
            }
            builder.add("$L", ProcessorUtils.typeCode(paraTypeName));
            addComma = true;
        }
        builder.add(")");
        return builder.build();
    }

    private List<TypeElement> findSuperTypes(@Nonnull TypeElement element) {
        return processingEnv.getTypeUtils().directSupertypes(element.asType()).stream()
            .filter(i -> i.getKind() == TypeKind.DECLARED)
            .map(TypeMirror::toString)
            .map(processingEnv.getElementUtils()::getTypeElement)
            .collect(Collectors.toList());
    }

    @Nullable
    private ExecutableElement getOverridingMethod(
        @Nonnull TypeElement element,
        String name,
        List<TypeName> paraTypes
    ) {
        ExecutableElement method = getMethodByNameAndParaTypes(element, name, paraTypes);
        if (method != null) {
            if (!method.getModifiers().contains(Modifier.FINAL)) {
                return method;
            }
            return null;
        }
        for (TypeElement e : findSuperTypes(element)) {
            method = getOverridingMethod(e, name, paraTypes);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    @Nullable
    private AnnotationMirror getAnnotationMirror(
        Element element,
        @SuppressWarnings("SameParameterValue") Class<?> annotationClass
    ) {
        for (AnnotationMirror am : processingEnv.getElementUtils().getAllAnnotationMirrors(element)) {
            if (am.getAnnotationType().toString().equals(annotationClass.getName())) {
                return am;
            }
        }
        return null;
    }

    @Nonnull
    private TypeElement getTypeElementFromAnnotationValue(
        @Nonnull AnnotationMirror annotationMirror,
        String methodName
    ) {
        AnnotationValue value = getAnnotationValue(annotationMirror, methodName);
        // com.sun.tools.javac.code.Type.ClassType
        TypeMirror type = (TypeMirror) Objects.requireNonNull(value).getValue();
        TypeElement element = processingEnv.getElementUtils().getTypeElement(type.toString());
        if (element == null) {
            throw new IllegalStateException("Cannot find a class of name \"" + type + "\".");
        }
        return element;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private List<TypeName> getTypeNamesFromAnnotationValue(
        @Nonnull AnnotationMirror annotationMirror,
        @SuppressWarnings("SameParameterValue") String methodName
    ) {
        AnnotationValue value = getAnnotationValue(annotationMirror, methodName);
        // com.sun.tools.javac.util.List<com.sun.tools.javac.code.Attribute.Class>
        return ((List<AnnotationValue>) Objects.requireNonNull(value).getValue()).stream()
            .map(AnnotationValue::getValue)
            .map(Object::toString)
            .map(processingEnv.getElementUtils()::getTypeElement)
            .map(Element::asType)
            .map(TypeName::get)
            .collect(Collectors.toList());
    }

    private void generateEvaluatorClassFile(
        @Nonnull EvaluatorsInfo info,
        String className,
        @Nonnull MethodSpec evalSpec,
        MethodSpec typeCodeSpec
    ) {
        TypeElement evaluatorBase = info.getEvaluatorBase();
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(serialVersionUid())
            .addMethod(evalSpec);
        if (evaluatorBase.getKind().isInterface()) {
            builder.addSuperinterface(evaluatorBase.asType());
        } else {
            builder.superclass(evaluatorBase.asType());
        }
        if (typeCodeSpec != null) {
            builder.addMethod(typeCodeSpec);
        }
        String packageName = info.getPackageName();
        ProcessorUtils.saveSourceFile(processingEnv, packageName, builder.build());
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
            "Evaluator \"" + className + "\" generated in package \"" + packageName + "\".");
    }

    private void generateEvaluator(
        @Nonnull ExecutableElement element,
        @Nonnull EvaluatorsInfo info,
        @Nullable List<TypeName> newParas
    ) {
        String methodName = element.getSimpleName().toString();
        Map<String, EvaluatorInfo> evaluatorMap = info.getEvaluatorMap()
            .computeIfAbsent(methodName, k -> new HashMap<>());
        List<TypeName> paras = getParaTypeList(element);
        if (newParas == null) {
            newParas = paras;
        }
        String evaluatorKey = getEvaluatorKey(newParas);
        if (evaluatorMap.containsKey(evaluatorKey)) {
            return;
        }
        TypeElement evaluatorBase = info.getEvaluatorBase();
        ExecutableElement evalMethod = getOverridingMethod(
            evaluatorBase,
            EVALUATOR_EVAL_METHOD,
            Collections.singletonList(TypeName.get(Object[].class))
        );
        if (evalMethod == null) {
            return;
        }
        String paraName = evalMethod.getParameters().get(0).getSimpleName().toString();
        TypeName returnType = getBoxedType(element.getReturnType());
        MethodSpec evalSpec = MethodSpec.overriding(evalMethod)
            .returns(returnType)
            .addCode(codeEvalParas(info, methodName, paraName, paras, newParas))
            .build();
        ExecutableElement typeCodeMethod = getOverridingMethod(
            info.getEvaluatorBase(),
            EVALUATOR_TYPE_CODE_METHOD,
            null
        );
        MethodSpec typeCodeSpec = null;
        if (typeCodeMethod != null) {
            typeCodeSpec = MethodSpec.overriding(typeCodeMethod)
                .addStatement("return $L", ProcessorUtils.typeCode(returnType))
                .build();
        }
        String className = getClassName(methodName, newParas);
        generateEvaluatorClassFile(info, className, evalSpec, typeCodeSpec);
        evaluatorMap.put(evaluatorKey, new EvaluatorInfo(className, returnType, newParas));
    }

    private void induceEvaluators(ExecutableElement element, EvaluatorsInfo info) {
        List<TypeName> paras = getParaTypeList(element);
        induceEvaluatorsRecursive(element, info, paras, 0);
    }

    private void tryDescentType(
        ExecutableElement element,
        EvaluatorsInfo info,
        @Nonnull List<TypeName> newParas,
        int pos,
        TypeName newTypeName
    ) {
        TypeName oldType = newParas.get(pos);
        newParas.set(pos, newTypeName);
        induceEvaluatorsRecursive(element, info, newParas, pos + 1);
        newParas.set(pos, oldType);
    }

    private void induceEvaluatorsRecursive(
        ExecutableElement element,
        EvaluatorsInfo info,
        @Nonnull List<TypeName> newParas,
        int pos
    ) {
        if (pos >= newParas.size()) {
            generateEvaluator(element, info, new ArrayList<>(newParas));
            return;
        }
        induceEvaluatorsRecursive(element, info, newParas, pos + 1);
        TypeName type = getParaTypeList(element).get(pos);
        List<TypeName> induceSequence = info.getInduceSequence();
        int index = induceSequence.indexOf(type);
        if (index >= 0) {
            for (int i = index + 1; i < induceSequence.size(); ++i) {
                tryDescentType(element, info, newParas, pos, induceSequence.get(i));
            }
        }
    }

    private void generateEvaluatorFactories(@Nonnull EvaluatorsInfo info) {
        String packageName = info.getPackageName();
        TypeElement evaluatorKey = info.getEvaluatorKey();
        TypeElement evaluatorFactory = info.getEvaluatorFactory();
        TypeElement universalEvaluator = info.getUniversalEvaluator();
        Map<String, Map<String, EvaluatorInfo>> multiEvaluatorMap = info.getEvaluatorMap();
        for (String m : multiEvaluatorMap.keySet()) {
            Map<String, EvaluatorInfo> evaluatorMap = multiEvaluatorMap.get(m);
            CodeBlock.Builder initBuilder = CodeBlock.builder();
            for (Map.Entry<String, EvaluatorInfo> entry : evaluatorMap.entrySet()) {
                EvaluatorInfo evaluatorInfo = entry.getValue();
                List<TypeName> paraTypeNames = evaluatorInfo.getParaTypeNames();
                initBuilder.addStatement("$L.put($L, new $T())",
                    EVALUATORS_VAR,
                    codeCreateEvaluatorKey(evaluatorKey, paraTypeNames),
                    ClassName.get(packageName, evaluatorInfo.getClassName())
                );
            }
            initBuilder.addStatement("$L.put($T.UNIVERSAL, new $T(this))",
                EVALUATORS_VAR, evaluatorKey, ClassName.get(universalEvaluator)
            );
            ClassName className = ClassName.get(packageName, getFactoryClassName(m));
            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .superclass(TypeName.get(evaluatorFactory.asType()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(serialVersionUid())
                .addField(FieldSpec.builder(className, ProcessorUtils.INSTANCE_VAR_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T()", className)
                    .build())
                .addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("super()")
                    .addCode(initBuilder.build())
                    .build())
                .build();
            ProcessorUtils.saveSourceFile(processingEnv, packageName, typeSpec);
        }
    }

    private void generateEvaluators(@Nonnull Element element, EvaluatorsInfo info) {
        Element pkg = element.getEnclosingElement();
        if (pkg.getKind() != ElementKind.PACKAGE) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "Class annotated with \"Evaluators\" must not be an inner class.");
        }
        info.setPackageName(pkg.asType().toString() + "." + getSubPackageName(element));
        info.setOriginClassName(TypeName.get(element.asType()));
        info.setEvaluatorMap(new HashMap<>());
        List<ExecutableElement> executableElements = ElementFilter.methodsIn(element.getEnclosedElements());
        executableElements.forEach(e -> generateEvaluator(e, info, null));
        executableElements.sort(Comparator.comparingInt(
            (ExecutableElement e) -> -getParaTypeList(e).stream()
                .mapToInt(info.getInduceSequence()::indexOf)
                .sum()
        ));
        executableElements.forEach(e -> induceEvaluators(e, info));
        generateEvaluatorFactories(info);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.<String>builder()
            .add(Evaluators.class.getName())
            .build();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null) {
            return true;
        }
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals(Evaluators.class.getCanonicalName())) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    AnnotationMirror annotationMirror = getAnnotationMirror(element, Evaluators.class);
                    TypeElement evaluatorKey = getTypeElementFromAnnotationValue(
                        Objects.requireNonNull(annotationMirror),
                        "evaluatorKey"
                    );
                    TypeElement evaluator = getTypeElementFromAnnotationValue(
                        Objects.requireNonNull(annotationMirror),
                        "evaluatorBase"
                    );
                    TypeElement evaluatorFactory = getTypeElementFromAnnotationValue(
                        Objects.requireNonNull(annotationMirror),
                        "evaluatorFactory"
                    );
                    TypeElement universalEvaluator = getTypeElementFromAnnotationValue(
                        Objects.requireNonNull(annotationMirror),
                        "universalEvaluator"
                    );
                    List<TypeName> induceSequence = getTypeNamesFromAnnotationValue(
                        annotationMirror,
                        "induceSequence"
                    );
                    EvaluatorsInfo info = new EvaluatorsInfo(
                        evaluatorKey,
                        evaluator,
                        evaluatorFactory,
                        universalEvaluator,
                        induceSequence
                    );
                    generateEvaluators(element, info);
                }
            }
        }
        return true;
    }

    @Getter
    @RequiredArgsConstructor
    public static class EvaluatorInfo {
        private final String className;
        private final TypeName returnTypeName;
        private final List<TypeName> paraTypeNames;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class EvaluatorsInfo {
        private final TypeElement evaluatorKey;
        private final TypeElement evaluatorBase;
        private final TypeElement evaluatorFactory;
        private final TypeElement universalEvaluator;
        private final List<TypeName> induceSequence;

        private String packageName;
        private TypeName originClassName;
        private Map<String, Map<String, EvaluatorInfo>> evaluatorMap;
    }
}
