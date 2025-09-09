package com.github.snowykte0426.peanut.butter.hexagonal.annotation.adapter;

import com.github.snowykte0426.peanut.butter.hexagonal.annotation.PortDirection;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

class AdapterTest {

    @Test
    void shouldBeAnnotatedWithComponent() {
        assertTrue(Adapter.class.isAnnotationPresent(Component.class));
    }

    @Test
    void shouldHaveRuntimeRetention() {
        Retention retention = Adapter.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldTargetType() {
        Target target = Adapter.class.getAnnotation(Target.class);
        assertEquals(1, target.value().length);
        assertEquals(ElementType.TYPE, target.value()[0]);
    }

    @Test
    void shouldHaveDirectionMethod() throws NoSuchMethodException {
        assertNotNull(Adapter.class.getMethod("direction"));
        assertEquals(PortDirection.class, Adapter.class.getMethod("direction").getReturnType());
    }

    @Component
    @Adapter(direction = PortDirection.INBOUND)
    static class TestInboundAdapter {
    }

    @Component
    @Adapter(direction = PortDirection.OUTBOUND)
    static class TestOutboundAdapter {
    }

    @Test
    void shouldWorkWithInboundDirection() {
        Adapter adapter = TestInboundAdapter.class.getAnnotation(Adapter.class);
        assertNotNull(adapter);
        assertEquals(PortDirection.INBOUND, adapter.direction());
    }

    @Test
    void shouldWorkWithOutboundDirection() {
        Adapter adapter = TestOutboundAdapter.class.getAnnotation(Adapter.class);
        assertNotNull(adapter);
        assertEquals(PortDirection.OUTBOUND, adapter.direction());
    }
}
