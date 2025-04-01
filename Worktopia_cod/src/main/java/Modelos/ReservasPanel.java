/**
 * Clase ReservasPanel que se encarga de gestionar las reservas de los clientes
 * y de enviar un correo de confirmación de la reserva.
 * Implementa la interfaz Initializable.
 */
package Modelos;

import Clases.Reservas;
import Controlador.ControladorEmail;
import Controlador.ControladorReservas;
import Manejadores_Reservas_Facturas.ReservaDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.Set;

public class ReservasPanel {
    private ControladorReservas controladorReservas = new ControladorReservas();
    private ControladorEmail controladorEmail = new ControladorEmail();
    private ListaClientes listaClientes = new ListaClientes();
    @FXML
    private Button btnSalir;
    @FXML
    private TextField espacio;
    @FXML
    private TextField horaInicio;
    @FXML
    private TextField horaFin;
    @FXML
    private TextField precio;
    @FXML
    private DatePicker fecha;
    @FXML
    private VBox contenedorHorarios;
    @FXML
    private TextField dniCliente;
    @FXML
    private StackPane imagenLayout;

    private Button BtnSeleccionado;
    private double precioTotal;
    private BigDecimal subtotal;

    @FXML
    public void initialize() {
        Stage ventanaSecundaria = new Stage();
        ventanaSecundaria.getIcons().add(new Image(getClass().getResourceAsStream("/Imagenes/bannerTopiaC.png")));
        dniCliente.textProperty().addListener((observableValue, s, t1) -> {
            controladorReservas.buscarDni(dniCliente.getText(), dniCliente);
        });
        dniCliente.textProperty().bindBidirectional(listaClientes.getSelectSeleccionDni());

    }

    public ReservasPanel() {
    }

    /**
     * Inserta una reserva en la base de datos y envía un correo de confirmación al cliente.
     */
    public void insertarReserva(ActionEvent event) {

        String emailCliente = controladorReservas.obtenerEmailCliente(dniCliente.getText());
        String nombreCliente = controladorReservas.obtenerNombreCliente(dniCliente.getText());
        //Formateo de la fecha a fecha europea
        LocalDate fechas = fecha.getValue();
        DateTimeFormatter formateo = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = fechas.format(formateo);

        if (emailCliente != null && !emailCliente.isEmpty()) {
            String htmlPlantilla = "src/main/resources/html/reservasHTML.html";
            String mensaje = controladorEmail.cargarPlantilla(
                    htmlPlantilla,
                    nombreCliente,
                    fechaFormateada,
                    horaInicio.getText().toString(),
                    horaFin.getText().toString(),
                    espacio.getText().toString(),
                    subtotal.toString()
            );
            controladorEmail.enviarCorreo(emailCliente, "Confirmación de reserva", mensaje);
        }
        insertarDatos();

    }

    /**
     * Muestra los datos de la mesa seleccionada en los campos de texto.
     */

    public void datosTextFieldMesa(ActionEvent event) {
        Button BtnSeleccionHora = (Button) event.getSource();

        if (BtnSeleccionHora != null) {
            espacio.setText(BtnSeleccionado.getText());
            if (horaInicio.getText().isEmpty()) {
                horaInicio.setText(BtnSeleccionHora.getText());
            } else if (horaFin.getText().isEmpty()) {
                horaFin.setText(BtnSeleccionHora.getText());
                cierreVentanaHorarios();
            }
        }
        precioTotal = controladorReservas.facturaPrecioText(espacio, horaInicio, horaFin);
        precio.setText(String.format("%.2f €", precioTotal));
        subtotal = BigDecimal.valueOf(precioTotal);


    }

    /**
     * Muestra la ventana de horarios al hacer clic en un botón de espacio.
     */

    public void ventanaHorarios(MouseEvent event) {
        Button BtnSeleccion = (Button) event.getSource();

        if (contenedorHorarios.isVisible() && BtnSeleccionado == BtnSeleccion) {
            cierreVentanaHorarios();
            return;
        }

        if (BtnSeleccionado != null && BtnSeleccionado != BtnSeleccion) {
            cierreVentanaHorarios();
        }

        BtnSeleccionado = BtnSeleccion;
        limpiarCamposHorarios();
        contenedorHorarios.setVisible(true);

        int espacioId = controladorReservas.obtenerIdEspacio(BtnSeleccionado.getText());
        actualizarColoresHorarios(espacioId);

        posicionarVentanaEnRaton(event);
    }

