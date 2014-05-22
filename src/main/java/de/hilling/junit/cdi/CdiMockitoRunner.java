package de.hilling.junit.cdi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import de.hilling.junit.cdi.scope.TestCaseLifecycle;

public class CdiMockitoRunner extends BlockJUnit4ClassRunner {
	private static final Logger LOG = Logger.getLogger(CdiMockitoRunner.class.getCanonicalName());

	private static final CdiContainer cdiContainer;
	private static final ContextControl contextControl;
	private static final Map<Class<?>, Object> testCases = new HashMap<>();

	private LifecycleNotifier lifecycleNotifier;

	static {
		cdiContainer = CdiContainerLoader.getCdiContainer();
		cdiContainer.boot();
		contextControl = cdiContainer.getContextControl();
	}

	public CdiMockitoRunner(Class<?> klass) throws InitializationError {
		super(klass);
		lifecycleNotifier = BeanProvider.getContextualReference(LifecycleNotifier.class, false);
	}

	@Override
	protected Object createTest() {
		final Class<?> testClass = getTestClass().getJavaClass();
		return resolveTest(testClass);
	}

	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
		LOG.fine("starting " + method.getName());
		contextControl.startContexts();
		lifecycleNotifier.notify(TestCaseLifecycle.TEST_STARTS);
		super.runChild(method, notifier);
		lifecycleNotifier.notify(TestCaseLifecycle.TEST_FINISHED);
		contextControl.stopContexts();
		LOG.fine("finished " + method.getName());
	}

	@SuppressWarnings("unchecked")
	protected <T> T resolveTest(Class<T> clazz) {
		if (testCases.containsKey(clazz)) {
			return (T) testCases.get(clazz);
		} else {
			T testCase = BeanProvider.getContextualReference(clazz, false);
			testCases.put(clazz, testCase);
			return testCase;
		}
	}

}
