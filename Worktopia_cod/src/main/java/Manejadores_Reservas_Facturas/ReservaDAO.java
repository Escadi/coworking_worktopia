/**
 * Clase que se encarga de realizar las operaciones de la base de datos relacionadas con las reservas.
 * Se encarga de obtener las reservas, insertar una nueva reserva, obtener la factura pendiente de un cliente y crear una nueva factura.
 */
package Manejadores_Reservas_Facturas;

import Clases.Reservas;
import ConexionDB.ConectionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public static List<Reservas> getReservas() throws SQLException {
        List<Reservas> reservas = new ArrayList<>();
        String query = "SELECT id_reserva, dni, id_asiento, id_factura, fecha_hora_inicio, fecha_hora_fin, subtotal FROM Reservas";
        try (PreparedStatement ps = ConectionDB.getConn().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reservas reserva = new Reservas(
                        rs.getInt("id_reserva"),
                        rs.getString("dni"),
                        rs.getInt("id_asiento"),
                        rs.getInt("id_factura"),
                        rs.getTimestamp("fecha_hora_inicio"),
                        rs.getTimestamp("fecha_hora_fin"),
                        rs.getBigDecimal("subtotal")
                );
                BigDecimal subtotal = reserva.calcularSubtotal();
                reserva.setSubtotal(subtotal);
                reservas.add(reserva);
            }
        }
        return reservas;
    }

    public static int insertarReserva(Reservas reserva) throws SQLException {
        String query = "INSERT INTO Reservas (dni, id_asiento, id_factura, fecha_hora_inicio, fecha_hora_fin, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConectionDB.getConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, reserva.getDni());
            ps.setInt(2, reserva.getId_asiento());
            ps.setInt(3, reserva.getId_factura());
            ps.setTimestamp(4, reserva.getFecha_hora_inicio());
            ps.setTimestamp(5, reserva.getFecha_hora_fin());
            ps.setBigDecimal(6, reserva.getSubtotal());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted == 0) {
                throw new SQLException("No se pudo crear la reserva.");
            }

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la reserva.");
            }
        }
    }

    public static int obtenerFacturaPendiente(String dni) throws SQLException {
        String query = "SELECT id_factura FROM Facturas WHERE dni = ? AND estado = 'Pendiente' ORDER BY fecha_hora_emision DESC LIMIT 1";
        try (PreparedStatement ps = ConectionDB.getConn().prepareStatement(query)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_factura");
            }
        }
        return -1;
    }

    public static int crearFactura(String dni, BigDecimal precioTotal) throws SQLException {
        int nuevoIdFactura = obtenerMaxIdFactura() + 1;
        String query = "INSERT INTO Facturas (id_factura, dni, precio_total, descuento, fecha_hora_emision, estado, subtotal) VALUES (?, ?, ?, 0.00, NOW(), 'Pendiente',?)";
        try (PreparedStatement ps = ConectionDB.getConn().prepareStatement(query)) {
            ps.setInt(1, nuevoIdFactura);
            ps.setString(2, dni);
            ps.setBigDecimal(3, precioTotal);
            ps.setBigDecimal(4, precioTotal);
            ps.executeUpdate();
            return nuevoIdFactura;
        }
    }

    private static int obtenerMaxIdFactura() throws SQLException {
        String query = "SELECT MAX(id_factura) AS max_id FROM Facturas";
        try (PreparedStatement ps = ConectionDB.getConn().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id");
            }
        }
        return 0; // Return 0 if there are no records
    }
}