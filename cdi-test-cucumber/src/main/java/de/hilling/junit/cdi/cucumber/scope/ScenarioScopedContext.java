package de.hilling.junit.cdi.cucumber.scope;

import de.hilling.junit.cdi.lifecycle.EventType;
import de.hilling.junit.cdi.lifecycle.TestEvent;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;
import org.junit.runner.Description;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * Implementation for Scope containing objects for one Cucumber Scenario.
 */
public class ScenarioScopedContext extends AbstractContext implements Context, Serializable {
    private static final long serialVersionUID = 1L;

    private static final ContextImplementation CONTEXT_INSTANCE = new ContextImplementation();

    @Inject
    protected ScenarioScopedContext(BeanManager beanManager) {
        super(beanManager);
        CONTEXT_INSTANCE.beanManager = beanManager;
    }

    protected void activate(@Observes @TestEvent(EventType.STARTING) Description description) {
        CONTEXT_INSTANCE.activate();
    }

    protected void deactivate(@Observes @TestEvent(EventType.FINISHING) Description description) {
        CONTEXT_INSTANCE.deactivate();
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        return CONTEXT_INSTANCE.storage;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ScenarioScoped.class;
    }

    @Override
    public boolean isActive() {
        return CONTEXT_INSTANCE.active;
    }

    private static class ContextImplementation {
        private boolean active = false;
        private ContextualStorage storage;
        private BeanManager beanManager;

        private void createStorage() {
            storage = new ContextualStorage(beanManager, true, false);
        }

        private void disposeStorage() {
            storage = null;
        }

        protected void activate() {
            createStorage();
            active = true;
        }

        protected void deactivate() {
            active = false;
            disposeStorage();
        }

    }
}
