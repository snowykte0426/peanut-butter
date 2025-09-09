package com.github.snowykte0426.peanut.butter.hexagonal.annotation.port;

import com.github.snowykte0426.peanut.butter.hexagonal.annotation.PortDirection;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

class PortTest {

    @Test
    void shouldHaveSourceRetention() {
        Retention retention = Port.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.SOURCE, retention.value());
    }

    @Test
    void shouldTargetType() {
        Target target = Port.class.getAnnotation(Target.class);
        assertEquals(1, target.value().length);
        assertEquals(ElementType.TYPE, target.value()[0]);
    }

    @Test
    void shouldHaveDirectionMethod() throws NoSuchMethodException {
        assertNotNull(Port.class.getMethod("direction"));
        assertEquals(PortDirection.class, Port.class.getMethod("direction").getReturnType());
    }

    @Port(direction = PortDirection.INBOUND)
    interface TestInboundPort {
    }

    @Port(direction = PortDirection.OUTBOUND)
    interface TestOutboundPort {
    }

    @Test
    void shouldWorkWithInboundDirection() {
        Port port = TestInboundPort.class.getAnnotation(Port.class);
        assertNull(port);
    }

    @Test
    void shouldWorkWithOutboundDirection() {
        Port port = TestOutboundPort.class.getAnnotation(Port.class);
        assertNull(port);
    }

    @Test
    void shouldBeUsableAtCompileTime() {
        @Port(direction = PortDirection.INBOUND)
        interface CompileTimeTestPort {
        }

        assertTrue(true, "Port annotation can be used at compile time");
    }
}
