/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package kt

import io.jooby.Kooby

class KtMvcAppWithRoutes :
  Kooby({
    val provider = { KtController() }

    routes { mvc(KtController::class, provider) }
  })
