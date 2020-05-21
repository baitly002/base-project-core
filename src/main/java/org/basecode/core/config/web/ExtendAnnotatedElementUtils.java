package org.basecode.core.config.web;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class ExtendAnnotatedElementUtils extends AnnotatedElementUtils {

    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        // Exhaustive retrieval of merged annotations...
        boolean flag = findAnnotations(element).isPresent(annotationType);
        return flag;
    }

    private static MergedAnnotations findAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.SUPERCLASS, RepeatableContainers.none());
    }
}
