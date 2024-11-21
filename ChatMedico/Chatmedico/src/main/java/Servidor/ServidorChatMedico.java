package Servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorChatMedico {
    private static final int PUERTO = 5050;
    private static final ConcurrentLinkedQueue<Socket> colaPacientes = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto: " + PUERTO);

            while (true) {
                Socket socket = servidor.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress());
                manejarConexion(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manejarConexion(Socket socket) {
        // Manejar la conexion, dependiendo si es medico o paciente
        new Thread(() -> {
            try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

                String tipoUsuario = entrada.readLine();
                if ("PACIENTE".equalsIgnoreCase(tipoUsuario)) {
                    colaPacientes.add(socket);
                    salida.println("Estas en la cola de espera...");
                    System.out.println("Paciente agregado a la cola: " + socket.getInetAddress());
                } else if ("MEDICO".equalsIgnoreCase(tipoUsuario)) {
                    atenderPaciente(socket, entrada, salida);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void atenderPaciente(Socket medicoSocket, BufferedReader entradaMedico, PrintWriter salidaMedico) {
        while (true) {
            Socket pacienteSocket = colaPacientes.poll();
            if (pacienteSocket != null) {
                try (BufferedReader entradaPaciente = new BufferedReader(new InputStreamReader(pacienteSocket.getInputStream()));
                     PrintWriter salidaPaciente = new PrintWriter(pacienteSocket.getOutputStream(), true)) {

                    salidaMedico.println("Conectado con paciente: " + pacienteSocket.getInetAddress());
                    salidaPaciente.println("Conectado con el medico.");

                    manejarChat(entradaMedico, salidaMedico, entradaPaciente, salidaPaciente);

                    pacienteSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                salidaMedico.println("No hay pacientes en la cola. Esperando...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void manejarChat(BufferedReader entradaMedico, PrintWriter salidaMedico, BufferedReader entradaPaciente, PrintWriter salidaPaciente) throws IOException {
        boolean chatActivo = true;

        while (chatActivo) {
            if (entradaPaciente.ready()) {
                String mensajePaciente = entradaPaciente.readLine();

                if ("SALIR".equalsIgnoreCase(mensajePaciente)) {
                    salidaMedico.println("El paciente ha terminado la sesion.");
                    chatActivo = false;
                } else {
                    salidaMedico.println("Paciente: " + mensajePaciente);
                }
            }

            if (entradaMedico.ready()) {
                String mensajeMedico = entradaMedico.readLine();
                if ("SALIR".equalsIgnoreCase(mensajeMedico)) {
                    salidaPaciente.println("El medico ha terminado la sesion.");
                    chatActivo = false;
                } else {
                    salidaPaciente.println("Medico: " + mensajeMedico);
                }
            }
        }
    }
}
