/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package examples;

import io.jooby.annotations.GET;
import io.jooby.annotations.QueryParam;
import io.jooby.test.MyValue;

public class MyValueRouter {

  @GET("/myvalue")
  public MyValue beanConverter(@QueryParam MyValue bean) {
    return bean;
  }
}
