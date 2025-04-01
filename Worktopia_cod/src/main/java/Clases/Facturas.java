package Clases;

import java.math.BigDecimal;

/**
 * Clase Facturas.
 */
public class Facturas {
    private Integer id_factura;
    private String dni;
    private BigDecimal precio_total;
    private BigDecimal descuento;
    private String fecha_hora_emision;
    private String estado;
    private String fecha_hora_pago;
    private String forma_pago;
    private BigDecimal subtotal;

    public Facturas(Integer id_factura, String dni, BigDecimal precio_total, BigDecimal descuento, String fecha_hora_emision, String estado, String fecha_hora_pago, String forma_pago, BigDecimal subtotal) {
        this.id_factura = id_factura;
        this.dni = dni;
        this.precio_total = precio_total;
        this.descuento = descuento;
        this.fecha_hora_emision = fecha_hora_emision;
        this.estado = estado;
        this.fecha_hora_pago = fecha_hora_pago;
        this.forma_pago = forma_pago;
        this.subtotal = subtotal;

    }

    /**
     * Clase Otro formato de construcción de Facturas por conveniencia de uso en otras funciones.
     */

    public Facturas(int idFactura, String fechaHoraEmision, BigDecimal totalConDescuento, String dni, BigDecimal descuento, BigDecimal totalSubtotales, String estado, String fechaHoraPago, String formaPago) {
        this.id_factura = idFactura;
        this.fecha_hora_emision = fechaHoraEmision;
        this.precio_total = totalConDescuento;
        this.dni = dni;
        this.descuento = descuento;
        this.subtotal = totalSubtotales;
        this.estado = estado;
        this.fecha_hora_pago = fechaHoraPago;
        this.forma_pago = formaPago;
    }

    public Integer getId_factura() {
        return id_factura;
    }

    public void setId_factura(Integer id_factura) {
        this.id_factura = id_factura;
    }

    public BigDecimal getPrecio_total() {
        return precio_total;
    }

    public void setPrecio_total(BigDecimal precio_total) {
        this.precio_total = precio_total;
    }

    public BigDecimal getdescuento() {
        return descuento;
    }

    public void setTiene_descuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getFecha_hora_emision() {
        return fecha_hora_emision;
    }

    public void setFecha_hora_emision(String fecha_hora_emision) {
        this.fecha_hora_emision = fecha_hora_emision;
    }

    public String getEstado() {
        return estado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha_hora_pago() {
        return fecha_hora_pago;
    }

    public void setFecha_hora_pago(String fecha_hora_pago) {
        this.fecha_hora_pago = fecha_hora_pago;
    }

    public String getForma_pago() {
        return forma_pago;
    }

    public void setForma_pago(String forma_pago) {
        this.forma_pago = forma_pago;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "Facturas{" +
                "id_factura=" + id_factura +
                ", dni='" + dni + '\'' +
                ", precio_total=" + precio_total +
                ", descuento=" + descuento +
                ", fecha_hora_emision='" + fecha_hora_emision + '\'' +
                ", estado='" + estado + '\'' +
                ", fecha_pago='" + fecha_hora_pago + '\'' +
                '}';
    }

    /**
     * Función para calcular el total con descuento.
     */

    public BigDecimal calcularTotalConDescuento(BigDecimal subtotal, BigDecimal descuento) {
        if (subtotal == null || descuento == null) {
            throw new IllegalArgumentException("Subtotal and discount must not be null");
        }
        BigDecimal discountAmount = subtotal.multiply(descuento).divide(BigDecimal.valueOf(100));
        BigDecimal totalConDescuento = subtotal.subtract(discountAmount);
        return totalConDescuento.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
