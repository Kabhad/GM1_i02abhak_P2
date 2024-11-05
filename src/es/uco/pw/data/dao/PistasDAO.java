package es.uco.pw.data.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import es.uco.pw.business.material.EstadoMaterial;
import es.uco.pw.business.material.MaterialDTO;
import es.uco.pw.business.material.TipoMaterial;
import es.uco.pw.business.pista.PistaDTO;
import es.uco.pw.business.pista.TamanoPista;

import java.util.*;
import java.io.*;

/**
 * Clase que gestiona las pistas y materiales del sistema.
 * Implementa el patrón Singleton para asegurar que solo haya una instancia de esta clase.
 */
public class PistasDAO {

    private static PistasDAO instancia; // Instancia única (Singleton)
    private List<PistaDTO> pistaDTOs;            // Lista de pistas disponibles en el sistema
    private List<MaterialDTO> materiales;     // Lista de materiales disponibles en el sistema
    private String ficheroPistasPath;      // Ruta del fichero de pistas
    private String ficheroMaterialesPath;   // Ruta del fichero de materiales

    /**
     * Constructor privado para evitar la instanciación directa.
     * Inicializa las listas de pistas y materiales y carga las rutas de los ficheros.
     */
    private PistasDAO() {
        this.pistaDTOs = new ArrayList<>();
        this.materiales = new ArrayList<>();
        this.cargarRutaFicheros(); // Cargar las rutas de los ficheros desde properties.txt
    }

    /**
     * Método estático para obtener la única instancia del gestor.
     * 
     * @return La instancia única de GestorPistas.
     */
    public static synchronized PistasDAO getInstance() {
        if (instancia == null) {
            instancia = new PistasDAO();
        }
        return instancia;
    }

