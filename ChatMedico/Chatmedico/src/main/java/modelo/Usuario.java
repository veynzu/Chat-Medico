package modelo;
public class Usuario {
    protected String nombre;
    protected String identificacion;
    protected String contrasenia;

    public Usuario(String nombre, String identificacion, String contrasenia) {
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.contrasenia = contrasenia;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Identificacion: " + identificacion;
    }
}


