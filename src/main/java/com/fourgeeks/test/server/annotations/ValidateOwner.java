package com.fourgeeks.test.server.annotations;


import javax.ws.rs.NameBinding;
import java.lang.annotation.*;

@NameBinding
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ValidateOwner {
    Class<?> target() default Object.class;
}
