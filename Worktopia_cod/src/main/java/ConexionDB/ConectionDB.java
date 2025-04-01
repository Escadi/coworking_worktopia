package ConexionDB;

import Clases.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que establece la conexión con la base de datos.
 */

public class ConectionDB {
    //     private static final String URL = "jdbc:mysql://bd-4free.net";
    private static final String URL = "jdbc:mysql://bt33sWO8fH9pN8LVVDGr@bwj6lss5tgchvux8qwv9-mysql.services.clever-cloud.com:3306/bwj6lss5tgchvux8qwv9";
    private static final String PORT = "3306";
    //private static final String USER = "worktopia";
    private static final String USER = "uqxj2bxqdgq4e1fp";
    //private static final String PASSWORD = "iesrincon";
    private static final String PASSWORD = "bt33sWO8fH9pN8LVVDGr";
    private static final String DATABASE = "worktopiaDB";
    private static Statement stmt;
    private static Connection conn;
    private static ResultSet rs;

    /**
     * Método que abre la conexión con la base de datos.
     */
    public static void openConn() throws ClassNotFoundException {
        // Hay que asegurar que ejecuta el DRIVER de MySQL
        Class.forName("com.mysql.cj.jdbc.Driver");
        try {

        } catch (Exception ex) {

        }

        try {
            String sUrl = URL + ":" + PORT + "/" + DATABASE + "?zeroDateTimeBehavior=convertToNull";
            conn = DriverManager.getConnection("jdbc:mysql://uqxj2bxqdgq4e1fp:bt33sWO8fH9pN8LVVDGr@bwj6lss5tgchvux8qwv9-mysql.services.clever-cloud.com:3306/bwj6lss5tgchvux8qwv9", USER, PASSWORD);
            System.out.println("Connected to: " + sUrl);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConn() {
        return conn;
    }

    /**
     * Método que cierra la conexión con la base de datos.
     */
    public static void closeConn() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static Statement getStmt() {
        return stmt;
    }


    /**
     * Método que adquiere una lista de clientes.
     *
     * @return Lista de clientes.
     */
    public static List<Clientes> getClientes() {
        List<Clientes> clientes = new ArrayList<>();
        String query = "SELECT dni, nombre, primerApellido, segundoApellido, email, telefono FROM Clientes";
        try {
            if (conn != null) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Clientes cliente = new Clientes(
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("primerApellido"),
                            rs.getString("segundoApellido"),
                            rs.getString("email"),
                            rs.getString("telefono")
                    );
                    clientes.add(cliente);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    /**
     * Método que adquiere una lista de asientos.
     *
     * @return Lista de asientos.
     */
    public static List<Asientos> getAsientos() {
        List<Asientos> asientos = new ArrayList<>();
        String query = "SELECT id_asiento, nombre, tarifa_hora FROM Asientos";
        try {
            if (conn != null) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Asientos asiento = new Asientos(
                            rs.getInt("id_asiento"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("tarifa_hora")

                    );
                    asientos.add(asiento);
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asientos;
    }


    /**
     * Método que adquiere una lista de reservas.
     * Además, calcula el subtotal de cada reserva.
     *
     * @return Lista de reservas.
     */
    public static List<Reservas> getReservas() {
        List<Reservas> reservas = new ArrayList<>();
        String query = "SELECT id_reserva, dni, id_asiento, id_factura, fecha_hora_inicio, fecha_hora_fin , subtotal FROM Reservas";
        try (PreparedStatement ps = conn.prepareStatement(query);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }


    /**
     * Método que adquiere una lista de facturas.
     *
     * @return Lista de facturas.
     */
    public static List<Facturas> getFacturas() {
        List<Facturas> facturas = new ArrayList<>();
        String query = "SELECT id_factura, dni, precio_total, descuento, fecha_hora_emision, estado, fecha_hora_pago, forma_pago , subtotal FROM Facturas";
        try {
            if (conn != null) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Facturas factura = new Facturas(
                            rs.getInt("id_factura"),
                            rs.getString("dni"),
                            rs.getBigDecimal("precio_total"),
                            rs.getBigDecimal("descuento"),
                            rs.getString("fecha_hora_emision"),
                            rs.getString("estado"),
                            rs.getString("fecha_hora_pago"),
                            rs.getString("forma_pago"),
                            rs.getBigDecimal("subtotal")
                    );
                    facturas.add(factura);
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facturas;
    }


    /**
     * Método que adquiere una lista de usuarios.
     *
     * @return Lista de usuarios.
     */
    public static List<Usuarios> getUsuarios() {
        List<Usuarios> usuarios = new ArrayList<>();
        String query = "SELECT id_usuario, nombre, email, contrasenia, categoria FROM Usuarios";
        try {
            if (conn != null) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Usuarios usuario = new Usuarios(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("contrasenia"),
                            rs.getString("categoria")
                    );
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

}