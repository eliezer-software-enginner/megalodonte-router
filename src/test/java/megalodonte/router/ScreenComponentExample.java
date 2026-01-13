// Exemplo de uso da nova interface ScreenComponent

package my_app.screens;

import megalodonte.base.ScreenComponent;
import megalodonte.components.*;
import megalodonte.props.ButtonProps;

// Forma recomendada: implementar ScreenComponent
public class HomeScreen implements ScreenComponent {
    
    @Override
    public Component render() {
        return new Column(
            new Text("Bem-vindo ao Home!"),
            new Button("Clique aqui", new ButtonProps()
                .primary()
                .onClick(() -> System.out.println("Botão clicado!"))
            ),
            new Text("Esta tela implementa ScreenComponent")
        );
    }
    
    @Override
    public void onMount() {
        System.out.println("HomeScreen foi montada!");
        // Aqui você pode:
        // - Carregar dados
        // - Configurar subscrições 
        // - Iniciar timers
        // - Fazer setup inicial
    }
}

// Forma legada (ainda suportada)
class LegacyScreen {
    
    public Component render() {
        return new Text("Tela legada");
    }
    
    public void onMount() {
        System.out.println("Tela legada montada");
    }
}