    private void limpiarCamposHorarios() {
        horaInicio.setText("");
        horaFin.setText("");
        precio.setText("");
    }

    /**
     * Posiciona la ventana de horarios en la posición del ratón.
     */
    private void posicionarVentanaEnRaton(MouseEvent event) {

        // Obtener coordenadas del ratón relativas al layout donde está la imagen
        double x = event.getSceneX() - imagenLayout.localToScene(0, 0).getX();
        double y = event.getSceneY() - imagenLayout.localToScene(0, 0).getY();

        // Asegurar que el contenedorHorarios no salga de los límites de la imagen
        double maxX = 833 - contenedorHorarios.getWidth();
        double maxY = 604 - contenedorHorarios.getHeight();

        // Limitar coordenadas para que no se salgan del área de la imagen
        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));

        // Ajustar la posición dentro del layout que contiene la imagen
        contenedorHorarios.setLayoutX(62 + x);
        contenedorHorarios.setLayoutY(146 + y);

    }

    public void cierreVentanaHorarios() {
        contenedorHorarios.setVisible(false);
        BtnSeleccionado = null;
    }

    /**
     * Actualiza los colores de los botones de horarios según si están ocupados o no.
     *
     * @param idEspacio ID del espacio seleccionado.
     */
    public void actualizarColoresHorarios(int idEspacio) {
        LocalDate fechaSeleccionada = fecha.getValue();
        if (fechaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar una fecha.");
            alert.showAndWait();
            contenedorHorarios.setVisible(false);
            return;
        }

        Set<String> horariosOcupados = controladorReservas.obtenerHorariosReservados(idEspacio, fechaSeleccionada);

        for (javafx.scene.Node node : contenedorHorarios.lookupAll(".button")) {
            if (node instanceof Button) {
                Button btnHorario = (Button) node;
                String horario = btnHorario.getText();

                if (horariosOcupados.contains(horario)) {
                    btnHorario.setStyle("-fx-background-color: #e60415; -fx-text-fill: white;");
                    btnHorario.setDisable(true);
                } else {
                    btnHorario.setStyle("-fx-background-color: #047f07; -fx-text-fill: black;");
                    btnHorario.setDisable(false);
                }
            }
        }
    }

    /**
     * Inserta los datos de la reserva en la base de datos.
     * Muestra un mensaje de error si no se han completado todos los campos.
     */
    public void insertarDatos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        String dniText = dniCliente.getText();
        String espacioText = espacio.getText();
        String horaInicioText = horaInicio.getText();
        String horaFinText = horaFin.getText();
        LocalDate fechaReserva = fecha.getValue();
        BigDecimal subTotal = subtotal;
        int idAsiento = controladorReservas.obtenerIdEspacio(espacioText);

        if (dniText.isEmpty() || espacioText.isEmpty() || horaInicioText.isEmpty() || horaFinText.isEmpty()) {
            alert.setContentText("Debe completar todos los campos.");
            alert.show();
            return;
        }

        LocalDateTime fechaHoraInicio = LocalDateTime.of(fechaReserva, LocalTime.parse(horaInicioText));
        LocalDateTime fechaHoraFin = LocalDateTime.of(fechaReserva, LocalTime.parse(horaFinText));

        try {
            int idFactura = ReservaDAO.obtenerFacturaPendiente(dniText);
            if (idFactura == -1) {
                idFactura = ReservaDAO.crearFactura(dniText, subTotal);
            }

            Reservas reserva = new Reservas(0, dniText, idAsiento, idFactura, Timestamp.valueOf(fechaHoraInicio), Timestamp.valueOf(fechaHoraFin), subTotal);
            int idReserva = ReservaDAO.insertarReserva(reserva);

            alert.setContentText("Reserva y factura creadas correctamente. ID Reserva: " + idReserva);
            alert.show();

        } catch (SQLException e) {
            alert.setContentText("Error en la base de datos: " + e.getMessage());
            alert.show();
        }
    }


    public void salir(ActionEvent event) {
        ((Stage) btnSalir.getScene().getWindow()).close();
    }

}
