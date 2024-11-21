package App;

import java.util.Scanner;
import modelo.*;

public class Main {
    private static Registro registro = new Registro();

    public static void main(String[] args) {
        // Quemar los datos de un medico para pruebas
        Medico medicoPredefinido = new Medico("Dr. Juan Perez", "medico123", "contrasenia123", "Cardiologia");
        registro.registrarUsuario(medicoPredefinido);

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- Menu ChatMedico ---");
            System.out.println("1. Registrar Paciente");
            System.out.println("2. Autenticar Usuario");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir nueva línea

            switch (opcion) {
                case 1:
                    registrarPaciente(scanner);
                    break;
                case 2:
                    autenticarUsuario(scanner);
                    break;
                case 3:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion no valida, intente de nuevo.");
            }
        } while (opcion != 3);

        scanner.close();
    }

    private static void registrarPaciente(Scanner scanner) {
        System.out.print("Ingrese el nombre del paciente: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese la identificacion del paciente: ");
        String identificacion = scanner.nextLine();
        System.out.print("Ingrese la contrasenia del paciente: ");
        String contrasenia = scanner.nextLine();

        Paciente paciente = new Paciente(nombre, identificacion, contrasenia);
        registro.registrarUsuario(paciente);
    }

    private static void autenticarUsuario(Scanner scanner) {
        System.out.print("Ingrese la identificacion del usuario: ");
        String identificacion = scanner.nextLine();
        System.out.print("Ingrese la contrasenia del usuario: ");
        String contrasenia = scanner.nextLine();

        Usuario usuario = registro.autenticarUsuario(identificacion, contrasenia);
        if (usuario != null) {
            System.out.println("Autenticacion exitosa. Bienvenido, " + usuario.getNombre());
            if (usuario instanceof Medico) {
                System.out.println("Rol: Medico");
                ((Medico) usuario).mostrarMenuMedico(scanner);
            } else if (usuario instanceof Paciente) {
                System.out.println("Rol: Paciente");
                ((Paciente) usuario).mostrarMenuPaciente(scanner);
            }
        } else {
            System.out.println("Credenciales incorrectas. Intente de nuevo.");
        }
    }
}
