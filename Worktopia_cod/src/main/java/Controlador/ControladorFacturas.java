/**
 * Clase ControladorFacturas que se encarga de gestionar las facturas de la aplicación.
 * Se encarga de modificar, eliminar y abonar facturas.
 * También se encarga de obtener la forma de pago de una factura.
 */
package Controlador;

import ConexionDB.ConectionDB;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class ControladorFacturas {

    public void modificarFactura(TextField fechaFactura, TextField descuento, TextField total, TextField subtotal, int idFactura) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Quiere modificar la factura? :" + idFactura);

        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(si, no);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == si) {
            String queryUrl = "UPDATE Facturas SET fecha_hora_emision = ?,descuento = ?, precio_total = ? , subtotal = ? WHERE id_factura = ?";
            try {
                PreparedStatement stmt = ConectionDB.getConn().prepareStatement(queryUrl);
                stmt.setString(1, fechaFactura.getText());
                stmt.setString(2, descuento.getText());
                stmt.setString(3, total.getText());
                stmt.setString(4, subtotal.getText());
                stmt.setInt(5, idFactura);
                int confirmacion = stmt.executeUpdate();
                if (confirmacion == 1) {
                    showAlert("Modificar Factura", "La factura se ha modificado Correctamente");
                } else {
                    showAlert("Modificar Factura", "No se ha modificado la factura");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void eliminarFactura(int idFactura) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Quiere eliminar la factura? :" + idFactura);

        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(si, no);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == si) {
            String queryUrl = "DELETE FROM Facturas WHERE id_factura = ?";
            String subquery = "DELETE FROM Reservas WHERE id_factura = ?";
            try {
                //Eliminamos la reserva
                PreparedStatement stmt = ConectionDB.getConn().prepareStatement(subquery);
                stmt.setInt(1, idFactura);
                stmt.executeUpdate();
                //Eliminamos la factura
                PreparedStatement stmt2 = ConectionDB.getConn().prepareStatement(queryUrl);
                stmt2.setInt(1, idFactura);

                int confirmacion2 = stmt2.executeUpdate();
                if (confirmacion2 == 1) {
                    showAlert("Eliminar Factura", "La factura se ha eliminado Correctamente");
                } else {
                    showAlert("Eliminar Factura", "No se ha elimado la factura");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void abonarFactura(TextField fechaFactura, TextField descuento, TextField total, TextField subtotal, ComboBox formaPago, int idFactura) {
        String estadoPago = "Pagada";
        LocalDateTime fecha = LocalDateTime.now();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Quiere abonar la factura? :" + idFactura);

        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(si, no);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == si) {
            String queryUrl = "UPDATE Facturas SET fecha_hora_emision = ?,descuento = ?, precio_total = ?, fecha_hora_pago = ? ,estado = ? , subtotal = ? , forma_pago = ? WHERE id_factura = ?";
            try {
                PreparedStatement stmt = ConectionDB.getConn().prepareStatement(queryUrl);
                stmt.setString(1, fechaFactura.getText());
                stmt.setString(2, descuento.getText());
                stmt.setString(3, subtotal.getText());
                stmt.setString(4, fecha.toString());
                stmt.setString(5, estadoPago);
                stmt.setString(6, total.getText());
                stmt.setString(7, formaPago.getSelectionModel().getSelectedItem().toString());
                stmt.setInt(8, idFactura);
                int confirmacion = stmt.executeUpdate();
                if (confirmacion == 1) {
                    showAlert("Pagar Factura", "La factura se ha Pagado Correctamente");
                } else {
                    showAlert("Pagar Factura", "No se ha Pagado la factura");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String obtenerForma(int idFactura) {
        String estado = "";
        String query = "SELECT estado FROM Facturas WHERE id_factura = ?";
        try (PreparedStatement ps = ConectionDB.getConn().prepareStatement(query)) {
            ps.setInt(1, idFactura);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                estado = rs.getString("estado");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return estado;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
