package Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import modelo.Paciente;
import modelo.Registro;

public class ControlerVistaRegistro {

    @FXML
    private Button btn_back;

    @FXML
    private Button btn_registrarFinal;

    @FXML
    private TextField txt_fld_name;

    @FXML
    private TextField txt_fld_identificacionRegistro;

    @FXML
    private TextField txt_fld_passwordRegister;

    private final ControlerPrincipal controlerPrincipal = new ControlerPrincipal();
    private final Registro registro = new Registro();

    public void initialize() {
        btn_back.setOnAction(event -> manejarAtras());
        btn_registrarFinal.setOnAction(event -> manejarRegistro());
    }
    private void manejarAtras() {
        // Navegar hacia atrás al login y cerrar la ventana actual
        controlerPrincipal.cerrarVentana(btn_back);
        controlerPrincipal.navegar("Login.fxml", "Login");

    }


    private void manejarRegistro() {
        String nombre = txt_fld_name.getText();
        String identificacion = txt_fld_identificacionRegistro.getText();
        String contrasenia = txt_fld_passwordRegister.getText();

        if (nombre.isEmpty() || identificacion.isEmpty() || contrasenia.isEmpty()) {
            controlerPrincipal.mostrarAlerta("Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }
        // Aquí puedes implementar la lógica de registro, como agregar el nuevo usuario al registro general
        Paciente pacienteNuevo = new Paciente(nombre, identificacion, contrasenia);
        registro.registrarUsuario(pacienteNuevo);
        controlerPrincipal.mostrarAlerta("Usuario registrado: " + nombre, Alert.AlertType.INFORMATION);

        // Navegar de vuelta al login y cerrar la ventana actual
        controlerPrincipal.cerrarVentana(btn_registrarFinal);
        controlerPrincipal.navegar("Login.fxml", "Login");

    }
}
