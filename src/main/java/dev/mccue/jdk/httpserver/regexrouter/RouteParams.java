package dev.mccue.jdk.httpserver.regexrouter;

import com.sun.net.httpserver.HttpExchange;

import java.util.Optional;

/**
 * Parameters extracted from a {@link com.sun.net.httpserver.Request}
 */
public sealed interface RouteParams permits RegexRouter.MatcherRouteParams {
    /**
     * Retrieves a positional parameter.
     * @param pos The position of the parameter.
     * @return The parameter, if there is one.
     */
    Optional<String> param(int pos);

    /**
     * Retrieves a named parameter.
     * @param name The name of the parameter.
     * @return The parameter, if there is one.
     */
    Optional<String> param(String name);

    static RouteParams get(HttpExchange exchange) {
        return (RouteParams) exchange.getAttribute("dev.mccue.jdk.httpserver.regexrouter/route-params");
    }

    default void set(HttpExchange exchange) {
        exchange.setAttribute("dev.mccue.jdk.httpserver.regexrouter/route-params", this);
    }
}
