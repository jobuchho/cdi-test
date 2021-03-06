package de.hilling.junit.cdi.scope;

import de.hilling.junit.cdi.annotations.ActivatableTestImplementation;
import de.hilling.junit.cdi.annotations.GlobalTestImplementation;
import de.hilling.junit.cdi.scope.annotationreplacement.AnnotatedTypeAdapter;
import de.hilling.junit.cdi.scope.annotationreplacement.AnnotationReplacementAdapter;
import de.hilling.junit.cdi.scope.annotationreplacement.AnnotationUtils;
import de.hilling.junit.cdi.scope.context.TestContext;
import de.hilling.junit.cdi.scope.context.TestSuiteContext;
import de.hilling.junit.cdi.util.MavenVersion;
import de.hilling.junit.cdi.util.MavenVersionResolver;
import de.hilling.junit.cdi.util.ReflectionsUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;

/**
 * CDI {@link javax.enterprise.inject.spi.Extension} to enable proxying of (nearly) all method invocations. <p> By
 * default, these are all classes, except: <ul> <li>Anonymous classes.</li> <li>Enums.</li> </ul> To preventing
 * <em>everything</em> from being proxied it is possible to define explicit packages.
 */
public class TestScopeExtension implements Extension, Serializable {
    public static final MavenVersion MINIMUM_WELD_VERSION_FOR_AFTER_TYPE_DISCOVERY = new MavenVersion(2, 2);

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TestScopeExtension.class
            .getCanonicalName());
    private final MavenVersionResolver versionResolver = MavenVersionResolver.getInstance();
    private final Map<Class<?>, AnnotatedType> decoratedTypes = new HashMap<>();

    /**
     * Add contexts after bean discovery.
     *
     * @param afterBeanDiscovery AfterBeanDiscovery
     */
    public void afterBeanDiscovery(
            @Observes AfterBeanDiscovery afterBeanDiscovery) {
        afterBeanDiscovery.addContext(new TestSuiteContext());
        afterBeanDiscovery.addContext(new TestContext());
    }

    public AnnotatedType decoratedTypeFor(Class<?> clazz) {
        return decoratedTypes.get(clazz);
    }

    /**
     * Use {@link javax.enterprise.inject.spi.AfterTypeDiscovery} to add Stereotype. <p> This is <a
     * href="https://issues.jboss.org/browse/WELD-1660">not possible in older weld versions</a>. </p>
     *
     * @param afterTypeDiscovery type meta information.
     */
    public void afterTypeDiscovery(@Observes AfterTypeDiscovery afterTypeDiscovery) {
        MavenVersion version = versionResolver.getVersion("org.jboss.weld", "weld-api");
        if (version != null && version.compareTo(MINIMUM_WELD_VERSION_FOR_AFTER_TYPE_DISCOVERY) >= 0) {
            afterTypeDiscovery.getAlternatives().add(GlobalTestImplementation.class);
        }
    }

    public <T> void replaceAnnotations(@Observes ProcessAnnotatedType<T> pat) {
        LOG.log(FINE, "processing type " + pat);
        AnnotatedTypeAdapter<T> enhancedAnnotatedType = new AnnotationReplacementAdapter<>(pat.getAnnotatedType());
        pat.setAnnotatedType(enhancedAnnotatedType);
        updateDecoratedTypes(pat);
    }

    private <T> void updateDecoratedTypes(ProcessAnnotatedType<T> pat) {
        decoratedTypes.put(pat.getAnnotatedType().getJavaClass(), pat.getAnnotatedType());
    }


    public <X> void processAnnotatedTypes(@Observes ProcessAnnotatedType<X> pat) {
        AnnotatedType<X> type = pat.getAnnotatedType();
        final Class<X> javaClass = type.getJavaClass();
        if (javaClass.isAnnotationPresent(ActivatableTestImplementation.class)) {
            new ActivatableAlternativeBuilder<X>(pat).invoke();
        } else if (ReflectionsUtils.isTestClass(javaClass)) {
            AnnotationUtils.addClassAnnotation(pat, new AnnotationLiteral<TestSuiteScoped>() {
            });
        } else if (ReflectionsUtils.shouldProxyCdiType(javaClass)) {
            AnnotationUtils.addClassAnnotation(pat, new AnnotationLiteral<Rediractable>() {
            });
        }
        updateDecoratedTypes(pat);
    }


}