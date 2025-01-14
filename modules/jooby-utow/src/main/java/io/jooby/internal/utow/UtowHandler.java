/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.utow;

import java.nio.charset.StandardCharsets;

import io.jooby.Context;
import io.jooby.Router;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import io.undertow.io.Receiver;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;

public class UtowHandler implements HttpHandler {
  protected final Router router;
  private final long maxRequestSize;
  private final int bufferSize;
  private final boolean defaultHeaders;

  public UtowHandler(Router router, int bufferSize, long maxRequestSize, boolean defaultHeaders) {
    this.router = router;
    this.maxRequestSize = maxRequestSize;
    this.bufferSize = bufferSize;
    this.defaultHeaders = defaultHeaders;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    UtowContext context = new UtowContext(exchange, router);

    /** default headers: */
    HeaderMap responseHeaders = exchange.getResponseHeaders();
    responseHeaders.put(Headers.CONTENT_TYPE, "text/plain");
    if (defaultHeaders) {
      responseHeaders.put(Headers.SERVER, "U");
    }

    if (context.isHttpGet()) {
      router.match(context).execute(context);
    } else {
      // possibly  HTTP body
      HeaderMap headers = exchange.getRequestHeaders();
      long len = parseLen(headers.getFirst(Headers.CONTENT_LENGTH));
      String chunked = headers.getFirst(Headers.TRANSFER_ENCODING);
      if (len > 0 || chunked != null) {
        if (len > maxRequestSize) {
          context.sendError(new StatusCodeException(StatusCode.REQUEST_ENTITY_TOO_LARGE));
          return;
        }

        /** Eager body parsing: */
        FormDataParser parser =
            FormParserFactory.builder(false)
                .addParser(
                    new MultiPartParserDefinition(router.getTmpdir())
                        .setDefaultEncoding(StandardCharsets.UTF_8.name()))
                .addParser(
                    new FormEncodedDataDefinition()
                        .setDefaultEncoding(StandardCharsets.UTF_8.name()))
                .build()
                .createParser(exchange);
        if (parser == null) {
          // Read raw body
          Router.Match route = router.match(context);
          Receiver receiver = exchange.getRequestReceiver();
          UtowBodyHandler reader = new UtowBodyHandler(route, context, bufferSize, maxRequestSize);
          if (len > 0 && len <= bufferSize) {
            receiver.receiveFullBytes(reader);
          } else {
            receiver.receivePartialBytes(reader);
          }
        } else {
          try {
            parser.parse(execute(router, context));
          } catch (Exception x) {
            context.sendError(x, StatusCode.BAD_REQUEST);
          }
        }
      } else {
        // no body move one:
        router.match(context).execute(context);
      }
    }
  }

  private static long parseLen(String value) {
    try {
      return value == null ? -1 : Long.parseLong(value);
    } catch (NumberFormatException x) {
      return -1;
    }
  }

  private static HttpHandler execute(Router router, Context ctx) {
    return exchange -> router.match(ctx).execute(ctx);
  }
}