    /**
     * Método para cargar la ruta de los ficheros desde properties.txt.
     * 
     * @throws RuntimeException Si hay un error al leer el fichero de propiedades.
     */
    private void cargarRutaFicheros() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/Ficheros/properties.txt")) {
            properties.load(fis);
            this.ficheroPistasPath = properties.getProperty("pistasFile");
            this.ficheroMaterialesPath = properties.getProperty("materialesFile");
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el fichero de propiedades: " + e.getMessage());
        }
    }

    /**
     * Método para cargar las pistas desde un fichero.
     * 
     * @throws IOException Si hay un error al leer el fichero de pistas.
     */
    public void cargarPistasDesdeFichero() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ficheroPistasPath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                int idPista = Integer.parseInt(datos[0]);
                String nombrePista = datos[1];
                boolean disponible = Boolean.parseBoolean(datos[2]);
                boolean exterior = Boolean.parseBoolean(datos[3]);
                TamanoPista tamanioPista = TamanoPista.valueOf(datos[4]);
                int maxJugadores = Integer.parseInt(datos[5]);

                PistaDTO pistaDTO = new PistaDTO(nombrePista, disponible, exterior, tamanioPista, maxJugadores);
                pistaDTO.setIdPista(idPista);

                // Cargar materiales asociados
                if (datos.length > 6 && !datos[6].isEmpty()) {
                    String[] materialesIds = datos[6].split(",");
                    for (String materialIdStr : materialesIds) {
                        try {
                            int idMaterial = Integer.parseInt(materialIdStr.trim());
                            MaterialDTO materialDTO = buscarMaterialPorId(idMaterial);
                            if (materialDTO != null) {
                                pistaDTO.asociarMaterialAPista(materialDTO);
                            }
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Error al parsear el ID de material: " + materialIdStr);
                        }
                    }
                }
                pistaDTOs.add(pistaDTO);
            }
        }
    }

    /**
     * Método para guardar las pistas en un fichero.
     * 
     * @throws IOException Si hay un error al escribir en el fichero de pistas.
     */
    public void guardarPistasEnFichero() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ficheroPistasPath))) {
            for (PistaDTO pistaDTO : pistaDTOs) {
                StringBuilder sb = new StringBuilder();
                sb.append(pistaDTO.getIdPista()).append(";")
                  .append(pistaDTO.getNombrePista()).append(";")
                  .append(pistaDTO.isDisponible()).append(";")
                  .append(pistaDTO.isExterior()).append(";")
                  .append(pistaDTO.getPista().name()).append(";")
                  .append(pistaDTO.getMax_jugadores());

                List<MaterialDTO> materiales = pistaDTO.getMateriales();
                if (!materiales.isEmpty()) {
                    sb.append(";");
                    for (int i = 0; i < materiales.size(); i++) {
                        sb.append(materiales.get(i).getId());
                        if (i < materiales.size() - 1) {
                            sb.append(",");
                        }
                    }
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }

    /**
     * Método para cargar los materiales desde un fichero.
     * 
     * @throws IOException Si hay un error al leer el fichero de materiales.
     */
    public void cargarMaterialesDesdeFichero() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ficheroMaterialesPath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    String[] datos = linea.split(";");
                    if (datos.length >= 4) {
                        try {
                            int idMaterial = Integer.parseInt(datos[0].trim());
                            TipoMaterial tipo = TipoMaterial.valueOf(datos[1].trim());
                            boolean usoExterior = Boolean.parseBoolean(datos[2].trim());
                            EstadoMaterial estado = EstadoMaterial.valueOf(datos[3].trim());

                            MaterialDTO materialDTO = new MaterialDTO(idMaterial, tipo, usoExterior, estado);
                            materiales.add(materialDTO);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Error al parsear material en la línea: " + linea);
                        }
                    } else {
                        throw new IllegalArgumentException("Línea incompleta en el fichero de materiales: " + linea);
                    }
                }
            }
        }
    }

    /**
     * Método para guardar los materiales en un fichero.
     * 
     * @throws IOException Si hay un error al escribir en el fichero de materiales.
     */
    public void guardarMaterialesEnFichero() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ficheroMaterialesPath))) {
            for (MaterialDTO materialDTO : materiales) {
                StringBuilder sb = new StringBuilder();
                sb.append(materialDTO.getId()).append(";")
                  .append(materialDTO.getTipo().name()).append(";")
                  .append(materialDTO.isUsoExterior()).append(";")
                  .append(materialDTO.getEstado().name());

                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }

    /**
     * Método para crear una nueva pista y añadirla a la lista de pistas.
     * 
     * @param nombre       Nombre de la nueva pista.
     * @param disponible   Indica si la pista está disponible.
     * @param exterior     Indica si la pista es exterior.
     * @param pista        Tamaño de la pista.
     * @param maxJugadores Número máximo de jugadores en la pista.
     */
    public void crearPista(String nombre, boolean disponible, boolean exterior, TamanoPista pista, int maxJugadores) {
        PistaDTO nuevaPista = new PistaDTO(nombre, disponible, exterior, pista, maxJugadores);
        pistaDTOs.add(nuevaPista);
    }

    /**
     * Método para crear un nuevo material y añadirlo a la lista de materiales.
     * 
     * @param id          Identificador del nuevo material.
     * @param tipo        Tipo del nuevo material.
     * @param usoExterior Indica si el material es para uso exterior.
     * @param estado      Estado del nuevo material.
     */
    public void crearMaterial(int id, TipoMaterial tipo, boolean usoExterior, EstadoMaterial estado) {
        MaterialDTO nuevoMaterial = new MaterialDTO(id, tipo, usoExterior, estado);
        materiales.add(nuevoMaterial);
    }

    /**
     * Método para asociar un material a una pista disponible.
     * 
     * @param nombrePista Nombre de la pista a la que se quiere asociar el material.
     * @param idMaterial  ID del material a asociar.
     * @return True si la asociación fue exitosa, false en caso contrario.
     * @throws IllegalArgumentException Si la pista o el material no existen, o si la pista o el material no están disponibles.
     */
    public boolean asociarMaterialAPista(String nombrePista, int idMaterial) {
        PistaDTO pistaSeleccionada = buscarPistaPorNombre(nombrePista);
        MaterialDTO materialSeleccionado = buscarMaterialPorId(idMaterial);

        if (pistaSeleccionada == null) {
            throw new IllegalArgumentException("La pista no existe.");
        }

        if (materialSeleccionado == null) {
            throw new IllegalArgumentException("El material no existe.");
        }

        if (!pistaSeleccionada.isDisponible()) {
            throw new IllegalArgumentException("La pista no está disponible.");
        }

        if (materialSeleccionado.getEstado() != EstadoMaterial.DISPONIBLE) {
            throw new IllegalArgumentException("El material no está disponible.");
        }

        for (PistaDTO pistaDTO : pistaDTOs) {
            if (pistaDTO.getMateriales().contains(materialSeleccionado)) {
                throw new IllegalArgumentException("El material ya está asignado a otra pista.");
            }
        }

        return pistaSeleccionada.asociarMaterialAPista(materialSeleccionado);
    }

    /**
     * Método auxiliar para buscar una pista por su nombre.
     * 
     * @param nombrePista Nombre de la pista a buscar.
     * @return La pista correspondiente al nombre dado, o null si no se encuentra.
     */
    private PistaDTO buscarPistaPorNombre(String nombrePista) {
        for (PistaDTO pistaDTO : pistaDTOs) {
            if (pistaDTO.getNombrePista().equalsIgnoreCase(nombrePista)) {
                return pistaDTO;
            }
        }
        return null;
    }

    /**
     * Método auxiliar para buscar un material por su ID.
     * 
     * @param idMaterial ID del material a buscar.
     * @return El material correspondiente al ID dado, o null si no se encuentra.
     */
    private MaterialDTO buscarMaterialPorId(int idMaterial) {
        for (MaterialDTO materialDTO : materiales) {
            if (materialDTO.getId() == idMaterial) {
                return materialDTO;
            }
        }
        return null;
    }

    /**
     * Método para buscar todas las pistas disponibles.
     * 
     * @return Lista de pistas disponibles.
     */
    public List<PistaDTO> buscarPistasDisponibles() {
        return pistaDTOs.stream()
                     .filter(PistaDTO::isDisponible)
                     .collect(Collectors.toList());
    }

    /**
     * Método para listar todas las pistas no disponibles.
     * 
     * @return Lista de pistas no disponibles.
     */
    public List<PistaDTO> listarPistasNoDisponibles() {
        return pistaDTOs.stream()
                     .filter(pista -> !pista.isDisponible())
                     .collect(Collectors.toList());
    }

    /**
     * Método para buscar pistas disponibles según el número de jugadores y tipo de pista.
     * 
     * @param numJugadores Número de jugadores que se busca.
     * @param tipoPista    Tipo de pista que se busca.
     * @return Lista de pistas disponibles que cumplen con los criterios dados.
     */
    public List<PistaDTO> buscarPistasDisponibles(int numJugadores, TamanoPista tipoPista) {
        return pistaDTOs.stream()
                     .filter(pista -> pista.isDisponible() && pista.getPista() == tipoPista && pista.getMax_jugadores() >= numJugadores)
                     .collect(Collectors.toList());
    }

    /**
     * Método para listar todas las pistas con sus detalles.
     * 
     * @return String con los detalles de todas las pistas.
     */
    public String listarPistas() {
        StringBuilder resultado = new StringBuilder();
        for (PistaDTO pistaDTO : pistaDTOs) {
            resultado.append("ID: ").append(pistaDTO.getIdPista()).append("\n")
                     .append("Nombre Pista: ").append(pistaDTO.getNombrePista()).append("\n")
                     .append("Disponible: ").append(pistaDTO.isDisponible() ? "Sí" : "No").append("\n")
                     .append("Exterior: ").append(pistaDTO.isExterior() ? "Sí" : "No").append("\n")
                     .append("Tamaño Pista: ").append(pistaDTO.getPista().toString()).append("\n")
                     .append("Max Jugadores: ").append(pistaDTO.getMax_jugadores()).append("\n")
                     .append("Materiales: ");
            
            if (pistaDTO.getMateriales().isEmpty()) {
                resultado.append("[]\n");
            } else {
                List<Integer> idsMateriales = pistaDTO.getMateriales().stream().map(MaterialDTO::getId).collect(Collectors.toList());
                resultado.append(idsMateriales).append("\n");
            }

            resultado.append("----------------------------------\n");
        }
        return resultado.toString();
    }

    /**
     * Método para buscar una pista por su ID.
     * 
     * @param idPista ID de la pista a buscar.
     * @return La pista correspondiente al ID dado, o null si no se encuentra.
     */
    public PistaDTO buscarPistaPorId(int idPista) {
        for (PistaDTO pistaDTO : pistaDTOs) {
            if (pistaDTO.getIdPista() == idPista) {
                return pistaDTO;
            }
        }
        return null;
    }
}

