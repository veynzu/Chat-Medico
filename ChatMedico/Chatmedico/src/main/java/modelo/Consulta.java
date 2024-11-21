package modelo;

public class Consulta {
    private Paciente paciente;
    private String descripcion;
    private String estado;  // Estado de la consulta: "Pendiente", "En Progreso", "Finalizada"

    public Consulta(Paciente paciente, String descripcion) {
        this.paciente = paciente;
        this.descripcion = descripcion;
        this.estado = "Pendiente";
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Consulta de " + paciente.getNombre() + " - Descripcion: " + descripcion + " - Estado: " + estado;
    }
}

