package es.uco.pw.data.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import es.uco.pw.business.jugador.JugadorDTO;

/**
 * Clase que gestiona los jugadores registrados en el sistema. Permite operaciones como
 * alta, baja, modificación, y almacenamiento de jugadores en un archivo CSV.
 * Implementa el patrón Singleton para garantizar una única instancia del gestor.
 */
public class JugadoresDAO {
    
    private List<JugadorDTO> listaJugadores;
    private String ficheroJugadoresPath;
    private static JugadoresDAO instancia; // Singleton

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private JugadoresDAO() {
        listaJugadores = new ArrayList<>();
        cargarRutaFicheros();
    }

    /**
     * Obtiene la única instancia de GestorJugadores.
     *
     * @return La instancia única del gestor de jugadores.
     */
    public static synchronized JugadoresDAO getInstance() {
        if (instancia == null) {
            instancia = new JugadoresDAO();
        }
        return instancia;
    }

    /**
     * Da de alta a un nuevo jugador en el sistema. Si el correo ya existe, reactiva la cuenta y actualiza los datos.
     *
     * @param nuevoJugador El nuevo jugador a registrar.
     * @return Mensaje indicando el resultado de la operación.
     */
    public String altaJugador(JugadorDTO nuevoJugador) {
        for (JugadorDTO jugadorDTO : listaJugadores) {
            if (jugadorDTO.getCorreoElectronico().equalsIgnoreCase(nuevoJugador.getCorreoElectronico())) {
                if (jugadorDTO.isCuentaActiva()) {
                    return "Error: el correo ya está registrado y en uso.";
                } else {
                    jugadorDTO.setCuentaActiva(true);
                    jugadorDTO.setNombreApellidos(nuevoJugador.getNombreApellidos());
                    jugadorDTO.setFechaNacimiento(nuevoJugador.getFechaNacimiento());
                    jugadorDTO.setFechaInscripcion(new Date());
                    return "Cuenta reactivada y datos actualizados con éxito.";
                }
            }
        }
        nuevoJugador.setIdJugador(listaJugadores.size() + 1001);
        nuevoJugador.setCuentaActiva(true);
        nuevoJugador.setFechaInscripcion(new Date());
        listaJugadores.add(nuevoJugador);
        return "Jugador registrado con éxito.";
    }

    /**
     * Da de baja a un jugador desactivando su cuenta.
     *
     * @param correoElectronico El correo del jugador a dar de baja.
     * @return Mensaje indicando el resultado de la operación.
     */
    public String bajaJugador(String correoElectronico) {
        for (JugadorDTO jugadorDTO : listaJugadores) {
            if (jugadorDTO.getCorreoElectronico().equalsIgnoreCase(correoElectronico)) {
                if (!jugadorDTO.isCuentaActiva()) {
                    return "Error: El jugador ya está dado de baja.";
                }
                jugadorDTO.setCuentaActiva(false);
                return "Jugador dado de baja correctamente.";
            }
        }
        return "Error: No se encontró el jugador.";
    }

    /**
     * Modifica los datos de un jugador en el sistema.
     *
     * @param correoElectronico     El correo del jugador a modificar.
     * @param nuevoNombre           Nuevo nombre del jugador.
     * @param nuevaFechaNacimiento  Nueva fecha de nacimiento del jugador.
     * @param nuevoCorreo           Nuevo correo del jugador.
     * @return Mensaje indicando el resultado de la operación.
     */
    public String modificarJugador(String correoElectronico, String nuevoNombre, Date nuevaFechaNacimiento, String nuevoCorreo) {
        for (JugadorDTO jugadorDTO : listaJugadores) {
            if (jugadorDTO.getCorreoElectronico().equalsIgnoreCase(correoElectronico)) {
                if (!jugadorDTO.isCuentaActiva()) {
                    return "Error: La cuenta del jugador no está activa.";
                }
                for (JugadorDTO usuario : listaJugadores) {
                    if (!usuario.getCorreoElectronico().equalsIgnoreCase(correoElectronico) &&
                        usuario.getCorreoElectronico().equalsIgnoreCase(nuevoCorreo)) {
                        return "Error: El nuevo correo ya está en uso.";
                    }
                }
                jugadorDTO.setNombreApellidos(nuevoNombre);
                jugadorDTO.setFechaNacimiento(nuevaFechaNacimiento);
                jugadorDTO.setCorreoElectronico(nuevoCorreo);
                return "Modificación realizada con éxito.";
            }
        }
        return "Error: No se encontró el jugador.";
    }

