/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allow access to flash parameter from MVC route method.
 *
 * <pre>{@code
 * public String search(&#64;FlashParam String q) {
 *   ...
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FlashParam {
  /**
   * Parameter name.
   *
   * @return Parameter name.
   */
  String value() default "";
}
