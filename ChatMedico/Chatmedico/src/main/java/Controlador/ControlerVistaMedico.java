package Controlador;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.ClienteChatMedico;
import modelo.Paciente;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ControlerVistaMedico {

    @FXML
    private Button btn_CerrarSesion;

    @FXML
    private Button btn_TerminarChat;

    @FXML
    private Button btn_iniciarTrabajoChat;

    @FXML
    private TextArea txtArea_ChatPacienteMedico;

    @FXML
    private TextArea txtArea_historialClinico;

    @FXML
    private Text txt_BienvenidoNameMedico;

    @FXML
    private Text txt_PacientesEnCola;

    @FXML
    private TextField txt_fld_MensajeMedico;

    @FXML
    private Button btn_EnviarMensajeMedico;

    private final ControlerPrincipal controlerPrincipal = new ControlerPrincipal();
    private BlockingQueue<Paciente> pacientesEnCola;
    private ClienteChatMedico clienteChatMedico;

    private Paciente pacienteActual;

    public void initialize() {
        btn_CerrarSesion.setOnAction(event -> cerrarSesion());
        btn_iniciarTrabajoChat.setOnAction(event -> iniciarTrabajoChat());
        btn_TerminarChat.setOnAction(event -> terminarChat());
        btn_EnviarMensajeMedico.setOnAction(event -> enviarMensaje());

        // Iniciar ClienteChatMedico
        clienteChatMedico = new ClienteChatMedico();
    }

    public void setPacientesEnCola(BlockingQueue<Paciente> pacientesEnCola) {
        this.pacientesEnCola = pacientesEnCola;
        actualizarPacientesEnCola();
    }

    private void cerrarSesion() {
        // Implementación de cerrar sesión
        try {
        if (clienteChatMedico != null) {
            clienteChatMedico.cerrarConexion();
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

    private void iniciarTrabajoChat() {
        if (pacientesEnCola != null && !pacientesEnCola.isEmpty()) {
            try {
                pacienteActual = pacientesEnCola.take(); // Obtener al siguiente paciente de la cola
                mostrarInformacionPaciente(pacienteActual);
                actualizarPacientesEnCola();

                // Conectar el cliente del médico al servidor
                clienteChatMedico.conectarAlServidor("localhost", 5050);
                clienteChatMedico.iniciarEscuchaMensajes(this::mostrarMensajeRecibido);

            } catch (InterruptedException e) {
                e.printStackTrace();
                controlerPrincipal.mostrarAlerta("Error al obtener el siguiente paciente en la cola.", Alert.AlertType.ERROR);
            }
        } else {
            controlerPrincipal.mostrarAlerta("No hay pacientes en la cola.", Alert.AlertType.INFORMATION);
        }
    }

    private void terminarChat() {
        txtArea_ChatPacienteMedico.clear();
        txtArea_historialClinico.clear();
        txt_BienvenidoNameMedico.setText("No se está atendiendo a ningún paciente.");
        controlerPrincipal.mostrarAlerta("El chat con el paciente ha finalizado. Puede iniciar el siguiente.", Alert.AlertType.INFORMATION);

        // Cerrar la conexión del cliente del chat
        if (clienteChatMedico != null) {
            clienteChatMedico.cerrarConexion();
        }
    }

    private void enviarMensaje() {
        String mensaje = txt_fld_MensajeMedico.getText().trim();
        if (mensaje.isEmpty()) {
            controlerPrincipal.mostrarAlerta("El mensaje no puede estar vacío.", Alert.AlertType.WARNING);
            return;
        }

        if (clienteChatMedico != null) {
            clienteChatMedico.enviarMensaje("Medico: " + mensaje);
            txtArea_ChatPacienteMedico.appendText("Yo: " + mensaje + "\n");
            txt_fld_MensajeMedico.clear(); // Limpiar el campo de texto después de enviar el mensaje
        } else {
            controlerPrincipal.mostrarAlerta("No estás conectado a ningún chat.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarInformacionPaciente(Paciente paciente) {
        txt_BienvenidoNameMedico.setText("Atendiendo a: " + paciente.getNombre());
        txtArea_historialClinico.setText(paciente.getHistorialClinico());
        txtArea_ChatPacienteMedico.appendText("Chat con el paciente iniciado...\n");
    }

    private void actualizarPacientesEnCola() {
        Platform.runLater(() -> txt_PacientesEnCola.setText("Pacientes en cola: " + (pacientesEnCola != null ? pacientesEnCola.size() : 0)));
    }

    // Método para mostrar mensajes recibidos del paciente en la interfaz
    private void mostrarMensajeRecibido(String mensaje) {
        Platform.runLater(() -> txtArea_ChatPacienteMedico.appendText("Paciente: " + mensaje + "\n"));
    }
}
