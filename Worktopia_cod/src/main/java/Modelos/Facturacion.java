/**
 * Clase Facturacion
 * Clase que se encarga de la lógica de la ventana de facturación.
 * Se encarga de mostrar las facturas, generar facturas en Excel y PDF, y de abonar facturas.
 */
package Modelos;

import Aplicaciones.MenuPrincipalApp;
import Clases.*;
import Controlador.ControladorFacturas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ConexionDB.ConectionDB;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;


public class Facturacion {
    private double xOffset = 0;
    private double yOffset = 0;
    private ControladorFacturas controladorFacturas = new ControladorFacturas();
    private Reservas reservas;
    @FXML
    private TextField TextDniCliente;
    @FXML
    private TextField TextSubtotal;
    @FXML
    private TextField TextDescuento;
    @FXML
    private TextField TextTotal;
    @FXML
    private TextField TextFechaFactura;
    @FXML
    private Button btnSalir;
    @FXML
    private Button ListaReservas;
    @FXML
    private Button ListaClientes;
    @FXML
    private Button BtnUsuarios;
    @FXML
    private TextField facturaBuscar;
    @FXML
    private VBox vboxFacturas;
    @FXML
    private ComboBox<String> comboFormaPago;

    ObservableList<String> formasPago = FXCollections.observableArrayList("Tarjeta", "Efectivo");

    private int idFactura;
    @FXML
    private TableView<Facturas> tablaFacturas;
    @FXML
    private TableColumn<Facturas, String> colNFactura;
    @FXML
    private TableColumn<Facturas, String> colFechaFactura;
    @FXML
    private TableColumn<Facturas, String> colTotal;
    @FXML
    private TableColumn<Facturas, String> colDni;
    @FXML
    private TableColumn<Facturas, String> colDescuento;
    @FXML
    private TableColumn<Facturas, String> colEstadoPago;
    @FXML
    private TableColumn<Facturas, String> colFechaPago;
    @FXML
    private TableColumn<Facturas, String> colFormaPago;
    @FXML
    private TableColumn<Facturas, String> colSubtotal;
    @FXML
    private ListView<String> listaReservas;


    @FXML
    public void generarFactura(ActionEvent event) {
        if (idFactura == 0) {
            showAlert("Error", "Debe seleccionar una factura.");
            return;
        }

        try {
            generarFacturaExcel();
            showAlert("Éxito", "Factura generada correctamente.");
        } catch (Exception e) {
            showAlert("Error", "Error al generar la factura: " + e.getMessage());
        }
    }