    /**
     * Lista todos los jugadores activos en el sistema.
     *
     * @return Cadena con la lista de jugadores activos o un mensaje si no hay jugadores activos.
     */
    public String listarJugadores() {
        if (listaJugadores.isEmpty()) {
            return "La lista de jugadores está vacía.";
        }

        StringBuilder resultado = new StringBuilder("Listando jugadores activos:\n");
        boolean hayJugadoresActivos = false;

        for (JugadorDTO jugadorDTO : listaJugadores) {
            if (jugadorDTO.isCuentaActiva()) {
                hayJugadoresActivos = true;
                resultado.append("ID: ").append(jugadorDTO.getIdJugador()).append("\n")
                         .append("Nombre: ").append(jugadorDTO.getNombreApellidos()).append("\n")
                         .append("Fecha de Nacimiento: ").append(new SimpleDateFormat("dd/MM/yyyy").format(jugadorDTO.getFechaNacimiento())).append("\n")
                         .append("Fecha de Inscripción: ").append(jugadorDTO.getFechaInscripcion() != null ?
                             new SimpleDateFormat("dd/MM/yyyy").format(jugadorDTO.getFechaInscripcion()) : "No inscrito").append("\n")
                         .append("Correo Electrónico: ").append(jugadorDTO.getCorreoElectronico()).append("\n")
                         .append("----------------------------------\n");
            }
        }
        if (!hayJugadoresActivos) {
            return "No hay jugadores activos en la lista.";
        }
        return resultado.toString();
    }

    /**
     * Carga la ruta de los archivos desde un archivo de propiedades.
     */
    private void cargarRutaFicheros() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/Ficheros/properties.txt")) {
            properties.load(fis);
            this.ficheroJugadoresPath = properties.getProperty("jugadoresFile");
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el fichero de propiedades: " + e.getMessage());
        }
    }

    /**
     * Carga jugadores desde un archivo CSV.
     *
     * @return Mensaje indicando el resultado de la carga.
     */
    public String cargarJugadoresDesdeFichero() {
        try (BufferedReader br = new BufferedReader(new FileReader(ficheroJugadoresPath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                int idJugador = Integer.parseInt(datos[0]);
                String nombreApellidos = datos[1];
                Date fechaNacimiento = new SimpleDateFormat("yyyy-MM-dd").parse(datos[2]);
                String fechaInscripcionStr = datos[3];
                String correoElectronico = datos[4];
                boolean cuentaActiva = Boolean.parseBoolean(datos[5]);

                JugadorDTO jugadorDTO = new JugadorDTO(nombreApellidos, fechaNacimiento, correoElectronico);
                jugadorDTO.setIdJugador(idJugador);
                jugadorDTO.setFechaInscripcion("null".equals(fechaInscripcionStr) ? null :
                    new SimpleDateFormat("yyyy-MM-dd").parse(fechaInscripcionStr));
                jugadorDTO.setCuentaActiva(cuentaActiva);

                listaJugadores.add(jugadorDTO);
            }
            return "Jugadores cargados desde el fichero CSV.";
        } catch (IOException | ParseException e) {
            return "Error al cargar los jugadores: " + e.getMessage();
        }
    }

    /**
     * Guarda los jugadores en un archivo CSV.
     *
     * @return Mensaje indicando el resultado del guardado.
     */
    public String guardarJugadoresEnFichero() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ficheroJugadoresPath))) {
            for (JugadorDTO jugadorDTO : listaJugadores) {
                StringBuilder sb = new StringBuilder();
                sb.append(jugadorDTO.getIdJugador()).append(";")
                  .append(jugadorDTO.getNombreApellidos()).append(";")
                  .append(new SimpleDateFormat("yyyy-MM-dd").format(jugadorDTO.getFechaNacimiento())).append(";")
                  .append(jugadorDTO.getFechaInscripcion() != null ? 
                      new SimpleDateFormat("yyyy-MM-dd").format(jugadorDTO.getFechaInscripcion()) : "null").append(";")
                  .append(jugadorDTO.getCorreoElectronico()).append(";")
                  .append(jugadorDTO.isCuentaActiva());
                
                bw.write(sb.toString());
                bw.newLine();
            }
            return "Jugadores guardados en el fichero CSV.";
        } catch (IOException e) {
            return "Error al guardar los jugadores: " + e.getMessage();
        }
    }

    /**
     * Busca un jugador por su correo electrónico.
     *
     * @param correoElectronico El correo del jugador a buscar.
     * @return El jugador encontrado o null si no existe.
     */
    public JugadorDTO buscarJugadorPorCorreo(String correoElectronico) {
        for (JugadorDTO jugadorDTO : listaJugadores) {
            if (jugadorDTO.getCorreoElectronico().equalsIgnoreCase(correoElectronico)) {
                return jugadorDTO;
            }
        }
        return null;
    }

    /**
     * Busca un jugador por su ID.
     *
     * @param idJugador El ID del jugador a buscar.
     * @return El jugador encontrado o null si no existe.
     */
    public JugadorDTO buscarJugadorPorId(int idJugador) {
        for (JugadorDTO jugadorDTO : listaJugadores) {
            if (jugadorDTO.getIdJugador() == idJugador) {
                return jugadorDTO;
            }
        }
        return null;
    }
}
