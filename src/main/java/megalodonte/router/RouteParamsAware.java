package megalodonte.router;

import java.util.Map;

public interface RouteParamsAware {
    void onRouteParams(Map<String, String> params);
}