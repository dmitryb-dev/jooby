/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package tests.i2026;

import io.jooby.annotations.GET;
import io.jooby.annotations.Path;

@Path("/api/todo")
public class C2026 {

  @GET
  public String handle() {
    return "TODO...";
  }
}
