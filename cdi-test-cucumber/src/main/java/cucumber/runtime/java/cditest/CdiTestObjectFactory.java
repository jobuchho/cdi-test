package cucumber.runtime.java.cditest;

import cucumber.api.java.ObjectFactory;
import de.hilling.junit.cdi.lifecycle.EventType;
import de.hilling.junit.cdi.lifecycle.TestEvent;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.runner.Description;

import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class CdiTestObjectFactory implements ObjectFactory {
    private static final Logger LOG = Logger.getLogger(CdiTestObjectFactory.class.getCanonicalName());

    private ContextControl contextControl;
    private Event<Description> lifecycleEvent;
    private static final Description DESCRIPTION = Description.createSuiteDescription("cucumber");


    {
        contextControl = BeanProvider.getContextualReference(ContextControl.class);
        lifecycleEvent = BeanProvider.getContextualReference(Event.class);
    }

    static {
        CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        cdiContainer.boot();
    }

    private Map<Class, Object> definitions = new HashMap<>();

    @Override
    public void start() {
        LOG.info("starting");
        contextControl.startContexts();

        notify(EventType.STARTING);
    }

    @Override
    public void stop() {
        contextControl.stopContexts();
        notify(EventType.FINISHING);
        LOG.info("stopped");
    }

    @Override
    public boolean addClass(Class<?> clazz) {
        return true;
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {
        if (definitions.get(clazz) == null) {
            LOG.info("adding " + clazz);
            definitions.put(clazz, BeanProvider.getContextualReference(clazz, false));
        }
        return (T) definitions.get(clazz);
    }

    private void notify(final EventType testCaseLifecycle) {
        AnnotationLiteral<TestEvent> event = new TestEventLiteral() {
            @Override
            public EventType value() {
                return testCaseLifecycle;
            }
        };
        lifecycleEvent.select(event).fire(DESCRIPTION);
    }

    private static abstract class TestEventLiteral extends AnnotationLiteral<TestEvent> implements TestEvent {
    }

}
