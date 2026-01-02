package megalodonte.router;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String identification) {
        super("Rota não encontrada: " + identification);
    }
}