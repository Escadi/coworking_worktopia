package Clases;

import java.math.BigDecimal;

/**
 * Clase Asientos.
 */
public class Asientos {

    private int id_asiento;
    private String nombre;
    private BigDecimal tarifa_hora;


    public Asientos(int id_asiento, String nombre, BigDecimal tarifa_hora) {
        this.id_asiento = id_asiento;
        this.nombre = nombre;
        this.tarifa_hora = tarifa_hora;
    }

    public int getId_asiento() {
        return id_asiento;
    }

    public void setId_asiento(int id_asiento) {
        this.id_asiento = id_asiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getTarifa_hora() {
        return tarifa_hora;
    }

    public void setTarifa_hora(BigDecimal tarifa_hora) {
        this.tarifa_hora = tarifa_hora;
    }

    @Override
    public String toString() {
        return "Asientos{" +
                "id_asiento=" + id_asiento +
                ", nombre='" + nombre + '\'' +
                ", tarifa_hora=" + tarifa_hora +
                '}';
    }
}