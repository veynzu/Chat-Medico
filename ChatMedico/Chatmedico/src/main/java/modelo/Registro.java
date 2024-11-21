package modelo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Registro {
    private static final String ARCHIVO_XML = "C:\\Users\\nitro\\OneDrive\\Escritorio\\programacion3\\Chatmedico\\src\\main\\java\\utilidades\\usuarios.xml";
    private static Queue<Consulta> colaConsultas = new ConcurrentLinkedQueue<>();

    // Método para registrar un usuario (médico o paciente) en un archivo XML
    public void registrarUsuario(Usuario usuario) {
        try {
            // Cargar o crear el archivo XML
            File archivo = new File(ARCHIVO_XML);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            if (archivo.exists()) {
                doc = dBuilder.parse(archivo);
                // Verificar si el usuario ya existe
                NodeList usuarios = doc.getElementsByTagName("Usuario");
                for (int i = 0; i < usuarios.getLength(); i++) {
                    Element usuarioElement = (Element) usuarios.item(i);
                    String id = usuarioElement.getElementsByTagName("Identificacion").item(0).getTextContent();
                    if (id.equals(usuario.getIdentificacion())) {
                        System.out.println("Error: Ya existe un usuario registrado con la identificacion: " + usuario.getIdentificacion());
                        return; // Salir sin registrar el usuario
                    }
                }
            } else {
                doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("Usuarios");
                doc.appendChild(rootElement);
            }

            // Crear el elemento usuario
            Element usuarioElement = doc.createElement("Usuario");
            usuarioElement.setAttribute("tipo", usuario instanceof Paciente ? "Paciente" : "Medico");

            Element nombreElement = doc.createElement("Nombre");
            nombreElement.appendChild(doc.createTextNode(usuario.getNombre()));
            usuarioElement.appendChild(nombreElement);

            Element identificacionElement = doc.createElement("Identificacion");
            identificacionElement.appendChild(doc.createTextNode(usuario.getIdentificacion()));
            usuarioElement.appendChild(identificacionElement);

            Element contraseniaElement = doc.createElement("Contrasenia");
            contraseniaElement.appendChild(doc.createTextNode(usuario.getContrasenia()));
            usuarioElement.appendChild(contraseniaElement);

            // Añadir el usuario al documento XML
            doc.getDocumentElement().appendChild(usuarioElement);

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

            System.out.println("Usuario registrado: " + usuario.getNombre());
        } catch (ParserConfigurationException | TransformerException | IOException | org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
    }

    // Método para autenticar un usuario leyendo del archivo XML
    public Usuario autenticarUsuario(String identificacion, String contrasenia) {
        try {
            // Cargar el archivo XML
            File archivo = new File(ARCHIVO_XML);
            if (!archivo.exists()) {
                System.out.println("No se encontraron usuarios registrados.");
                return null;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            NodeList usuarios = doc.getElementsByTagName("Usuario");
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element usuarioElement = (Element) usuarios.item(i);
                String id = usuarioElement.getElementsByTagName("Identificacion").item(0).getTextContent();
                String pass = usuarioElement.getElementsByTagName("Contrasenia").item(0).getTextContent();

                if (id.equals(identificacion) && pass.equals(contrasenia)) {
                    String nombre = usuarioElement.getElementsByTagName("Nombre").item(0).getTextContent();
                    String tipo = usuarioElement.getAttribute("tipo");

                    if (tipo.equals("Medico")) {
                        return new Medico(nombre, identificacion, contrasenia, "EspecialidadDesconocida");
                    } else if (tipo.equals("Paciente")) {
                        return new Paciente(nombre, identificacion, contrasenia);
                    }
                }
            }

            System.out.println("Credenciales incorrectas.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para registrar una consulta en la cola
    public static void registrarConsulta(Consulta consulta) {
        colaConsultas.add(consulta);
        System.out.println("Consulta registrada. Actualmente hay " + colaConsultas.size() + " consultas en cola.");
    }

    // Método para obtener la siguiente consulta en la cola
    public static Consulta obtenerSiguienteConsulta() {
        return colaConsultas.poll(); // Retira y devuelve la siguiente consulta en la cola, o null si la cola está vacía
    }

    // Método para verificar si hay consultas pendientes
    public static boolean hayConsultasPendientes() {
        return !colaConsultas.isEmpty();
    }
}
