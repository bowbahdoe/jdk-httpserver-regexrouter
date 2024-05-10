package dev.mccue.jdk.httpserver.regexrouter.test;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import dev.mccue.jdk.httpserver.regexrouter.RegexRouter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegexRouterTest {
    @Test
    public void getRouteTest() throws Exception {
        var called = new AtomicBoolean(false);
        var router = RegexRouter.builder()
                .get(Pattern.compile("/abc"), exchange -> called.set(true))
                .build();
        router.handle(new HttpExchange() {
            @Override
            public Headers getRequestHeaders() {
                return new Headers();
            }

            @Override
            public Headers getResponseHeaders() {
                return new Headers();
            }

            @Override
            public URI getRequestURI() {
                return URI.create("/abc");
            }

            @Override
            public String getRequestMethod() {
                return "GET";
            }

            @Override
            public HttpContext getHttpContext() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getRequestBody() {
                return InputStream.nullInputStream();
            }

            @Override
            public OutputStream getResponseBody() {
                return OutputStream.nullOutputStream();
            }

            @Override
            public void sendResponseHeaders(int rCode, long responseLength) throws IOException {

            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                return new InetSocketAddress(50);
            }

            @Override
            public int getResponseCode() {
                return 0;
            }

            @Override
            public InetSocketAddress getLocalAddress() {
                return new InetSocketAddress(40);
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public void setAttribute(String name, Object value) {

            }

            @Override
            public void setStreams(InputStream i, OutputStream o) {

            }

            @Override
            public HttpPrincipal getPrincipal() {
                return null;
            }
        });

        assertTrue(called.get());
    }


    @Test
    public void postRouteTest() throws Exception {
        var called = new AtomicBoolean(false);
        var router = RegexRouter.builder()
                .post(Pattern.compile("/abc"), exchange -> called.set(true))
                .build();
        router.handle(new HttpExchange() {
            @Override
            public Headers getRequestHeaders() {
                return new Headers();
            }

            @Override
            public Headers getResponseHeaders() {
                return new Headers();
            }

            @Override
            public URI getRequestURI() {
                return URI.create("/abc");
            }

            @Override
            public String getRequestMethod() {
                return "POST";
            }

            @Override
            public HttpContext getHttpContext() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getRequestBody() {
                return InputStream.nullInputStream();
            }

            @Override
            public OutputStream getResponseBody() {
                return OutputStream.nullOutputStream();
            }

            @Override
            public void sendResponseHeaders(int rCode, long responseLength) throws IOException {

            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                return new InetSocketAddress(50);
            }

            @Override
            public int getResponseCode() {
                return 0;
            }

            @Override
            public InetSocketAddress getLocalAddress() {
                return new InetSocketAddress(40);
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public void setAttribute(String name, Object value) {

            }

            @Override
            public void setStreams(InputStream i, OutputStream o) {

            }

            @Override
            public HttpPrincipal getPrincipal() {
                return null;
            }
        });

        assertTrue(called.get());
    }

    @Test
    public void notFoundTest() throws Exception {
        var called = new AtomicBoolean(false);
        var router = RegexRouter.builder()
                .notFoundHandler(exchange -> called.set(true))
                .build();
        router.handle(new HttpExchange() {
            @Override
            public Headers getRequestHeaders() {
                return new Headers();
            }

            @Override
            public Headers getResponseHeaders() {
                return new Headers();
            }

            @Override
            public URI getRequestURI() {
                return URI.create("/abc");
            }

            @Override
            public String getRequestMethod() {
                return "get";
            }

            @Override
            public HttpContext getHttpContext() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getRequestBody() {
                return InputStream.nullInputStream();
            }

            @Override
            public OutputStream getResponseBody() {
                return OutputStream.nullOutputStream();
            }

            @Override
            public void sendResponseHeaders(int rCode, long responseLength) throws IOException {

            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                return new InetSocketAddress(50);
            }

            @Override
            public int getResponseCode() {
                return 0;
            }

            @Override
            public InetSocketAddress getLocalAddress() {
                return new InetSocketAddress(40);
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public void setAttribute(String name, Object value) {

            }

            @Override
            public void setStreams(InputStream i, OutputStream o) {

            }

            @Override
            public HttpPrincipal getPrincipal() {
                return null;
            }
        });

        assertTrue(called.get());
    }
}
