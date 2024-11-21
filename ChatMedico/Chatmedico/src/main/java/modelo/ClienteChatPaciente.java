package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ClienteChatPaciente {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private String nombrePaciente;
    private TextArea txtAreaChat;

    public ClienteChatPaciente(String nombrePaciente, TextArea txtAreaChat) {
        this.nombrePaciente = nombrePaciente;
        this.txtAreaChat = txtAreaChat;
    }

    // Metodo para conectar al servidor
    public void conectarAlServidor(String direccionServidor, int puerto) {
        try {
            socket = new Socket(direccionServidor, puerto);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar el nombre del paciente al servidor
            salida.println(nombrePaciente);

            // Iniciar un hilo para escuchar mensajes del servidor (medico)
            new Thread(this::escucharMensajes).start();

        } catch (IOException e) {
            actualizarChat("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    // Metodo para enviar mensajes al servidor
    public void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
            actualizarChat("Tú: " + mensaje);
        }
    }

    // Metodo para escuchar mensajes del servidor
    private void escucharMensajes() {
        String mensaje;
        try {
            while ((mensaje = entrada.readLine()) != null) {
                String mensajeFinal = mensaje;
                Platform.runLater(() -> actualizarChat("Medico: " + mensajeFinal));
            }
        } catch (IOException e) {
            actualizarChat("Conexion cerrada: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    // Metodo para actualizar el area de chat
    private void actualizarChat(String mensaje) {
        Platform.runLater(() -> txtAreaChat.appendText(mensaje + "\n"));
    }

    // Metodo para cerrar la conexion
    public void cerrarConexion() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (entrada != null) {
                entrada.close();
            }
            if (salida != null) {
                salida.close();
            }
        } catch (IOException e) {
            actualizarChat("Error al cerrar la conexion: " + e.getMessage());
        }
    }
}
