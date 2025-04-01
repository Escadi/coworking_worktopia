/**
 * Clase ListaClientes que se encarga de mostrar la lista de clientes en la tabla y de gestionar las acciones de los botones
 * de la interfaz de usuario.
 */
package Modelos;

import Aplicaciones.MenuPrincipalApp;
import Clases.Clientes;
import Clases.SesionUsuario;
import ConexionDB.ConectionDB;
import Controlador.ControladorClientes;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;

import java.io.IOException;


public class ListaClientes {
    private ControladorClientes controladorClientes = new ControladorClientes();
    @FXML
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private Button AgregarClientes;
    @FXML
    private Button Reservas;
    @FXML
    private Button ListaReservas;
    @FXML
    private Button Facturacion;
    @FXML
    private Button BtnUsuarios;
    @FXML
    private TextField DNIbuscar;
    @FXML
    private TextField TextNombre;
    @FXML
    private TextField TextPrimerApellido;
    @FXML
    private TextField TextSegundoApellido;
    @FXML
    private TextField TextEmail;
    @FXML
    private TextField TextTelefono;
    @FXML
    private Button btnSalir;

    private String dni;
    @FXML
    private TextField TextDniGhost;
    @FXML
    private VBox vboxIconos;
    @FXML
    private Button BtnGenerarReserva;

    @FXML
    private TableView<Clientes> tablaClientes;
    @FXML
    private TableColumn<Clientes, String> colDNI;
    @FXML
    private TableColumn<Clientes, String> colNombre;
    @FXML
    private TableColumn<Clientes, String> colPrimerApellido;
    @FXML
    private TableColumn<Clientes, String> colSegundoApellido;
    @FXML
    private TableColumn<Clientes, String> colEmail;
    @FXML
    private TableColumn<Clientes, String> colTelefono;

    private static final StringProperty seleccionDni = new SimpleStringProperty();

    /**
     * Método initialize que se encarga de inicializar la tabla de clientes, de cargar los datos de los clientes en la tabla,
     * de filtrar los datos de la tabla, de mostrar los datos de un cliente seleccionado en los campos de texto y de gestionar
     * la sesión del usuario.
     * Además, se encarga de gestionar la acción de los botones de la interfaz de usuario.
     */

    @FXML
    public void initialize() {
        btnSalir.setStyle(("-fx-background-color: transparent;"));
        TextDniGhost.setText(dni);
        Stage ventanaSecundaria = new Stage();
        ventanaSecundaria.getIcons().add(new Image(getClass().getResourceAsStream("/Imagenes/bannerTopiaC.png")));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrimerApellido.setCellValueFactory(new PropertyValueFactory<>("primerApellido"));
        colSegundoApellido.setCellValueFactory(new PropertyValueFactory<>("segundoApellido"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));


        ObservableList<Clientes> clientesList = FXCollections.observableArrayList(ConectionDB.getClientes());
        tablaClientes.setItems(clientesList);

        FilteredList<Clientes> filtroClientes = new FilteredList<Clientes>(clientesList, p -> true);
        DNIbuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroClientes.setPredicate(Clientes -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                } else {
                    return Clientes.getDni().toLowerCase().contains(newValue.toLowerCase());
                }
            });
        });

        SortedList<Clientes> sortedData = new SortedList<>(filtroClientes);
        sortedData.comparatorProperty().bind(tablaClientes.comparatorProperty());
        tablaClientes.setItems(sortedData);


        tablaClientes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Clientes cliente = tablaClientes.getSelectionModel().getSelectedItem();
                if (cliente != null) {
                    exportarDatos(cliente);
                }
            }
        });

        vboxIconos.setOnMousePressed(event -> {
            xOffset = event.getSceneX() - vboxIconos.getLayoutX() + 250;
            yOffset = event.getSceneY() - vboxIconos.getLayoutY() + 70;
        });
        vboxIconos.setOnMouseDragged(event -> {
            vboxIconos.setLayoutX(event.getScreenX() - xOffset);
            vboxIconos.setLayoutY(event.getScreenY() - yOffset);
        });
        inicioSesion();

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newdni) -> {
            if (newdni != null) {
                seleccionDni.set(String.valueOf(newdni));
            }
        });
        TextDniGhost.textProperty().bind(seleccionDni);


    }

    public ListaClientes() {
    }

    public static StringProperty getSelectSeleccionDni() {
        return seleccionDni;
    }

    /**
     * Método que se encarga generar una reserva de un cliente seleccionado en la tabla de clientes.
     */

    public void generarReserva(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menus/Reservas.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro Usuarios");
            stage.initStyle(StageStyle.UNDECORATED);
            MenuPrincipalApp.agregarIcono(stage);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void exportarDatos(Clientes clientes) {
        dni = clientes.getDni();
        TextNombre.setText(clientes.getNombre());
        TextPrimerApellido.setText(clientes.getPrimerApellido());
        TextSegundoApellido.setText(clientes.getSegundoApellido());
        TextEmail.setText(clientes.getEmail());
        TextTelefono.setText(clientes.getTelefono());
        vboxIconos.setVisible(true);
    }

    public void modificarDatos(ActionEvent event) {
        controladorClientes.modificarClientes(TextNombre, TextPrimerApellido, TextSegundoApellido, TextEmail, TextTelefono, dni);
        initialize();

    }

    public void eliminarDatos(ActionEvent event) {
        controladorClientes.eliminarClientes(dni);
        initialize();
    }

    public void inicioSesion() {
        String categoria = SesionUsuario.getCategoriaUsuario();

        if (categoria != null && categoria.equals("Admin")) {
            BtnUsuarios.setVisible(true);
            BtnUsuarios.setDisable(false);
        } else {
            BtnUsuarios.setVisible(false);
        }
    }

    public void abrirVentana(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.setResizable(false);
            stage.setMaximized(false);
            MenuPrincipalApp.agregarIcono(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ventanaRegistro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menus/RegistroClientes.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro Cliente");
            stage.initStyle(StageStyle.UNDECORATED);
            MenuPrincipalApp.agregarIcono(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void ventanaFacturaciones(ActionEvent event) {
        abrirVentana("/Menus/Facturacion.fxml", "Factuacion");
        ((Stage) Facturacion.getScene().getWindow()).close();
    }

    public void ventanaListaReservas(ActionEvent event) {
        abrirVentana("/Menus/ListaReservas.fxml", "Listas De Reservas");
        ((Stage) ListaReservas.getScene().getWindow()).close();
    }

    public void ventanaListaUsuarios(ActionEvent event) {
        abrirVentana("/Menus/ListaUsuarios.fxml", "Lista De Usuarios");
        ((Stage) BtnUsuarios.getScene().getWindow()).close();
    }

    public void salirVbox(ActionEvent event) {
        vboxIconos.setVisible(false);
    }

}