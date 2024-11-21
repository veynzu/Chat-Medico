module chatmedico.chatmedico {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml; // Si estás usando XML para persistencia

    opens Controlador to javafx.fxml;
    exports App; // Asegúrate de que esto esté presente si tu clase principal está en el paquete App
    exports Controlador;
}
