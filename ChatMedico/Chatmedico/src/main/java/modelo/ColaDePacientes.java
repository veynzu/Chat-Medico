package modelo;
import java.util.LinkedList;
import java.util.Queue;

public class ColaDePacientes {
    private static Queue<Paciente> colaDePacientes = new LinkedList<>();

    // Agregar paciente a la cola
    public static void agregarPaciente(Paciente paciente) {
        colaDePacientes.add(paciente);
    }

    // Remover el primer paciente de la cola (cuando el paciente es atendido y termina)
    public static void removerPaciente() {
        colaDePacientes.poll(); // Remueve el primer paciente en la cola (el que está siendo atendido)
    }

    // Obtener la posición del paciente en la cola
    public static int obtenerPosicionPaciente(Paciente paciente) {
        int posicion = 1;
        for (Paciente p : colaDePacientes) {
            if (p.equals(paciente)) {
                return posicion;
            }
            posicion++;
        }
        return -1; // Si el paciente no está en la cola
    }

    // Obtener la cantidad de pacientes en cola
    public static int getCantidadPacientesEnCola() {
        return colaDePacientes.size();
    }

    // Obtener el siguiente paciente a atender
    public static Paciente obtenerSiguientePaciente() {
        return colaDePacientes.peek(); // Devuelve el primer paciente sin removerlo
    }
}

