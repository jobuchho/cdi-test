package de.hilling.junit.cdi.lifecycle;

import org.junit.runner.Description;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * Provide notifications for test lifecycle events.
 */
public class LifecycleNotifier {

    @Inject
    @Any
    private Event<Description> lifecycleEvent;

    public void notify(final EventType testCaseLifecycle, Description description) {
        AnnotationLiteral<TestEvent> event = new TestEventLiteral() {
            @Override
            public EventType value() {
                return testCaseLifecycle;
            }
        };
        lifecycleEvent.select(event).fire(description);
    }

    private static abstract class TestEventLiteral extends AnnotationLiteral<TestEvent> implements TestEvent {
    }
}
