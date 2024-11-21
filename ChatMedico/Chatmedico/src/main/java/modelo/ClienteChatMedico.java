package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClienteChatMedico {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;

    // Metodo para conectar al servidor
    public void conectarAlServidor(String direccionServidor, int puerto) {
        try {
            socket = new Socket(direccionServidor, puerto);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Iniciar un hilo para escuchar mensajes del servidor (paciente)
            new Thread(this::escucharMensajes).start();

        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    // Metodo para enviar mensajes al servidor
    public void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    // Metodo para iniciar la escucha de mensajes, usando un callback para la interfaz
    public void iniciarEscuchaMensajes(Consumer<String> mensajeCallback) {
        new Thread(() -> {
            String mensaje;
            try {
                while ((mensaje = entrada.readLine()) != null) {
                    mensajeCallback.accept(mensaje);
                }
            } catch (IOException e) {
                System.out.println("Conexion cerrada: " + e.getMessage());
            } finally {
                cerrarConexion();
            }
        }).start();
    }

    // Metodo para escuchar mensajes del servidor (directamente desde hilo)
    private void escucharMensajes() {
        String mensaje;
        try {
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Paciente: " + mensaje);
            }
        } catch (IOException e) {
            System.out.println("Conexion cerrada: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
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
            System.out.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }
}
