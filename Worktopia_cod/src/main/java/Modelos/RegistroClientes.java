/**
 * Clase RegistroClientes que se encarga de gestionar el registro de los clientes en la base de datos.
 * Implementa los métodos necesarios para la inserción de los datos de los clientes en la base de datos.
 */
package Modelos;

import ConexionDB.ConectionDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class RegistroClientes {

    @FXML
    private TextField dni;
    @FXML
    private TextField nombre;
    @FXML
    private TextField primerApellido;
    @FXML
    private TextField segundoApellido;
    @FXML
    private TextField eMail;
    @FXML
    private TextField telefono;
    @FXML
    private Button btnConfirmar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button BtnUsuarios;
    @FXML
    private Button Reservas;
    @FXML
    private Button ListaReservas;
    @FXML
    private Button ListaClientes;
    @FXML
    private Button Facturacion;
    @FXML
    private Button btnSalir;

    @FXML
    public void initialize() {
        btnSalir.setStyle(("-fx-background-color: transparent;"));
    }

    public RegistroClientes() {
    }

    public void guardarCliente(ActionEvent event) {
        agregarCliente();

    }

    public void salirVentana(ActionEvent event) {
        ((Stage) btnSalir.getScene().getWindow()).close();
    }


    public void agregarCliente() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String dniText = this.dni.getText();
        String nombreText = this.nombre.getText();
        String primerApellidoText = this.primerApellido.getText();
        String segundoApellidoText = this.segundoApellido.getText();
        String EmailText = this.eMail.getText();
        String telefonoText = this.telefono.getText();

        if (dniText.isEmpty() || nombreText.isEmpty() || primerApellidoText.isEmpty() || segundoApellidoText.isEmpty() || EmailText.isEmpty() || telefonoText.isEmpty()) {
            alert.setContentText("Debe completar todos los campos");
            alert.show();
        } else {
            try {
                String query = "INSERT INTO Clientes (dni, nombre, primerApellido, segundoApellido, eMail, telefono) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = ConectionDB.getConn().prepareStatement(query)) {
                    stmt.setString(1, dni.getText());
                    stmt.setString(2, nombre.getText());
                    stmt.setString(3, primerApellido.getText());
                    stmt.setString(4, segundoApellido.getText());
                    stmt.setString(5, eMail.getText());
                    stmt.setString(6, telefono.getText());

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        alert.setContentText("Usuario agregado correctamente");
                        alert.show();

                    } else {
                        alert.setContentText("Usuario no agregado");
                        alert.show();
                    }


                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }


    }


}
