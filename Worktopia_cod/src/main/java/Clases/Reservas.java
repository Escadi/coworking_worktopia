package Clases;

import ConexionDB.ConectionDB;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase Reservas.
 */
public class Reservas {
    private int id_reserva;
    private String dni;
    private int id_asiento;
    private int id_factura;
    private Timestamp fecha_hora_inicio;
    private Timestamp fecha_hora_fin;
    private BigDecimal subtotal;

    public Reservas(int id_reserva, String dni, int id_asiento, int id_factura, Timestamp fecha_hora_inicio, Timestamp fecha_hora_fin, BigDecimal subtotal) {
        this.id_reserva = id_reserva;
        this.dni = dni;
        this.id_asiento = id_asiento;
        this.id_factura = id_factura;
        this.fecha_hora_inicio = fecha_hora_inicio;
        this.fecha_hora_fin = fecha_hora_fin;
        this.subtotal = subtotal;
    }

    public int getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getId_asiento() {
        return id_asiento;
    }

    public void setId_asiento(int id_asiento) {
        this.id_asiento = id_asiento;
    }

    public int getId_factura() {
        return id_factura;
    }

    public void setId_factura(int id_factura) {
        this.id_factura = id_factura;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Ambs funciones siguientes se usan para calcular el subtotal de la reserva.
     */

    public BigDecimal calcularSubtotal() {
        BigDecimal tarifaHora = getTarifaHoraId(id_asiento);
        LocalDateTime inicio = fecha_hora_inicio.toLocalDateTime();
        LocalDateTime fin = fecha_hora_fin.toLocalDateTime();
        long horas = Duration.between(inicio, fin).toHours();
        return tarifaHora.multiply(BigDecimal.valueOf(horas));
    }

    private BigDecimal getTarifaHoraId(int id_asiento) {
        List<Asientos> asientos = ConectionDB.getAsientos();
        for (Asientos asiento : asientos) {
            if (asiento.getId_asiento() == id_asiento) {
                return asiento.getTarifa_hora();
            }
        }
        return BigDecimal.ZERO;
    }

    public Timestamp getFecha_hora_inicio() {
        return fecha_hora_inicio;
    }

    public void setFecha_hora_inicio(Timestamp fecha_hora_inicio) {
        this.fecha_hora_inicio = fecha_hora_inicio;
    }

    public Timestamp getFecha_hora_fin() {
        return fecha_hora_fin;
    }

    public void setFecha_hora_fin(Timestamp fecha_hora_fin) {
        this.fecha_hora_fin = fecha_hora_fin;
    }

    @Override
    public String toString() {
        return "Reservas{" +
                "id_reserva=" + id_reserva +
                ", dni='" + dni + '\'' +
                ", id_cliente=" + id_factura +
                ", id_asiento=" + id_asiento +
                ", fecha_hora_inicio=" + fecha_hora_inicio +
                ", fecha_hora_fin=" + fecha_hora_fin +
                ", subtotal=" + subtotal +
                '}';
    }
}
