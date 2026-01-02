package megalodonte;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import megalodonte.components.Component;
import megalodonte.router.RouteNotFoundException;
import megalodonte.router.RouteParamsAware;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class Router {
    //900, 700, Cadastro de produtos
    public record RouteProps(int screenWidth, int screenHeight, String name){}
    //public record Route(RouteType type, Class<?> klass, RouteProps props){}

    //identification pode ser: cad-prod ou cad-prod/${}, exemplo cad-prod/${id}
    public record Route(String identification, Function<Router, Object> routerObjectFunction, RouteProps props){}
    private final Set<Route> routes;
    private final Stage mainStage;

    //private record InnerScreenType(Class<?> screenClass, Scene scene){}
    private record InnerData(Route route, Scene scene){}

    public Router(Set<Route> routes, String entrypointScreenName, Stage mainStage) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.routes = routes;
        //private final Class<?> entrypointScreen;
        this.mainStage = mainStage;

        //cada screen tem sua scene atrelada a ela
        //obter a rota que contém a entrypoint

        final var scene = generateSceneFromScreenName(entrypointScreenName);
        mainStage.setScene(scene);
    }

    private Scene generateSceneFromScreenName(String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var opRoute = this.routes.stream().filter(route -> route.identification().equals(name)).findFirst();

        if(opRoute.isPresent()) {
            final var route = opRoute.get();

            var identification = route.identification;

            final var screenObj = route.routerObjectFunction.apply(this);
            final var entrypointScreenClass = screenObj.getClass();

            var component = (Component) entrypointScreenClass.getMethod("render").invoke(screenObj);
            final var scene = new Scene((Parent) component.getNode(), route.props.screenWidth(), route.props.screenHeight());

            return scene;
        }
        return null;
        }

    private record ResolvedRoute(
            Route route,
            java.util.Map<String, String> params
    ) {}

    public void navigateTo(String screenName) {
        try{
            final var scene = generateSceneFromScreenName(screenName);
            mainStage.setScene(scene);
        }catch (Exception e){
        }
    }

    public void navigateTo(String screenName, Consumer<Exception> errorHandler) {
        try{
            final var scene = generateSceneFromScreenName(screenName);
            mainStage.setScene(scene);
        }catch (Exception e){
            errorHandler.accept(e);
        }
    }

    public void spawnWindow(
            String screenIdentification,
            Consumer<Exception> errorHandler
    ) {
        try {
            var stage = new Stage();
            var scene = generateSceneFromIdentificationRoute(screenIdentification, stage);

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            errorHandler.accept(e);
        }
    }

    public void spawnWindow(String screenIdentification) {
        spawnWindow(screenIdentification, e -> {
            throw new RuntimeException(e);
        });
    }

    private Scene generateSceneFromIdentificationRoute(
            String identification,
            Stage stage
    ) throws ReflectiveOperationException {

        var resolved = resolveRoute(identification);
        var route = resolved.route();
        var params = resolved.params();

        stage.setTitle(route.props().name());

        var screenObj = route.routerObjectFunction().apply(this);

        // INJETANDO PARAMS SE EXISTIR
        if (screenObj instanceof RouteParamsAware aware) {
            aware.onRouteParams(params);
        }

        var component = (Component) screenObj
                .getClass()
                .getMethod("render")
                .invoke(screenObj);

        return new Scene(
                (Parent) component.getNode(),
                route.props().screenWidth(),
                route.props().screenHeight()
        );
    }

    private ResolvedRoute resolveRoute(String path) {

        var pathParts = path.split("/");

        for (var route : routes) {
            var routeParts = route.identification().split("/");

            if (routeParts.length != pathParts.length) continue;

            var params = new java.util.HashMap<String, String>();
            boolean matched = true;

            for (int i = 0; i < routeParts.length; i++) {
                var rp = routeParts[i];
                var pp = pathParts[i];

                if (rp.startsWith("${") && rp.endsWith("}")) {
                    var key = rp.substring(2, rp.length() - 1);
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


//    private Scene generateSceneFromIdentificationRoute(
//            String identification,
//            Stage stage
//    ) throws ReflectiveOperationException {
//
//        final var route = this.routes.stream()
//                .filter(r -> r.identification().equals(identification))
//                .findFirst()
//                .orElseThrow(() -> new RouteNotFoundException(identification));
//
//        stage.setTitle(route.props().name());
//
//        final var screenObj = route.routerObjectFunction().apply(this);
//        final var screenClass = screenObj.getClass();
//
//        final var component = (Component) screenClass
//                .getMethod("render")
//                .invoke(screenObj);
//
//        return new Scene(
//                (Parent) component.getNode(),
//                route.props().screenWidth(),
//                route.props().screenHeight()
//        );
//    }
}
