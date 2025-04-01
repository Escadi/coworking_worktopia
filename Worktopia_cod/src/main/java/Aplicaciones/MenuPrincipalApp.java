/**
 * Worktopia, Aplicación de Coworking
 * <p>
 * Creadores de la aplicación:
 *
 * @author David Liaño Macías
 * @author Eliu Manuel Viera Lorenzo
 * @version 1.0
 */
package Aplicaciones;


import ConexionDB.ConectionDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.image.Image;

/**
 * Clase principal para la aplicación.
 */
public class MenuPrincipalApp extends Application {
    private static final Image icono = new Image(MenuPrincipalApp.class.getResourceAsStream("/Imagenes/bannerTopiaC.png"));

    @Override
    public void start(Stage menu) throws IOException {
        FXMLLoader menuPrincipal = new FXMLLoader(MenuPrincipalApp.class.getResource("/Menus/MenuPrincipal.fxml"));
        menu.getIcons().add(icono);
        menu.setTitle("Menu Principal");
        menu.setScene(new Scene(menuPrincipal.load()));
        menu.getMaxHeight();
        menu.setMaxWidth(1300);
        menu.maximizedProperty().addListener((obs, oldVal, isMaximized) -> {
            if (isMaximized) {
                menu.setFullScreen(true);
            }
        });
        menu.show();
    }

    public static void agregarIcono(Stage ventana) {
        ventana.getIcons().add(icono);

    }

    public static void main(String[] args) throws ClassNotFoundException {
        ConectionDB.openConn();
        launch();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ConectionDB.closeConn();
            System.out.println("Connection closed.");
        }));
    }
}
