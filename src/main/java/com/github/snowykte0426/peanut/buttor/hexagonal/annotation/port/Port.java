package com.github.snowykte0426.peanut.buttor.hexagonal.annotation.port;

import com.github.snowykte0426.peanut.buttor.hexagonal.annotation.PortDirection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface Port {
    PortDirection direction();
}