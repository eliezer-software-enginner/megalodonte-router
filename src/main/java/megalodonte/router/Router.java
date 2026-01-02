package megalodonte.router;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import megalodonte.components.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Central routing manager responsible for navigation and window spawning.
 *
 * <p>The Router handles:</p>
 * <ul>
 *   <li>Main window navigation</li>
 *   <li>Dynamic route matching</li>
 *   <li>Secondary window lifecycle management</li>
 *   <li>Route parameter injection</li>
 * </ul>
 */
public class Router {

    /**
     * Route UI metadata.
     */
    public record RouteProps(int screenWidth, int screenHeight, String name) {}

    /**
     * Route definition.
     *
     * @param identification route path (static or dynamic, e.g. cad-prod/${id})
     * @param routerObjectFunction factory responsible for creating the screen instance
     * @param props visual configuration of the route
     */
    public record Route(
            String identification,
            Function<Router, Object> routerObjectFunction,
            RouteProps props
    ) {}

    private final Set<Route> routes;
    private final Stage mainStage;

    /**
     * Represents a spawned (secondary) window managed by the router.
     */
    private record SpawnedWindow(
            String identification,
            Route route,
            Stage stage
    ) {}

    private final List<SpawnedWindow> spawnedWindows = new ArrayList<>();

    /**
     * Creates a new Router instance and initializes the entrypoint route.
     *
     * @param routes all registered application routes
     * @param entrypointScreenName initial route identification
     * @param mainStage primary application stage
     * @throws ReflectiveOperationException if screen instantiation fails
     */
    public Router(
            Set<Route> routes,
            String entrypointScreenName,
            Stage mainStage
    ) throws ReflectiveOperationException {

        this.routes = routes;
        this.mainStage = mainStage;

        Scene scene = resolveAndCreateScene(entrypointScreenName, mainStage);
        mainStage.setScene(scene);
    }

    /**
     * Navigates the main stage to a given route.
     *
     * @param screenIdentification route identification to navigate to
     */
    public void navigateTo(String screenIdentification) {
        navigateTo(screenIdentification, e -> {});
    }

    /**
     * Navigates the main stage to a given route with error handling.
     *
     * @param screenIdentification route identification to navigate to
     * @param errorHandler callback invoked if navigation fails
     */
    public void navigateTo(
            String screenIdentification,
            Consumer<Exception> errorHandler
    ) {
        try {
            Scene scene = resolveAndCreateScene(screenIdentification, mainStage);
            mainStage.setScene(scene);
        } catch (Exception e) {
            errorHandler.accept(e);
        }
    }

    /**
     * Spawns a new window for the given route.
     *
     * @param screenIdentification route identification to spawn
     * @param errorHandler callback invoked if spawning fails
     */
    public void spawnWindow(
            String screenIdentification,
            Consumer<Exception> errorHandler
    ) {
        try {
            Stage stage = new Stage();
            Scene scene = resolveAndCreateScene(screenIdentification, stage);

            stage.setScene(scene);
            stage.show();

            ResolvedRoute resolved = resolveRoute(screenIdentification);

            spawnedWindows.add(
                    new SpawnedWindow(
                            screenIdentification,
                            resolved.route(),
                            stage
                    )
            );

            stage.setOnHidden(e ->
                    spawnedWindows.removeIf(w -> w.stage() == stage)
            );

        } catch (Exception e) {
            errorHandler.accept(e);
        }
    }

    /**
     * Spawns a new window and throws a runtime exception on error.
     *
     * @param screenIdentification route identification to spawn
     */
    public void spawnWindow(String screenIdentification) {
        spawnWindow(screenIdentification, RuntimeException::new);
    }

    /**
     * Closes the most recently spawned window.
     */
    public void closeSpawn() {
        if (spawnedWindows.isEmpty()) return;

        SpawnedWindow last = spawnedWindows.remove(spawnedWindows.size() - 1);
        last.stage().close();
    }

    /**
     * Closes a spawned window by its route identification.
     *
     * @param identification route identification of the window to close
     */
    public void closeSpawn(String identification) {
        Iterator<SpawnedWindow> it = spawnedWindows.iterator();

        while (it.hasNext()) {
            SpawnedWindow window = it.next();
            if (window.identification().equals(identification)) {
                window.stage().close();
                it.remove();
                return;
            }
        }
    }

    /**
     * Resolves a route and builds its Scene.
     *
     * @param identification route identification
     * @param targetStage stage where the scene will be applied
     * @return the resolved JavaFX Scene
     * @throws ReflectiveOperationException if rendering fails
     */
    private Scene resolveAndCreateScene(
            String identification,
            Stage targetStage
    ) throws ReflectiveOperationException {

        ResolvedRoute resolved = resolveRoute(identification);
        Route route = resolved.route();

        applyStageTitle(targetStage, route);

        Object screen = instantiateScreen(route, resolved.params());
        return buildScene(screen, route);
    }

    /**
     * Applies the route title to the given stage.
     */
    private void applyStageTitle(Stage stage, Route route) {
        String title = route.props().name();
        stage.setTitle(title != null ? title : mainStage.getTitle());
    }

    /**
     * Instantiates a screen and injects route parameters if supported.
     *
     * @param route resolved route
     * @param params extracted route parameters
     * @return instantiated screen
     */
    private Object instantiateScreen(
            Route route,
            Map<String, String> params
    ) {
        Object screen = route.routerObjectFunction().apply(this);

        if (screen instanceof RouteParamsAware aware) {
            aware.onRouteParams(params);
        }

        return screen;
    }

    /**
     * Builds a JavaFX Scene from a screen instance.
     *
     * @param screen instantiated screen
     * @param route resolved route
     * @return constructed JavaFX Scene
     * @throws ReflectiveOperationException if render method invocation fails
     */
    private Scene buildScene(Object screen, Route route)
            throws ReflectiveOperationException {

        Component component = (Component) screen
                .getClass()
                .getMethod("render")
                .invoke(screen);

        return new Scene(
                (Parent) component.getNode(),
                route.props().screenWidth(),
                route.props().screenHeight()
        );
    }

    /**
     * Represents a resolved route with extracted parameters.
     */
    private record ResolvedRoute(
            Route route,
            Map<String, String> params
    ) {}

    /**
     * Resolves a route path, supporting dynamic segments.
     */
    private ResolvedRoute resolveRoute(String path) {

        String[] pathParts = path.split("/");

        for (Route route : routes) {
            String[] routeParts = route.identification().split("/");

            if (routeParts.length != pathParts.length) continue;

            Map<String, String> params = new HashMap<>();
            boolean matched = true;

            for (int i = 0; i < routeParts.length; i++) {
                String rp = routeParts[i];
                String pp = pathParts[i];

                if (rp.startsWith("${") && rp.endsWith("}")) {
                    String key = rp.substring(2, rp.length() - 1);
                    params.put(key, pp);
                } else if (!rp.equals(pp)) {
                    matched = false;
                    break;
                }
            }

            if (matched) {
                return new ResolvedRoute(route, params);
            }
        }

        throw new RouteNotFoundException(path);
    }
}
