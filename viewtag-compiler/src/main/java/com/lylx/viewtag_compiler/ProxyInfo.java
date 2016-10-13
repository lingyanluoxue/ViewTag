package com.lylx.viewtag_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by zhanghongmei on 2016/10/11.
 */

public class ProxyInfo {

    private Elements mElementUtils;
    private TypeElement mTypeElement;
    private String mProxyClassName;
    private String mPackageName;
    private static String SUFFIX = "ViewInjector";
    private static ClassName VIEWINJECTOR ;

    //key为id，value为对应的成员变量
    public Map<Integer, VariableElement> mInjectElements = new HashMap<Integer, VariableElement>();

    public Map<Integer, ExecutableElement> mOnclickInjectElements = new HashMap<Integer, ExecutableElement>();


    public ProxyInfo(Elements elementUtils, TypeElement typeElement) {
        this.mElementUtils = elementUtils;
        this.mTypeElement = typeElement;
        mPackageName = mElementUtils.getPackageOf(typeElement).toString();
        mProxyClassName = mTypeElement.getSimpleName() + "$$" + SUFFIX;
        VIEWINJECTOR = ClassName.get("com.lylx.viewtag_api", "ViewInjector");
    }

    public String getProxyClassFullName() {
        return mTypeElement.getQualifiedName() + "$$" + SUFFIX;
    }


    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.mTypeElement = typeElement;
    }

    JavaFile brewJava() {
        return JavaFile.builder(mPackageName, createType())
                .addFileComment("Generated code from View Tag. Do not modify!")
                .build();
    }

    private TypeSpec createType() {
        return TypeSpec.classBuilder(mProxyClassName)
                .addModifiers(PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(VIEWINJECTOR, TypeName.get(mTypeElement.asType())))
                .addMethod(createInjectMethod())
                .build();

    }

    private MethodSpec createInjectMethod() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("inject")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.get(mTypeElement.asType()), "host", Modifier.FINAL)
                .addParameter(TypeName.OBJECT, "object");

        for (int id : mInjectElements.keySet()) {
            VariableElement variableElement = mInjectElements.get(id);
            Name name = variableElement.getSimpleName();
            TypeMirror type = variableElement.asType();
            result.beginControlFlow(" if(object instanceof android.app.Activity)")
                    .addStatement("host.$N= ($T)(((android.app.Activity)object).findViewById($L))", name, type, id)
                    .nextControlFlow("else")
                    .addStatement("host.$N=($T)(((android.view.View)object).findViewById($L))", name, type, id)
                    .endControlFlow();
        }
        return result.build();
    }

    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package " + mPackageName).append(";\n\n");
        builder.append("import com.lylx.viewtag.*;\n");
        builder.append("import com.lylx.viewtag_api.*;\n");
        builder.append("public class ").append(mProxyClassName).append(" implements " + SUFFIX + "<" + mTypeElement.getQualifiedName() + ">");
        builder.append("\n{\n");
        generateMethod(builder);
        builder.append("\n}\n");
        return builder.toString();
    }

    private void generateMethod(StringBuilder builder) {
        builder.append("public void inject(" + mTypeElement.getQualifiedName() + " host , Object object )");
        builder.append("\n{\n");
        for (int id : mInjectElements.keySet()) {
            VariableElement variableElement = mInjectElements.get(id);
            String name = variableElement.getSimpleName().toString();
            String type = variableElement.asType().toString();

            builder.append(" if(object instanceof android.app.Activity)");
            builder.append("\n{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)object).findViewById(" + id + "));");
            builder.append("\n}\n").append("else").append("\n{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.view.View)object).findViewById(" + id + "));");
            builder.append("\n}\n");
        }
        builder.append("\n}\n");
    }
}