    /**
     * Método que se ejecuta al iniciar la ventana de facturación.
     * Se encarga de cargar las facturas en la tabla de facturas.
     * Además, se encarga de filtrar las facturas por su ID.
     * También se encarga de abrir la ventana de facturación al hacer doble clic en una factura.
     * Por último, se encarga de mover la ventana de facturación.
     */
    @FXML
    public void initialize() {
        llenarComboBoxInicio();
        btnSalir.setStyle(("-fx-background-color: transparent;"));
        Stage ventanaSecundaria = new Stage();
        ventanaSecundaria.getIcons().add(new Image(getClass().getResourceAsStream("/Imagenes/bannerTopiaC.png")));
        colNFactura.setCellValueFactory(new PropertyValueFactory<>("id_factura"));
        colFechaFactura.setCellValueFactory(new PropertyValueFactory<>("fecha_hora_emision"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("precio_total"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFechaPago.setCellValueFactory(new PropertyValueFactory<>("fecha_hora_pago"));
        colFormaPago.setCellValueFactory(new PropertyValueFactory<>("forma_pago"));

        ObservableList<Facturas> facturasList = FXCollections.observableArrayList();

        Connection conn = null;
        try {
            conn = ConectionDB.getConn();
            String queryFacturas = "SELECT * FROM Facturas";

            try (PreparedStatement psFacturas = conn.prepareStatement(queryFacturas);
                 ResultSet rsFacturas = psFacturas.executeQuery()) {

                while (rsFacturas.next()) {
                    int idFactura = rsFacturas.getInt("id_factura");
                    BigDecimal descuento = rsFacturas.getBigDecimal("descuento");

                    BigDecimal totalSubtotales = cargarReservasEnFactura(conn, idFactura);
                    BigDecimal totalConDescuento = totalSubtotales.subtract(totalSubtotales.multiply(descuento.divide(BigDecimal.valueOf(100))));
                    totalConDescuento = totalConDescuento.setScale(2, BigDecimal.ROUND_HALF_UP);

                    Facturas factura = new Facturas(
                            idFactura,
                            rsFacturas.getString("fecha_hora_emision"),
                            totalConDescuento,
                            rsFacturas.getString("dni"),
                            descuento,
                            totalSubtotales,
                            rsFacturas.getString("estado"),
                            rsFacturas.getString("fecha_hora_pago"),
                            rsFacturas.getString("forma_pago")
                    );
                    facturasList.add(factura);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "No se pudieron cargar las facturas: " + e.getMessage());
        } finally {

        }

        tablaFacturas.setItems(facturasList);
        inicioSesion();

        FilteredList<Facturas> filtroFacturas = new FilteredList<>(facturasList, p -> true);
        facturaBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroFacturas.setPredicate(Facturas -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                } else {
                    return Facturas.getId_factura().toString().contains(newValue.toLowerCase());
                }
            });
        });

        SortedList<Facturas> sortedData = new SortedList<>(filtroFacturas);
        sortedData.comparatorProperty().bind(tablaFacturas.comparatorProperty());
        tablaFacturas.setItems(sortedData);

        tablaFacturas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Facturas factura = tablaFacturas.getSelectionModel().getSelectedItem();
                if (factura != null) {
                    try {
                        exportarFacturas(factura);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        TextDescuento.textProperty().addListener((observableValue, s, t1) -> actualizarTotal());

        vboxFacturas.setOnMousePressed(event -> {
            xOffset = event.getSceneX() - vboxFacturas.getLayoutX() + 250;
            yOffset = event.getSceneY() - vboxFacturas.getLayoutY() + 70;
        });
        vboxFacturas.setOnMouseDragged(event -> {
            vboxFacturas.setLayoutX(event.getScreenX() - xOffset);
            vboxFacturas.setLayoutY(event.getScreenY() - yOffset);
        });

        tablaFacturas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Facturas factura = tablaFacturas.getSelectionModel().getSelectedItem();
                if (factura != null) {
                    idFactura = factura.getId_factura(); // Almacena la ID de la factura
                    try {
                        exportarFacturas(factura);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public Facturacion() {
    }

    public BigDecimal cargarReservasEnFactura(Connection conn, int idFactura) throws SQLException, ClassNotFoundException {
        List<Reservas> reservas = new ArrayList<>();
        String queryReservas = "SELECT * FROM Reservas WHERE id_factura = ?";
        BigDecimal totalSubtotales = BigDecimal.ZERO;

        try (PreparedStatement ps = conn.prepareStatement(queryReservas)) {
            ps.setInt(1, idFactura); // Establecemos el idFactura en el PreparedStatement

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idReserva = rs.getInt("id_reserva");
                    String dni = rs.getString("dni");
                    int idAsiento = rs.getInt("id_asiento");
                    Timestamp fechaInicio = rs.getTimestamp("fecha_hora_inicio");
                    Timestamp fechaFin = rs.getTimestamp("fecha_hora_fin");

                    BigDecimal subtotal = calcularSubtotal(idAsiento, fechaInicio, fechaFin);
                    totalSubtotales = totalSubtotales.add(subtotal);

                    Reservas reserva = new Reservas(
                            idReserva,
                            dni,
                            idAsiento,
                            idFactura,
                            fechaInicio,
                            fechaFin,
                            subtotal
                    );
                    reservas.add(reserva);
                }
            }
        }

        ObservableList<String> reservasList = FXCollections.observableArrayList();
        for (Reservas r : reservas) {
            reservasList.add("Nº Reserva: " + r.getId_reserva() + " Precio: " + r.getSubtotal());
        }

        listaReservas.setItems(reservasList);
        return totalSubtotales;
    }

    @FXML
    public void salirVbox() {
        System.out.println("Limpiando facturaBuscar");
        facturaBuscar.clear();
        vboxFacturas.setVisible(false);
    }


    public static void rellenarCombo(ComboBox<String> comboBox, ObservableList<String> forma) {
        comboBox.setItems(forma);
    }

    public void llenarComboBox(ActionEvent event) {
        rellenarCombo(comboFormaPago, formasPago);
    }

    public void llenarComboBoxInicio() {
        rellenarCombo(comboFormaPago, formasPago);
    }


    public void inicioSesion() {
        String categoria = SesionUsuario.getCategoriaUsuario();

        if (categoria != null && categoria.equals("Admin")) {
            BtnUsuarios.setVisible(true);
            BtnUsuarios.setDisable(false);
        } else {
            BtnUsuarios.setVisible(false);
        }
    }

    /**
     * Método que se encarga de exportar las facturas a la ventana de facturación.
     * Se encarga de mostrar la información de la factura seleccionada.
     *
     * @param facturas Factura seleccionada
     * @throws ClassNotFoundException Excepción de clase no encontrada
     */

    public void exportarFacturas(Facturas facturas) throws ClassNotFoundException {
        idFactura = facturas.getId_factura();
        TextFechaFactura.setText(facturas.getFecha_hora_emision());
        TextDniCliente.setText(facturas.getDni());
        TextDescuento.setText(facturas.getDescuento().toString());
        comboFormaPago.getSelectionModel().select(facturas.getForma_pago());

        try {
            BigDecimal totalSubtotales = cargarReservasEnFactura(idFactura);
            TextSubtotal.setText(totalSubtotales.toString());
            double descuento = Double.parseDouble(TextDescuento.getText());
            double subTotal = totalSubtotales.doubleValue();
            TextTotal.setText(String.valueOf(descuento(descuento, subTotal)));
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "No se pudieron cargar las reservas: " + e.getMessage());
        }

        vboxFacturas.setVisible(true);
    }

    public double descuento(double descuentoText, double subtotal) {
        if (descuentoText > 0) {
            return subtotal - (subtotal * descuentoText / 100);
        }
        return subtotal;
    }

    private void actualizarTotal() {
        try {
            double descuentoValor = validarNumero(TextDescuento.getText(), 0.00);
            double subtotal = validarNumero(TextSubtotal.getText(), 0.00);
            double total = descuento(descuentoValor, subtotal);

            TextTotal.setText(String.valueOf(total));
        } catch (Exception e) {
            TextTotal.setText("Error");
        }
    }

    private double validarNumero(String texto, double valorPorDefecto) {
        if (texto == null || texto.trim().isEmpty()) {
            return valorPorDefecto;
        }
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    /**
     * Método que se encarga cargar las reservas en la factura.
     *
     */

    public BigDecimal cargarReservasEnFactura(int idFactura) throws SQLException, ClassNotFoundException {
        List<Reservas> reservas = new ArrayList<>();
        String queryReservas = "SELECT * FROM Reservas WHERE id_factura = ?";
        BigDecimal totalSubtotales = BigDecimal.ZERO;

        Connection conn = ConectionDB.getConn();
        try (PreparedStatement ps = conn.prepareStatement(queryReservas)) {
            ps.setInt(1, idFactura);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idReserva = rs.getInt("id_reserva");
                    String dni = rs.getString("dni");
                    int idAsiento = rs.getInt("id_asiento");
                    Timestamp fechaInicio = rs.getTimestamp("fecha_hora_inicio");
                    Timestamp fechaFin = rs.getTimestamp("fecha_hora_fin");

                    BigDecimal subtotal = calcularSubtotal(idAsiento, fechaInicio, fechaFin);
                    totalSubtotales = totalSubtotales.add(subtotal);

                    Reservas reserva = new Reservas(
                            idReserva,
                            dni,
                            idAsiento,
                            idFactura,
                            fechaInicio,
                            fechaFin,
                            subtotal
                    );
                    reservas.add(reserva);
                }
            }
        }

        ObservableList<String> reservasList = FXCollections.observableArrayList();
        for (Reservas r : reservas) {
            reservasList.add("Nº Reserva: " + r.getId_reserva() + " Precio: " + r.getSubtotal());
        }

        listaReservas.setItems(reservasList);
        return totalSubtotales;
    }

    public void modificarFactura(ActionEvent event) {
        String forma = controladorFacturas.obtenerForma(idFactura).toLowerCase();
        if (forma.equals("pendiente")) {
            controladorFacturas.modificarFactura(TextFechaFactura, TextDescuento, TextTotal, TextSubtotal, idFactura);
        } else {
            showAlert("aviso", "La factura se encuentra pagada");
        }
        System.out.println("forma" + forma);
        initialize();
    }

    public void eliminarFactura(ActionEvent event) {
        String forma = controladorFacturas.obtenerForma(idFactura).toLowerCase();
        if (forma.equals("pendiente")) {
            controladorFacturas.eliminarFactura(idFactura);
        } else {
            showAlert("aviso", "La factura se encuentra pagada");
        }
        System.out.println("forma" + forma);
        initialize();


    }

    public void pagarFactura(ActionEvent event) {
        controladorFacturas.abonarFactura(TextFechaFactura, TextDescuento, TextTotal, TextSubtotal, comboFormaPago, idFactura);
        initialize();


    }

    /**
     *  Método que se encarga de generar la factura en Excel y PDF.
     *  Se encarga de obtener la información de la factura, cliente y reservas.
     *  Además, se encarga de calcular el subtotal de las reservas.
     *  Por último, se encarga de convertir el archivo Excel a PDF.
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */

    private void generarFacturaExcel() throws SQLException, IOException, ClassNotFoundException {
        String queryFactura = "SELECT * FROM Facturas WHERE id_factura = ?";
        String queryCliente = "SELECT * FROM Clientes WHERE dni = ?";
        String queryReservas = "SELECT * FROM Reservas WHERE id_factura = ?";

        Connection conn = ConectionDB.getConn(); // Obtén la conexión sin cerrarla
        try (PreparedStatement psFactura = conn.prepareStatement(queryFactura);
             PreparedStatement psCliente = conn.prepareStatement(queryCliente);
             PreparedStatement psReservas = conn.prepareStatement(queryReservas)) {

            psFactura.setInt(1, idFactura);
            try (ResultSet rsFactura = psFactura.executeQuery()) {
                if (!rsFactura.next()) {
                    throw new SQLException("Factura no encontrada.");
                }

                String dniCliente = rsFactura.getString("dni");
                LocalDateTime fechaEmision = rsFactura.getTimestamp("fecha_hora_emision").toLocalDateTime();
                BigDecimal totalFactura = rsFactura.getBigDecimal("precio_total");
                BigDecimal descuento = rsFactura.getBigDecimal("descuento");

                psCliente.setString(1, dniCliente);
                try (ResultSet rsCliente = psCliente.executeQuery()) {
                    if (!rsCliente.next()) {
                        throw new SQLException("Cliente no encontrado.");
                    }

                    String nombreCliente = rsCliente.getString("nombre");
                    String telefonoCliente = rsCliente.getString("telefono");
                    String emailCliente = rsCliente.getString("email");

                    psReservas.setInt(1, idFactura);
                    try (ResultSet rsReservas = psReservas.executeQuery()) {
                        List<String> reservas = new ArrayList<>();
                        List<BigDecimal> subtotales = new ArrayList<>();
                        BigDecimal totalSubtotales = BigDecimal.ZERO;

                        while (rsReservas.next()) {
                            int idReserva = rsReservas.getInt("id_reserva");
                            int idAsiento = rsReservas.getInt("id_asiento");
                            Timestamp inicio = rsReservas.getTimestamp("fecha_hora_inicio");
                            Timestamp fin = rsReservas.getTimestamp("fecha_hora_fin");

                            BigDecimal subtotal = calcularSubtotal(idAsiento, inicio, fin);
                            totalSubtotales = totalSubtotales.add(subtotal);

                            String reserva = String.format(
                                    "Reserva ID: %d Asiento ID: %d fecha y hora de inicio %s y fin %s",
                                    idReserva, idAsiento, inicio.toLocalDateTime(), fin.toLocalDateTime()
                            );
                            reservas.add(reserva);
                            subtotales.add(subtotal);
                        }

                        try (FileInputStream fis = new FileInputStream("src/main/resources/Docs/Modelo_factura_excel.xlsx");
                             Workbook workbook = new XSSFWorkbook(fis)) {

                            Sheet sheet = workbook.getSheetAt(0);

                            setCellValue(sheet, 9, 1, String.valueOf(idFactura));
                            setCellValue(sheet, 15, 1, dniCliente);
                            setCellValue(sheet, 16, 1, fechaEmision.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            setCellValue(sheet, 15, 5, nombreCliente);
                            setCellValue(sheet, 16, 5, telefonoCliente);
                            setCellValue(sheet, 14, 5, emailCliente);

                            int rowIndex = 17;
                            for (int i = 0; i < reservas.size(); i++) {
                                Row row = sheet.getRow(rowIndex);
                                if (row == null) {
                                    row = sheet.createRow(rowIndex);
                                }

                                Cell cell0 = row.getCell(0);
                                if (cell0 == null) {
                                    cell0 = row.createCell(0);
                                }
                                cell0.setCellValue(reservas.get(i));

                                Cell cell8 = row.getCell(8);
                                if (cell8 == null) {
                                    cell8 = row.createCell(8);
                                }
                                cell8.setCellValue(subtotales.get(i).doubleValue());

                                rowIndex++;
                            }

                            BigDecimal totalConDescuento = totalSubtotales.subtract(totalSubtotales.multiply(descuento.divide(BigDecimal.valueOf(100))));
                            BigDecimal baseImponible = totalConDescuento.divide(BigDecimal.valueOf(1.07), BigDecimal.ROUND_HALF_UP);
                            BigDecimal impuestos = baseImponible.multiply(BigDecimal.valueOf(0.07));

                            setCellValue(sheet, 45, 6, descuento.doubleValue() / 100);
                            BigDecimal valorDescuento = totalSubtotales.multiply(descuento.divide(BigDecimal.valueOf(100)));
                            setCellValue(sheet, 45, 7, valorDescuento.doubleValue());
                            setCellValue(sheet, 46, 7, totalConDescuento.doubleValue());
                            setCellValue(sheet, 43, 7, baseImponible.doubleValue());
                            setCellValue(sheet, 44, 7, impuestos.doubleValue());
                            setCellValue(sheet, 47, 7, fechaEmision.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                            String excelFilePath = "src/main/resources/Docs/Factura_" + idFactura + ".xlsx";
                            try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
                                workbook.write(fos);
                            }

                            convertirApdf(excelFilePath);
                        }
                    }
                }
            }
        }
    }

    private void convertirApdf(String archivoExcel) throws IOException {
        String libreOfficePath = "C:\\Program Files\\LibreOffice\\program\\soffice.exe";
        String comando = libreOfficePath + " --headless --convert-to pdf " + archivoExcel + " --outdir " + new File(archivoExcel).getParent();

        try {
            Process process = Runtime.getRuntime().exec(comando);
            process.waitFor();
            System.out.println("Archivo convertido exitosamente a PDF.");

            String pdfFilePath = archivoExcel.replace(".xlsx", ".pdf");

            File pdfFile = new File(pdfFilePath);
            if (pdfFile.exists()) {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Desktop.getDesktop().open(pdfFile);
                } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    Runtime.getRuntime().exec("open " + pdfFilePath);
                } else {
                    Runtime.getRuntime().exec("xdg-open " + pdfFilePath);
                }
            } else {
                System.out.println("El archivo PDF no se ha generado correctamente.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new IOException("Error al convertir el archivo a PDF.");
        }
    }


    public BigDecimal calcularSubtotal(int idAsiento, Timestamp fechaInicio, Timestamp fechaFin) {
        BigDecimal tarifaHora = getTarifaHoraId(idAsiento);
        LocalDateTime inicio = fechaInicio.toLocalDateTime();
        LocalDateTime fin = fechaFin.toLocalDateTime();
        long horas = Duration.between(inicio, fin).toHours();
        return tarifaHora.multiply(BigDecimal.valueOf(horas));
    }

    private static Map<Integer, BigDecimal> tarifaHoraCache = new HashMap<>();

    private BigDecimal getTarifaHoraId(int id_asiento) {
        if (!tarifaHoraCache.containsKey(id_asiento)) {
            List<Asientos> asientos = ConectionDB.getAsientos();
            for (Asientos asiento : asientos) {
                if (asiento.getId_asiento() == id_asiento) {
                    tarifaHoraCache.put(id_asiento, asiento.getTarifa_hora());
                    return asiento.getTarifa_hora();
                }
            }
        }
        return tarifaHoraCache.getOrDefault(id_asiento, BigDecimal.ZERO);
    }

    private void setCellValue(Sheet sheet, int rowIndex, int colIndex, Object value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void abrirVentana(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.setResizable(false);
            stage.setMaximized(false);
            MenuPrincipalApp.agregarIcono(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ventanaListaClientes(ActionEvent event) {
        abrirVentana("/Menus/ListaClientes.fxml", "Lista de Cliente");
        ((Stage) ListaClientes.getScene().getWindow()).close();
    }

    public void ventanaListaReservas(ActionEvent event) {
        abrirVentana("/Menus/ListaReservas.fxml", "Listas De Reservas");
        ((Stage) ListaReservas.getScene().getWindow()).close();
    }

    public void ventanaUsuarios(ActionEvent event) {
        abrirVentana("/Menus/ListaUsuarios.fxml", "Usuarios");
        ((Stage) BtnUsuarios.getScene().getWindow()).close();
    }

    public void salirVbox(ActionEvent event) {
        vboxFacturas.setVisible(false);
    }
}







