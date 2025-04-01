package Prueba_errores;

import Clases.Facturas;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class FacturasTest {

    @Test
    public void testConstructorYGetters() {
        // Datos de prueba
        Integer idFactura = 1;
        String dni = "12345678A";
        BigDecimal precioTotal = new BigDecimal("100.00");
        BigDecimal descuento = new BigDecimal("10.00");
        String fechaEmision = "2023-10-01 12:00:00";
        String estado = "Pendiente";
        String fechaPago = "2023-10-02 12:00:00";
        String formaPago = "Tarjeta";
        BigDecimal subtotal = new BigDecimal("90.00");

        Facturas factura = new Facturas(idFactura, dni, precioTotal, descuento, fechaEmision, estado, fechaPago, formaPago, subtotal);

        assertEquals(idFactura, factura.getId_factura());
        assertEquals(dni, factura.getDni());
        assertEquals(precioTotal, factura.getPrecio_total());
        assertEquals(descuento, factura.getDescuento());
        assertEquals(fechaEmision, factura.getFecha_hora_emision());
        assertEquals(estado, factura.getEstado());
        assertEquals(fechaPago, factura.getFecha_hora_pago());
        assertEquals(formaPago, factura.getForma_pago());
        assertEquals(subtotal, factura.getSubtotal());
    }

    @Test
    public void testSetters() {
        Facturas factura = new Facturas(1, "12345678A", new BigDecimal("100.00"), new BigDecimal("10.00"),
                "2023-10-01 12:00:00", "Pendiente", "2023-10-02 12:00:00", "Tarjeta", new BigDecimal("90.00"));

        factura.setId_factura(2);
        factura.setDni("87654321B");
        factura.setPrecio_total(new BigDecimal("200.00"));
        factura.setDescuento(new BigDecimal("20.00"));
        factura.setFecha_hora_emision("2023-10-03 12:00:00");
        factura.setEstado("Pagada");
        factura.setFecha_hora_pago("2023-10-04 12:00:00");
        factura.setForma_pago("Efectivo");
        factura.setSubtotal(new BigDecimal("180.00"));

        assertEquals(2, factura.getId_factura());
        assertEquals("87654321B", factura.getDni());
        assertEquals(new BigDecimal("200.00"), factura.getPrecio_total());
        assertEquals(new BigDecimal("20.00"), factura.getDescuento());
        assertEquals("2023-10-03 12:00:00", factura.getFecha_hora_emision());
        assertEquals("Pagada", factura.getEstado());
        assertEquals("2023-10-04 12:00:00", factura.getFecha_hora_pago());
        assertEquals("Efectivo", factura.getForma_pago());
        assertEquals(new BigDecimal("180.00"), factura.getSubtotal());
    }

    @Test
    public void testEqualsYHashCode() {
        Facturas factura1 = new Facturas(1, "12345678A", new BigDecimal("100.00"), new BigDecimal("10.00"),
                "2023-10-01 12:00:00", "Pendiente", "2023-10-02 12:00:00", "Tarjeta", new BigDecimal("90.00"));

        Facturas factura2 = new Facturas(1, "12345678A", new BigDecimal("100.00"), new BigDecimal("10.00"),
                "2023-10-01 12:00:00", "Pendiente", "2023-10-02 12:00:00", "Tarjeta", new BigDecimal("90.00"));

        assertEquals(factura1, factura2);
        assertEquals(factura1.hashCode(), factura2.hashCode());
    }

    @Test
    public void testNotEquals() {
        Facturas factura1 = new Facturas(1, "12345678A", new BigDecimal("100.00"), new BigDecimal("10.00"),
                "2023-10-01 12:00:00", "Pendiente", "2023-10-02 12:00:00", "Tarjeta", new BigDecimal("90.00"));

        Facturas factura2 = new Facturas(2, "87654321B", new BigDecimal("200.00"), new BigDecimal("20.00"),
                "2023-10-03 12:00:00", "Pagada", "2023-10-04 12:00:00", "Efectivo", new BigDecimal("180.00"));

        assertNotEquals(factura1, factura2);
        assertNotEquals(factura1.hashCode(), factura2.hashCode());
    }

    @Test
    public void testCalcularTotalConDescuento() {
        Facturas factura = new Facturas(1, "12345678A", new BigDecimal("100.00"), new BigDecimal("10.00"),
                "2023-10-01 12:00:00", "Pendiente", "2023-10-02 12:00:00", "Tarjeta", new BigDecimal("90.00"));

        BigDecimal subtotal = new BigDecimal("100.00");
        BigDecimal descuento = new BigDecimal("10.00");

        BigDecimal totalConDescuento = factura.calcularTotalConDescuento(subtotal, descuento);

        assertEquals(new BigDecimal("90.00"), totalConDescuento);
    }

    @Test
    public void testCalcularTotalConDescuentoConValoresNulos() {
        Facturas factura = new Facturas(1, "12345678A", new BigDecimal("100.00"), new BigDecimal("10.00"),
                "2023-10-01 12:00:00", "Pendiente", "2023-10-02 12:00:00", "Tarjeta", new BigDecimal("90.00"));

        assertThrows(IllegalArgumentException.class, () -> {
            factura.calcularTotalConDescuento(null, new BigDecimal("10.00"));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            factura.calcularTotalConDescuento(new BigDecimal("100.00"), null);
        });
    }

    @Test
    public void testToString() {
        Facturas factura = new Facturas(1, "12345678A", new BigDecimal("100.00"), new BigDecimal("10.00"),
                "2023-10-01 12:00:00", "Pendiente", "2023-10-02 12:00:00", "Tarjeta", new BigDecimal("90.00"));

        String expectedToString = "Facturas{id_factura=1, dni='12345678A', precio_total=100.00, descuento=10.00, " +
                "fecha_hora_emision='2023-10-01 12:00:00', estado='Pendiente', fecha_pago='2023-10-02 12:00:00'}";
        assertEquals(expectedToString, factura.toString());
    }
}
