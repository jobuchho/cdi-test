package de.hilling.junit.cdi.scope.annotationreplacement;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Implement {@link AnnotatedType}, delegating to existing instance by default.
 * 
 * @author gunnar
 * 
 * @param <X> Type of annotation being replaced.
 */
public class AnnotatedTypeAdapter<X> implements AnnotatedType<X> {
    private final AnnotatedType<X> delegate;

    public AnnotatedTypeAdapter(AnnotatedType<X> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Type getBaseType() {
        return delegate.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return delegate.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return delegate.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return delegate.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return delegate.isAnnotationPresent(annotationType);
    }

    @Override
    public Class<X> getJavaClass() {
        return delegate.getJavaClass();
    }

    @Override
    public Set<AnnotatedConstructor<X>> getConstructors() {
        return delegate.getConstructors();
    }

    @Override
    public Set<AnnotatedMethod<? super X>> getMethods() {
        return delegate.getMethods();
    }

    @Override
    public Set<AnnotatedField<? super X>> getFields() {
        return delegate.getFields();
    }

}
