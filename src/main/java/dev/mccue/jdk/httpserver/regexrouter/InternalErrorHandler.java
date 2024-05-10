package dev.mccue.jdk.httpserver.regexrouter;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

final class InternalErrorHandler implements RegexRouter.ErrorHandler {
    @Override
    public void handle(Throwable throwable, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
        exchange.close();
    }
}
