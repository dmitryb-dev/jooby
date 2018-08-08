package io.jooby.jetty;

import io.jooby.Mode;
import io.jooby.Route;
import io.jooby.Router;
import io.jooby.internal.jetty.JettyContext;
import io.jooby.internal.jetty.JettyHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jooby.funzy.Throwing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Jetty implements io.jooby.Server {

  private int port = 8080;

  private Server server;

  @Override public io.jooby.Server port(int port) {
    this.port = port;
    return this;
  }

  @Override public io.jooby.Server mode(Mode mode) {
    // NOOP: Jetty always run in a worker thread.
    return this;
  }

  public io.jooby.Server start(Router router) {
    this.server = new Server();
    server.setStopAtShutdown(false);

    ServerConnector connector = new ServerConnector(server,
        new HttpConnectionFactory(new HttpConfiguration()));
    connector.setPort(port);
    connector.setHost("0.0.0.0");

    server.addConnector(connector);

    server.setHandler(new JettyHandler(router, server.getThreadPool()));

    try {
      server.start();
      return this;
    } catch (Exception x) {
      throw Throwing.sneakyThrow(x);
    }
  }

  @Override public io.jooby.Server stop() {
    try {
      server.stop();
      return this;
    } catch (Exception x) {
      throw Throwing.sneakyThrow(x);
    }
  }
}
