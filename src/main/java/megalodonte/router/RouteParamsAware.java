package megalodonte.router;

import java.util.Map;

/**
 * Optional contract for screens that want to receive
 * dynamic route parameters.
 *
 * <p>If a screen implements this interface, the Router will
 * automatically inject the resolved route parameters before
 * the screen is rendered.</p>
 *
 * <p>This allows screens to remain decoupled from the routing
 * mechanism while still supporting dynamic routes.</p>
 */
public interface RouteParamsAware {

    /**
     * Called by the Router when route parameters are available.
     *
     * @param params a map containing resolved route parameters
     *               (e.g. "id" -> "123")
     */
    void onRouteParams(Map<String, String> params);
}
