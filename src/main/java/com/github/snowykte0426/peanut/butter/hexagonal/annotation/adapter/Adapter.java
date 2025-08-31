package com.github.snowykte0426.peanut.butter.hexagonal.annotation.adapter;

import com.github.snowykte0426.peanut.butter.hexagonal.annotation.PortDirection;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface Adapter {
    PortDirection direction();
}