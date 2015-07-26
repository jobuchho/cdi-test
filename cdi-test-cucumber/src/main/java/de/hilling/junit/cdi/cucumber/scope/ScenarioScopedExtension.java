package de.hilling.junit.cdi.cucumber.scope;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Declares {@link ScenarioScoped} as a scope type and
 * registers {@link ScenarioScopedContext} with the container.
 */
public class ScenarioScopedExtension implements Extension, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ScenarioScopedExtension.class
                                                       .getCanonicalName());

    public void addScope(@Observes BeforeBeanDiscovery event) {
        event.addScope(ScenarioScoped.class, true, false);
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
        afterBeanDiscovery.addContext(new ScenarioScopedContext(beanManager));
    }

}
