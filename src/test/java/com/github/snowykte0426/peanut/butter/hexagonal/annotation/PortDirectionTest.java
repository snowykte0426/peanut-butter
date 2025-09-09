package com.github.snowykte0426.peanut.butter.hexagonal.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortDirectionTest {

    @Test
    void shouldHaveInboundDirection() {
        assertEquals("INBOUND", PortDirection.INBOUND.name());
    }

    @Test
    void shouldHaveOutboundDirection() {
        assertEquals("OUTBOUND", PortDirection.OUTBOUND.name());
    }

    @Test
    void shouldHaveTwoDirections() {
        assertEquals(2, PortDirection.values().length);
    }

    @Test
    void shouldBeValidEnum() {
        assertEquals(PortDirection.INBOUND, PortDirection.valueOf("INBOUND"));
        assertEquals(PortDirection.OUTBOUND, PortDirection.valueOf("OUTBOUND"));
    }
}
