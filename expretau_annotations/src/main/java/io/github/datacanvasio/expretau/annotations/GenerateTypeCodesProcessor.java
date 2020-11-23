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
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GenerateTypeCodesProcessor extends AbstractProcessor {
    private static final String CLASS_NAME = "TypeCode";
    private static final String LOOKUP_VAR = "codeName";
    private static final String GET_NAME_METHOD = "nameOf";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.<String>builder()
            .add(GenerateTypeCodes.class.getName())
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
            if (annotation.getQualifiedName().contentEquals(GenerateTypeCodes.class.getCanonicalName())) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    Element pkg = element.getEnclosingElement();
                    if (pkg.getKind() != ElementKind.PACKAGE) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Class annotated with \"GenerateTypeCodes\" must not be an inner class.");
                    }
                    String packageName = pkg.asType().toString();
                    GenerateTypeCodes generateTypeCodes = element.getAnnotation(GenerateTypeCodes.class);
                    ClassName className = ClassName.get(packageName, CLASS_NAME);
                    TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(FieldSpec.builder(className, ProcessorUtils.INSTANCE_VAR_NAME)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("new $T()", className)
                            .build())
                        .addField(
                            FieldSpec.builder(
                                ParameterizedTypeName.get(
                                    ClassName.get(Map.class),
                                    TypeName.get(Integer.class),
                                    TypeName.get(String.class)
                                ),
                                LOOKUP_VAR
                            )
                                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                .initializer("new $T<>()", TypeName.get(HashMap.class))
                                .build()
                        );
                    CodeBlock.Builder initCodeBuilder = CodeBlock.builder();
                    GenerateTypeCodes.TypeCode[] typeCodes = generateTypeCodes.value();
                    for (GenerateTypeCodes.TypeCode typeCode : typeCodes) {
                        String name = typeCode.name();
                        int code = ProcessorUtils.typeCode(typeCode.type());
                        classBuilder.addField(FieldSpec.builder(TypeName.INT, name)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$L", code)
                            .build());
                        initCodeBuilder.addStatement("$L.put($L, $S)", LOOKUP_VAR, code, name);
                    }
                    classBuilder
                        .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PRIVATE)
                            .addCode(initCodeBuilder.build())
                            .build())
                        .addMethod(MethodSpec.methodBuilder(GET_NAME_METHOD)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeName.INT, "code")
                            .returns(String.class)
                            .addStatement("return $L.get(code)", LOOKUP_VAR)
                            .build());
                    ProcessorUtils.saveSourceFile(processingEnv, packageName, classBuilder.build());
                }
            }
        }
        return true;
    }
}
