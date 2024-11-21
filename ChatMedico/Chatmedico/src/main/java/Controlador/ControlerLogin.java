package Controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Paciente;
import modelo.Medico;
import modelo.Registro;
import modelo.Usuario;

import java.io.IOException;

public class ControlerLogin {

    @FXML
    private Button btn_connect;

    @FXML
    private Button btn_record;

    @FXML
    private TextField txt_fld_username;

    @FXML
    private TextField txt_fld_password;

    @FXML
    private AnchorPane login_pane;

    private Registro registro = new Registro();
    private ControlerPrincipal controlerPrincipal = new ControlerPrincipal();

    @FXML
    public void initialize() {
        btn_connect.setOnAction(event -> autenticarUsuario());
        btn_record.setOnAction(event -> registrarUsuario());
    }

    private void autenticarUsuario() {
        String usuario = txt_fld_username.getText();
        String contrasenia = txt_fld_password.getText();

        // Autenticar al usuario
        Usuario usuarioAutenticado = registro.autenticarUsuario(usuario, contrasenia);

        if (usuarioAutenticado != null) {
            if (usuarioAutenticado instanceof Paciente) {
                // Si es un paciente, redirigir a la vista del paciente
                Paciente pacienteAutenticado = (Paciente) usuarioAutenticado;
                controlerPrincipal.mostrarAlerta("Autenticación exitosa. Bienvenido, " + pacienteAutenticado.getNombre(), Alert.AlertType.INFORMATION);
                abrirVistaPaciente(pacienteAutenticado);
            } else if (usuarioAutenticado instanceof Medico) {
                // Si es un médico, redirigir a la vista del médico
                Medico medicoAutenticado = (Medico) usuarioAutenticado;
                controlerPrincipal.mostrarAlerta("Autenticación exitosa. Bienvenido Dr. " + medicoAutenticado.getNombre(), Alert.AlertType.INFORMATION);
                abrirVistaMedico(medicoAutenticado);
            } else {
                // Si es otro tipo de usuario no esperado
                controlerPrincipal.mostrarAlerta("Tipo de usuario desconocido. Acceso denegado.", Alert.AlertType.ERROR);
            }
        } else {
            // Credenciales incorrectas
            controlerPrincipal.mostrarAlerta("Credenciales incorrectas. Inténtelo de nuevo.", Alert.AlertType.ERROR);
        }
    }

    private void abrirVistaPaciente(Paciente paciente) {
        try {
            // Cargar la vista del paciente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/vistaPaciente.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista del paciente
            ControlerVistaPaciente controlerVistaPaciente = loader.getController();

            // Pasar el nombre y la identificación del paciente autenticado al controlador
            controlerVistaPaciente.establecerDatosPaciente(paciente.getNombre(), paciente.getIdentificacion());

            // Crear la escena y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Vista del Paciente");
            stage.show();

            // Cerrar la ventana de login
            controlerPrincipal.cerrarVentana(btn_connect);
        } catch (Exception e) {
            e.printStackTrace();
            controlerPrincipal.mostrarAlerta("Error al cargar la vista del paciente.", Alert.AlertType.ERROR);
        }
    }

    private void abrirVistaMedico(Medico medico) {
        try {
            // Cargar la vista del paciente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/vistaMedico.fxml"));
            Parent root = loader.load();

            // Crear la escena y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Vista del medico");
            stage.show();

            // Cerrar la ventana de login
            controlerPrincipal.cerrarVentana(btn_connect);
        } catch (Exception e) {
            e.printStackTrace();
            controlerPrincipal.mostrarAlerta("Error al cargar la vista del medico.", Alert.AlertType.ERROR);
        }
    }

    private void registrarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/vistaRegistro.fxml"));
            Parent root = loader.load();

            // Crear la nueva escena y el stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Registro de Usuario");
            stage.setResizable(false);
            stage.show();

            // Cerrar la ventana actual de login
            controlerPrincipal.cerrarVentana(btn_connect);
        } catch (IOException e) {
            e.printStackTrace();
            controlerPrincipal.mostrarAlerta("Error al cargar la vista de registro.", Alert.AlertType.ERROR);
        }
    }
}
