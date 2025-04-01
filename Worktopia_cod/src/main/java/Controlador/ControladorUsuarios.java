/**
 * Controlador de la vista de usuarios
 * En este controlador se encuentran los métodos para modificar y eliminar usuarios
 * de la base de datos.
 */
package Controlador;

import ConexionDB.ConectionDB;
import javafx.scene.control.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class ControladorUsuarios {

    public void modificarUsuario(TextField nombre, TextField email, ComboBox categoria, int idusuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Quiere modificar El Usuario :" + idusuario);

        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(si, no);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == si) {
            String query = "UPDATE Usuarios SET nombre = ?, email = ?, categoria = ? WHERE id_usuario = ?";
            try (PreparedStatement update = ConectionDB.getConn().prepareStatement(query)) {
                update.setString(1, nombre.getText());
                update.setString(2, email.getText());
                update.setString(3, categoria.getSelectionModel().getSelectedItem().toString());
                update.setInt(4, idusuario);
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

    public void eliminarUsuario(int idusuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Quiere eliminar el usuario :" + idusuario);
        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(si, no);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == si) {
            String query = "DELETE FROM Usuarios WHERE id_usuario = ?";
            try (PreparedStatement delete = ConectionDB.getConn().prepareStatement(query)) {
                delete.setInt(1, idusuario);
                int actualizado = delete.executeUpdate();
                if (actualizado > 0) {
                    alert2.setContentText("Datos Eliminados correctamente");
                    alert2.show();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
