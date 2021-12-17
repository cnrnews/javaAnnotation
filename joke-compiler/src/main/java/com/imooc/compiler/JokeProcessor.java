package com.imooc.compiler;

import com.google.auto.service.AutoService;
import com.imooc.annotations.WXPayEntry;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * 绕过微信支付分享限制：生成微信回调注册类
 */
@AutoService(Processor.class)
public class JokeProcessor extends AbstractProcessor {


    //返回用来创建新源、类或辅助文件的 Filer 它支持创建新文件
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
//        返回用来创建新源、类或辅助文件的 Filer。
        mFiler = processingEnvironment.getFiler();
    }

    /**
     * 指定需要处理的Annotation
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> supportedOption : getSupportedAnnotations()) {
            // getCanonicalName：添加注解类名
            types.add(supportedOption.getCanonicalName());
        }
        return types;
    }

    /**
     * 返回需要处理的注解类集合
     * @return
     */
    private Set<Class<? extends Annotation>> getSupportedAnnotations(){
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(WXPayEntry.class);
        return annotations;
    }

    /**
     * 指定版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 核心方法：用于生成类
     * @param set 请求处理的注解类型
     * @param roundEnvironment
     * @return true:表示当前注解已经处理；false：可能需要后续的 processor 来处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("-------------------------------");
        generateWXPayCode(roundEnvironment);
        return false;
    }
    private void generateWXPayCode(RoundEnvironment roundEnvironment){
        WXPayEntryVisitor visitor = new WXPayEntryVisitor();
        visitor.setFiler(mFiler);
        scanElement(roundEnvironment,WXPayEntry.class,visitor);
    }
    private void scanElement(RoundEnvironment roundEnvironment, Class<? extends Annotation> annotation, AnnotationValueVisitor
                             visitor){
        // 1.获取所有有 WXPayEntry 注解的 Element
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
        // 2.遍历所有被注解的 类或者接口
        for (Element element : elements) {
            List<? extends AnnotationMirror> annotationMirrors =
                    element.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues =
                        annotationMirror.getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                    //将获得的注解上的实际的值给visitor
                    entry.getValue().accept(visitor,null);
                }
            }
        }
    }
}