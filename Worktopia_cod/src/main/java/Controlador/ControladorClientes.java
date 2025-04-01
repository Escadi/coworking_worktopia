/**
 * Clase ControladorClientes que se encarga de modificar y eliminar los datos de los clientes
 * de la base de datos.
 * Esta clase se encarga de modificar y eliminar los datos de los clientes de la base de datos.
 * Para ello, se encarga de realizar las consultas necesarias a la base de datos para modificar
 * o eliminar los datos de los clientes.
 */

package Controlador;

import ConexionDB.ConectionDB;
import javafx.scene.control.*;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class ControladorClientes {

    public void modificarClientes(TextField nombre, TextField primerApellido, TextField segundoApellido, TextField email, TextField telefono, String dni) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Quiere modificar el Cliente con dni: " + dni);

        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(si, no);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == si) {
            String query = "UPDATE Clientes SET nombre = ?, primerApellido = ?, segundoApellido = ?, email = ?, telefono = ? WHERE dni = ?";
            try (PreparedStatement update = ConectionDB.getConn().prepareStatement(query)) {
                update.setString(1, nombre.getText());
                update.setString(2, primerApellido.getText());
                update.setString(3, segundoApellido.getText());
                update.setString(4, email.getText());
                update.setString(5, telefono.getText());
                update.setString(6, dni);
                int actualizado = update.executeUpdate();
                if (actualizado > 0) {
                    alert2.setContentText("Datos actualizados correctamente");
                    alert2.show();
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void eliminarClientes(String dni) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Quiere eliminar el Cliente con dni: " + dni);
        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(si, no);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == si) {
            String query = "DELETE FROM Clientes WHERE dni = ?";
            try (PreparedStatement delete = ConectionDB.getConn().prepareStatement(query)) {
                delete.setString(1, dni);
                int actualizado = delete.executeUpdate();
                if (actualizado > 0) {
                    alert2.setContentText("Datos Eliminados correctamente");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
