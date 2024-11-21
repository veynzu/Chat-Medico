package modelo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Paciente extends Usuario {
    private String historialMedico;

    public Paciente(String nombre, String identificacion, String contrasenia) {
        super(nombre, identificacion, contrasenia);
        this.historialMedico = "";
    }

    // Getter y setter del historial medico
    public String getHistorialMedico() {
        return historialMedico;
    }

    public void setHistorialMedico(String historialMedico) {
        this.historialMedico = historialMedico;
    }

    // Menu para el paciente
    public void mostrarMenuPaciente(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- Menu Paciente ---");
            System.out.println("1. Solicitar consulta");
            System.out.println("2. Ver historial de consultas");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    solicitarConsulta(scanner);
                    break;
                case 2:
                    verHistorialConsultas();
                    break;
                case 3:
                    System.out.println("Saliendo del menu paciente...");
                    break;
                default:
                    System.out.println("Opcion no valida, intente de nuevo.");
            }
        } while (opcion != 3);
    }

    // Solicitar una consulta
    public void solicitarConsulta(Scanner scanner) {
        System.out.print("Ingrese una descripcion del problema: ");
        String descripcion = scanner.nextLine();
        Consulta consulta = new Consulta(this, descripcion);
        Registro.registrarConsulta(consulta);
        System.out.println("Consulta solicitada correctamente y agregada a la cola.");
    }

    // Guardar el historial médico de la consulta
    public void guardarHistorialConsulta(String descripcion, String notasMedicas) {
        try {
            // Definir la ruta del archivo XML
            String rutaArchivo = "C:\\Users\\nitro\\OneDrive\\Escritorio\\programacion3\\Chatmedico\\src\\main\\java\\Historial\\" + getIdentificacion() + ".xml";
            File archivo = new File(rutaArchivo);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            // Si el archivo ya existe, cargar el documento
            if (archivo.exists()) {
                doc = dBuilder.parse(archivo);
                doc.getDocumentElement().normalize();
            } else {
                // Si no existe, crear un nuevo documento
                doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("HistorialMedico");
                doc.appendChild(rootElement);
            }

            // Crear un nuevo elemento Consulta
            Element consultaElement = doc.createElement("Consulta");

            // Agregar la fecha de la consulta
            Element fechaElement = doc.createElement("Fecha");
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            fechaElement.appendChild(doc.createTextNode(fechaActual));
            consultaElement.appendChild(fechaElement);

            // Agregar la descripción del problema
            Element descripcionElement = doc.createElement("Descripcion");
            descripcionElement.appendChild(doc.createTextNode(descripcion));
            consultaElement.appendChild(descripcionElement);

            // Agregar las notas médicas
            Element notasElement = doc.createElement("NotasMedicas");
            notasElement.appendChild(doc.createTextNode(notasMedicas));
            consultaElement.appendChild(notasElement);

            // Añadir la nueva consulta al documento XML
            doc.getDocumentElement().appendChild(consultaElement);

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

            System.out.println("Historial médico actualizado para el paciente: " + getNombre());

        } catch (ParserConfigurationException | TransformerException | IOException | org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
    }

    // Ver historial de consultas
    public void verHistorialConsultas() {
        try {
            // Definir la ruta del archivo XML
            String rutaArchivo = "C:\\Users\\nitro\\OneDrive\\Escritorio\\programacion3\\Chatmedico\\src\\main\\java\\Historial\\" + getIdentificacion() + ".xml";
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                System.out.println("No hay historial de consultas disponible para el paciente: " + getNombre());
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();

            NodeList consultas = doc.getElementsByTagName("Consulta");
            System.out.println("\n--- Historial de Consultas de " + getNombre() + " ---");
            for (int i = 0; i < consultas.getLength(); i++) {
                Element consultaElement = (Element) consultas.item(i);
                String fecha = consultaElement.getElementsByTagName("Fecha").item(0).getTextContent();
                String descripcion = consultaElement.getElementsByTagName("Descripcion").item(0).getTextContent();

                System.out.println("Fecha: " + fecha);
                System.out.println("Descripcion: " + descripcion);
                System.out.println("------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para obtener el historial clínico como un String para mostrarlo en la interfaz del médico
    public String getHistorialClinico() {
        StringBuilder historialTexto = new StringBuilder();
        try {
            // Definir la ruta del archivo XML
            String rutaArchivo = "C:\\Users\\nitro\\OneDrive\\Escritorio\\programacion3\\Chatmedico\\src\\main\\java\\Historial\\" + getIdentificacion() + ".xml";
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                return "No se encontró el historial clínico para el paciente con ID: " + getIdentificacion();
            }

            // Cargar el archivo XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();

            // Obtener los nodos de las consultas del historial
            NodeList listaConsultas = doc.getElementsByTagName("Consulta");

            // Iterar sobre cada consulta y agregar la información al historialTexto
            for (int i = 0; i < listaConsultas.getLength(); i++) {
                Node nodoConsulta = listaConsultas.item(i);

                if (nodoConsulta.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoConsulta = (Element) nodoConsulta;
                    String fecha = elementoConsulta.getElementsByTagName("Fecha").item(0).getTextContent();
                    String descripcion = elementoConsulta.getElementsByTagName("Descripcion").item(0).getTextContent();
                    String notasMedicas = elementoConsulta.getElementsByTagName("NotasMedicas").item(0).getTextContent();

                    historialTexto.append("Fecha: ").append(fecha).append(" - Descripción: ").append(descripcion)
                            .append("\nNotas Médicas: ").append(notasMedicas).append("\n\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al cargar el historial clínico.";
        }

        return historialTexto.toString();
    }
}
