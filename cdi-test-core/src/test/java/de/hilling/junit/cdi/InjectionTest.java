package de.hilling.junit.cdi;

import de.hilling.junit.cdi.beans.ConstructorInjected;
import de.hilling.junit.cdi.beans.Person;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InjectionTest extends CdiTestAbstract {

    @Inject
    private Person person;

    @Inject
    private CurrentTestInformation testInformation;

    @Inject
    private ConstructorInjected constructorInjected;


    @Test
    public void checkTestInformation()throws Exception {
        assertNotNull(testInformation);
        assertEquals(InjectionTest.class, testInformation.getTestClass());
        assertEquals(InjectionTest.class.getMethod("checkTestInformation"), testInformation.getMethod());
    }

    @Test
    public void testInjection() {
        assertNotNull(person);
        assertNotNull(constructorInjected);
    }

    @Test
    public void testProxiedCostructorInjection() {
        assertNotNull(constructorInjected.getPerson());
        assertNotNull(constructorInjected.getRequest());
    }

    @Test
    public void testPersons() {
        checkPersonWorks(person);
        checkPersonWorks(constructorInjected.getPerson());
    }

    private void checkPersonWorks(Person person) {
        person.setName("test");
        assertEquals("test", person.getName());
    }

}
