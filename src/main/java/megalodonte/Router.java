package megalodonte;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class Router {
    public record RouteProps(int screenWidth, int screenHeight){}
    //public record Route(RouteType type, Class<?> klass, RouteProps props){}
    public record Route(String name, Function<Router, Object> routerObjectFunction, RouteProps props){}
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
        var opRoute = this.routes.stream().filter(route -> route.name().equals(name)).findFirst();

        if(opRoute.isPresent()) {
            final var route = opRoute.get();

            final var screenObj = route.routerObjectFunction.apply(this);
            final var entrypointScreenClass = screenObj.getClass();

            var component = (Component) entrypointScreenClass.getMethod("render").invoke(screenObj);
            final var scene = new Scene((Parent) component.getNode(), route.props.screenWidth(), route.props.screenHeight());
            return scene;
        }
        return null;
        }


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

    public void spawnWindow(String screenName, Consumer<Exception> errorHandler) {
        try{
            final var scene = generateSceneFromScreenName(screenName);
            var stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            errorHandler.accept(e);
        }
    }

    public void spawnWindow(String screenName) {
        try{
            final var scene = generateSceneFromScreenName(screenName);
            var stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
        }
    }
}
