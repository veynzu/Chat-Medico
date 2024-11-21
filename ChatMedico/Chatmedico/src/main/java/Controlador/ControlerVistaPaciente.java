package Controlador;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import modelo.ClienteChatPaciente;
import modelo.ColaDePacientes;
import modelo.Paciente;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;

public class ControlerVistaPaciente {

    @FXML
    private Button btn_CerrarSesion;

    @FXML
    private Button btn_enviarMensaje;

    @FXML
    private Button btn_iniciarChat;

    @FXML
    private TextArea txtArea_ChatPacienteMedico;

    @FXML
    private TextArea txtArea_historialClinico;

    @FXML
    private Text txt_BienvenidoName;

    @FXML
    private Text txt_PosicionCola;

    @FXML
    private TextField txt_field_MensajePaciente;

    private final ControlerPrincipal controlerPrincipal = new ControlerPrincipal();

    // Variable de instancia para almacenar el paciente actual
    private Paciente pacienteActual;

    // Cliente para el chat
    private ClienteChatPaciente clienteChat;

    @FXML
    public void initialize() {
        btn_CerrarSesion.setOnAction(this::manejarCerrarSesion);
        btn_iniciarChat.setOnAction(this::manejarIniciarChat);
        btn_enviarMensaje.setOnAction(this::manejarEnviarMensaje);

        // Activar el ajuste de línea en el área de historial clínico para evitar el desplazamiento horizontal
        txtArea_historialClinico.setWrapText(true);
        txtArea_ChatPacienteMedico.setWrapText(true);
    }

    // Metodo para manejar el evento de cerrar sesión.
    @FXML
    private void manejarCerrarSesion(ActionEvent event) {
        try {
            // Cerrar la conexión del chat antes de salir
            if (clienteChat != null) {
                clienteChat.cerrarConexion();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Login");
            stage.show();

            // Cerrar la ventana actual
            controlerPrincipal.cerrarVentana(btn_CerrarSesion);
        } catch (IOException e) {
            e.printStackTrace();
            controlerPrincipal.mostrarAlerta("Error al cargar la vista de inicio de sesión.", Alert.AlertType.ERROR);
        }
    }

    // Método para manejar el evento de iniciar el chat con el médico.
    @FXML
    private void manejarIniciarChat(ActionEvent event) {
        controlerPrincipal.mostrarAlerta("Iniciando chat con el médico...", Alert.AlertType.INFORMATION);

        // Iniciar la conexión con el servidor del chat
        clienteChat = new ClienteChatPaciente(pacienteActual.getNombre(), txtArea_ChatPacienteMedico);
        clienteChat.conectarAlServidor("localhost", 5050); // Conectar al servidor (localhost y puerto 5050)
    }

    // Método para manejar el evento de enviar un mensaje al médico.
    @FXML
    private void manejarEnviarMensaje(ActionEvent event) {
        String mensaje = txt_field_MensajePaciente.getText().trim();
        if (!mensaje.isEmpty() && clienteChat != null) {
            clienteChat.enviarMensaje(mensaje);
            txt_field_MensajePaciente.clear();
            txtArea_ChatPacienteMedico.appendText("Yo: " + mensaje + "\n");
        } else {
            controlerPrincipal.mostrarAlerta("El mensaje no puede estar vacío.", Alert.AlertType.WARNING);
        }
    }

    // Método para cargar el historial clínico del paciente desde un archivo XML.
    private void cargarHistorialClinico(String identificacionPaciente) {
        try {
            System.out.println(identificacionPaciente);
            // Especificar la ruta del archivo XML de historial clínico del paciente
            String rutaArchivo = "C:\\Users\\nitro\\OneDrive\\Escritorio\\programacion3\\Chatmedico\\src\\main\\java\\Historial\\" + identificacionPaciente + ".xml";
            File archivoXML = new File(rutaArchivo);

            if (!archivoXML.exists()) {
                txtArea_historialClinico.setText("No se encontró el historial clínico para el paciente con ID: " + identificacionPaciente);
                return;
            }

            // Cargar el archivo XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivoXML);
            doc.getDocumentElement().normalize();

            // Obtener los nodos de las consultas del historial
            NodeList listaConsultas = doc.getElementsByTagName("Consulta");

            StringBuilder historialTexto = new StringBuilder();

            // Iterar sobre cada consulta y agregar la información al historialTexto
            for (int i = 0; i < listaConsultas.getLength(); i++) {
                Node nodoConsulta = listaConsultas.item(i);

                if (nodoConsulta.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoConsulta = (Element) nodoConsulta;
                    String fecha = elementoConsulta.getElementsByTagName("Fecha").item(0).getTextContent();
                    String descripcion = elementoConsulta.getElementsByTagName("Descripcion").item(0).getTextContent();
                    String notasMedicas = elementoConsulta.getElementsByTagName("NotasMedicas").item(0).getTextContent();

                    historialTexto.append("Fecha: ").append(fecha).append(" - Descripción: ").append(descripcion).append("\nNotas Médicas: ").append(notasMedicas).append("\n\n");
                }
            }

            // Mostrar el historial clínico en el área de texto
            txtArea_historialClinico.setText(historialTexto.toString());

        } catch (Exception e) {
            e.printStackTrace();
            controlerPrincipal.mostrarAlerta("Error al cargar el historial clínico del paciente.", Alert.AlertType.ERROR);
        }
    }

    // Método para establecer el nombre del paciente en el mensaje de bienvenida y cargar su historial.
    public void establecerDatosPaciente(String nombre, String identificacion) {
        txt_BienvenidoName.setText("Bienvenido, " + nombre);
        cargarHistorialClinico(identificacion);

        // Crear un paciente y almacenarlo como paciente actual
        pacienteActual = new Paciente(nombre, identificacion, ""); // Asegúrate de obtener el paciente adecuado

        // Agregar el paciente actual a la cola
        ColaDePacientes.agregarPaciente(pacienteActual);

        // Actualizar la posición del paciente en la cola
        actualizarPosicionEnCola();
    }

    // Método para actualizar la posición del paciente en la cola y mostrarlo en la interfaz
    private void actualizarPosicionEnCola() {
        int posicion = ColaDePacientes.obtenerPosicionPaciente(pacienteActual);
        if (posicion != -1) {
            txt_PosicionCola.setText("Posición en la cola: " + posicion);
        } else {
            txt_PosicionCola.setText("No estás en la cola.");
        }
    }
}
