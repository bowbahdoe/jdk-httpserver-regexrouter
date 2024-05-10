package dev.mccue.jdk.httpserver.regexrouter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of a Router that just does a linear scan through regexes.
 *
 * <p>
 * This was chosen for my demo since it is the easiest thing to implement, not because it
 * is the most performant.
 * </p>
 *
 * <p>
 * That being said, this seems to be roughly how Django handles routing.
 * </p>
 *
 * <p>
 * From <a href="https://docs.djangoproject.com/en/3.2/topics/http/urls/">the django docs</a>
 * </p>
 *
 * <ul>
 *     <li>Django runs through each URL pattern, in order, and stops at the first one that matches the requested URL, matching against path_info.</li>
 *     <li>Once one of the URL patterns matches, Django imports and calls the given view, which is a Python function (or a class-based view). The view gets passed the following arguments:</li>
 *     <li>
 *         <ul>
 *            <li>An instance of HttpRequest.</li>
 *            <li>If the matched URL pattern contained no named groups, then the matches from the regular expression are provided as positional arguments.</li>
 *            <li>The keyword arguments are made up of any named parts matched by the path expression that are provided, overridden by any arguments specified in the optional kwargs argument to django.urls.path() or django.urls.re_path().</li>
 *        </ul>
 *     </li>
 * </ul>
 *
 * <p>
 *     For the syntax of declaring a named group, <a href="https://stackoverflow.com/questions/415580/regex-named-groups-in-java">this stack overflow question should be useful.</a>
 * </p>
 */
public final class RegexRouter implements HttpHandler {
    private final List<Mapping> mappings;

    private final ErrorHandler errorHandler;
    private final HttpHandler notFoundHandler;

    private RegexRouter(Builder builder) {
        this.mappings = builder.mappings;
        this.errorHandler = builder.errorHandler;
        this.notFoundHandler = builder.notFoundHandler;
    }

    /**
     * Creates a {@link Builder}.
     *
     * @return A builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Takes a matcher and provides an implementation of RouteParams on top of it.
     *
     * <p>
     *     Makes the assumption that it takes ownership of the matcher and will be exposed only via
     *     the interface, so the mutability of the matcher is not relevant.
     * </p>
     */
    record MatcherRouteParams(Matcher matcher) implements RouteParams {
        @Override
        public Optional<String> param(int pos) {
            if (matcher.groupCount() > pos - 1 || pos < 0) {
                return Optional.empty();
            }
            else {
                return Optional.of(URLDecoder.decode(matcher.group(pos + 1), StandardCharsets.UTF_8));
            }
        }

        @Override
        public Optional<String> param(String name) {
            try {
                final var namedGroup = matcher.group(name);
                if (namedGroup == null) {
                    return Optional.empty();
                }
                else {
                    return Optional.of(URLDecoder.decode(namedGroup, StandardCharsets.UTF_8));
                }
            } catch (IllegalArgumentException ex) {
                // Yes this is bad, but there is no interface that a matcher gives
                // for verifying whether a named group even exists in the pattern.
                // If no match exists it will throw this exception, so for better or worse
                // this should be okay. JDK never changes.
                return Optional.empty();
            }
        }
    }

    /**
     * Handles the request if there is a matching handler.
     * @param exchange The request to handle.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        for (final var mapping : this.mappings) {
            final var method = exchange.getRequestMethod();
            final var pattern = mapping.routePattern();
            final var matcher = pattern.matcher(exchange.getRequestURI().getPath());
            if (method.equalsIgnoreCase(mapping.method) && matcher.matches()) {
                new MatcherRouteParams(matcher).set(exchange);
                try {
                    mapping.handler.handle(exchange);
                } catch (Throwable t) {
                    errorHandler.handle(t, exchange);
                }
                return;
            }
        }
        notFoundHandler.handle(exchange);
    }

    private record Mapping(
            String method,
            Pattern routePattern,
            HttpHandler handler
    ) {}

    /**
     * A builder for {@link RegexRouter}.
     */
    public static final class Builder {
        private final List<Mapping> mappings;
        private ErrorHandler errorHandler;
        private HttpHandler notFoundHandler;

        private Builder() {
            this.mappings = new ArrayList<>();
            this.errorHandler = new InternalErrorHandler();
            this.notFoundHandler = new NotFoundHandler();
        }

        public Builder route(
                String method,
                Pattern routePattern,
                HttpHandler handler
        ) {
            return route(List.of(method), routePattern, handler);
        }

        public Builder route(
                List<String> methods,
                Pattern routePattern,
                HttpHandler handler
        ) {
            Objects.requireNonNull(methods);
            Objects.requireNonNull(routePattern);
            Objects.requireNonNull(handler);

            if (!methods.isEmpty()) {
                for (var method : methods) {
                    this.mappings.add(new Mapping(
                            method.toLowerCase(),
                            routePattern,
                            handler
                    ));
                }
            }

            return this;
        }

        public Builder get(Pattern routePattern, HttpHandler handler) {
            return route("get", routePattern, handler);
        }

        public Builder post(Pattern routePattern, HttpHandler handler) {
            return route("get", routePattern, handler);
        }

        public Builder patch(Pattern routePattern, HttpHandler handler) {
            return route("patch", routePattern, handler);
        }

        public Builder put(Pattern routePattern, HttpHandler handler) {
            return route("put", routePattern, handler);
        }

        public Builder head(Pattern routePattern, HttpHandler handler) {
            return route("head", routePattern, handler);
        }

        public Builder delete(Pattern routePattern, HttpHandler handler) {
            return route("delete", routePattern, handler);
        }

        public Builder options(Pattern routePattern, HttpHandler handler) {
            return route("options", routePattern, handler);
        }

        public Builder errorHandler(Function<Throwable, HttpHandler> errorHandler) {
            this.errorHandler = (t, exchange) -> errorHandler.apply(t).handle(exchange);
            return this;
        }

        public Builder errorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Builder notFoundHandler(HttpHandler handler) {
            this.notFoundHandler = handler;
            return this;
        }

        /**
         * Builds the {@link RegexRouter}.
         * @return The built router, ready to handle requests.
         */
        public RegexRouter build() {
            return new RegexRouter(this);
        }
    }

    public interface ErrorHandler {
        void handle(Throwable error, HttpExchange exchange) throws IOException;
    }
}
