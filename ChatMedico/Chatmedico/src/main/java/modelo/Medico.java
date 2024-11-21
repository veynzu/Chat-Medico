package modelo;

import java.util.Scanner;

public class Medico extends Usuario {
    private String especialidad;

    public Medico(String nombre, String identificacion, String contrasenia, String especialidad) {
        super(nombre, identificacion, contrasenia);
        this.especialidad = especialidad;
    }

    // Getter y setter de la especialidad
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    // Menu para el médico
    public void mostrarMenuMedico(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- Menu Medico ---");
            System.out.println("1. Iniciar atencion de consultas");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    iniciarAtencionConsultas(scanner);
                    break;
                case 2:
                    System.out.println("Saliendo del menu medico...");
                    break;
                default:
                    System.out.println("Opcion no valida, intente de nuevo.");
            }
        } while (opcion != 2);
    }

    // Método para iniciar la atención de consultas
    public void iniciarAtencionConsultas(Scanner scanner) {
        while (Registro.hayConsultasPendientes()) {
            Consulta consulta = Registro.obtenerSiguienteConsulta();
            if (consulta != null) {
                Paciente paciente = consulta.getPaciente();
                System.out.println("Atendiendo consulta de " + paciente.getNombre() + " - Descripcion: " + consulta.getDescripcion());

                // Pedir al médico que ingrese notas médicas
                System.out.print("Ingrese notas medicas para el paciente: ");
                String notasMedicas = scanner.nextLine();

                // Guardar el historial de la consulta
                paciente.guardarHistorialConsulta(consulta.getDescripcion(), notasMedicas);

                System.out.println("Consulta finalizada con " + paciente.getNombre());
            }
        }
    }
}
