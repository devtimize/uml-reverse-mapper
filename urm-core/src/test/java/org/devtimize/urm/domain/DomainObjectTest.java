package org.devtimize.urm.domain;

import org.devtimize.urm.testdomain.weirdos.Colors;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

public class DomainObjectTest {

    @Test
    public void domainObjectConstructedProperlyFromClass() {
        DomainClass viaClassConstructor = new DomainClass(DomainObjectTest.class);
        assertThat(viaClassConstructor,
                new DomainObjectAssertion("com.iluwatar.urm.domain", "DomainObjectTest"));
    }

    @Test
    public void domainConstructedProperlyFromInnerClass() {
        DomainClass viaClassConstructor = new DomainClass(DomainObjectTest.DomainObjectAssertion.class);
        assertThat(viaClassConstructor,
                new DomainObjectAssertion("com.iluwatar.urm.domain", "DomainObjectAssertion"));
    }

    @Test
    public void domainConstructedProperlyWithEnum() {
        DomainClass viaClassConstructor = new DomainClass(Colors.class);
        List<String> fieldNames = viaClassConstructor.getFields().stream()
                .map(DomainField::getUmlName)
                .collect(Collectors.toList());
        assertThat(fieldNames, containsInAnyOrder("RED",
                "BARON_RED", "BLOOD_RED", "DARK_RED", "SLIGHTLY_DARKER_RED",
                "A_LOT_DARKER_RED", "SO_RED_YOU_CANT_EVEN_IMAGINE"));
    }

    private static class DomainObjectAssertion extends TypeSafeMatcher<DomainClass> {
        private String expectedPackageName;
        private String expectedClassName;

        public DomainObjectAssertion(String expectedPackageName, String expectedClassName) {
            this.expectedPackageName = expectedPackageName;
            this.expectedClassName = expectedClassName;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("faulty domain object construction");
        }

        @Override
        protected boolean matchesSafely(DomainClass domainObject) {
            assertThat(domainObject.getPackageName(), is(expectedPackageName));
            assertThat(domainObject.getClassName(), is(expectedClassName));
            return true;
        }
    }
}
