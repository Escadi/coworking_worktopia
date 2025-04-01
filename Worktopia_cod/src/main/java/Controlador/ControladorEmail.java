/**
 * Clase ControladorEmail que se encarga de enviar correos electrónicos a los clientes
 * con la información de la reserva realizada.
 */
package Controlador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ControladorEmail {
    private static final String correo = "woktopiacoworking@gmail.com";
    private static final String contrasena = "zywzibuvkyhqzmvk";

    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final Session session;
    private static Transport transport;

    static {
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        propiedades.put("mail.smtp.charset", "utf-8");
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.ssl.protocols", "TLSv1.2");
        propiedades.put("mail.smtp.port", "587");

        session = Session.getInstance(propiedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(correo, contrasena);
            }
        });

        try {

            transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", correo, contrasena);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void enviarCorreo(String destinatario, String asunto, String mensajeHtml) {
        executor.submit(() -> {
            try {
                Message email = new MimeMessage(session);
                email.setFrom(new InternetAddress(correo));
                email.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
                email.setSubject(asunto);
                email.setContent(mensajeHtml, "text/html; charset=utf-8");

                synchronized (transport) {
                    transport.sendMessage(email, email.getAllRecipients());
                }

                System.out.println("Correo enviado correctamente");
            } catch (MessagingException e) {
                e.printStackTrace();
                System.out.println("Error al enviar el correo.");
            }
        });
    }

    public String cargarPlantilla(String ruta, String cliente, String fecha, String horaInicio, String horaFin, String espacio, String subtotal) {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error cargando la plantilla";
        }

        return contenido.toString()

                .replace("{{CLIENTE}}", cliente)
                .replace("{{FECHA}}", fecha)
                .replace("{{HORA_INICIO}}", horaInicio)
                .replace("{{HORA_FIN}}", horaFin)
                .replace("{{ESPACIO}}", espacio)
                .replace("{{SUBTOTAL}}", subtotal);
    }


}



