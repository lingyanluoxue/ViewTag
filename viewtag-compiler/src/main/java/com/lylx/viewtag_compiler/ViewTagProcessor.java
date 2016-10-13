package com.lylx.viewtag_compiler;


import com.google.auto.service.AutoService;
import com.lylx.viewtag_annotation.BindView;
import com.lylx.viewtag_annotation.OnClick;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by zhanghongmei on 2016/10/11.
 */
@AutoService(Processor.class)
public class ViewTagProcessor extends AbstractProcessor {

    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;

    private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFileUtils = processingEnv.getFiler();//跟文件相关的辅助类，生成JavaSourceCode
        mElementUtils = processingEnv.getElementUtils();//跟元素相关的辅助类，帮助我们去获取一些元素相关的信息
        mMessager = processingEnv.getMessager();//跟日志相关的辅助类
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<String>();
        annotationTypes.add(BindView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProxyMap.clear();
        processBindView(roundEnv);

        processOnClick(roundEnv);

        //生成代理类
/*        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            JavaFileObject sourceFile = null;
            try {
                sourceFile = mFileUtils.createSourceFile(
                        proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());
                Writer writer = sourceFile.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/
        //生成代理类
        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            JavaFile javaFile = proxyInfo.brewJava();
            try {
                javaFile.writeTo(mFileUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    private void processBindView(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        //一、收集信息
        for (Element element : elements) {
            //field type
            VariableElement variableElement = (VariableElement) element;
            //class type
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();//TypeElement
            String qualifiedName = typeElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = mProxyMap.get(qualifiedName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(mElementUtils, typeElement);
                mProxyMap.put(qualifiedName, proxyInfo);
            }
            BindView annotation = variableElement.getAnnotation(BindView.class);
            int id = annotation.value();
            proxyInfo.mInjectElements.put(id, variableElement);
        }
    }
    private void processOnClick(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OnClick.class);
        //一、收集信息
        for (Element element : elements) {
            //method type
            ExecutableElement executableElement = (ExecutableElement) element;
            //class type
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();//TypeElement
            String qualifiedName = typeElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = mProxyMap.get(qualifiedName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(mElementUtils, typeElement);
                mProxyMap.put(qualifiedName, proxyInfo);
            }
            OnClick annotation = executableElement.getAnnotation(OnClick.class);
            int[] ids = annotation.value();
            for(int i=0; i<ids.length; i++){
                proxyInfo.mOnclickInjectElements.put(ids[i], executableElement);
            }
        }
    }

}
