package es.uco.pw.data.dao;

import com.mysql.jdbc.Connection;

import es.uco.pw.business.material.EstadoMaterial;
import es.uco.pw.business.material.MaterialDTO;
import es.uco.pw.business.material.TipoMaterial;
import es.uco.pw.business.pista.PistaDTO;
import es.uco.pw.business.pista.TamanoPista;
import es.uco.pw.data.common.DBConnection;

import java.util.*;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase que gestiona las pistas y materiales del sistema.
 * Implementa el patrón Singleton para asegurar que solo haya una instancia de esta clase.
 */
public class PistasDAO {

	private Connection con;
    private Properties prop;

    /**
     * Constructor privado para evitar la instanciación directa.
     * Inicializa las listas de pistas y materiales y carga las rutas de los ficheros.
     */
    private PistasDAO() {
    	prop = new Properties();
        
        try {
        	BufferedReader reader = new BufferedReader(new FileReader("sql.properties"));
        	prop.load(reader);
        	reader.close();
        } catch(FileNotFoundException e) {
        	e.printStackTrace();
        } catch(IOException e) {
        	e.printStackTrace();
        }
    }

    /**
     * Método estático para obtener la única instancia del gestor.
     * 
     * @return La instancia única de GestorPistas.
     */
    public static synchronized PistasDAO getInstance() {
        PistasDAO instancia = null;
		if (instancia == null) {
            instancia = new PistasDAO();
        }
        return instancia;
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
    public void crearPista(String nombre, boolean disponible, boolean exterior, TamanoPista pista, int maxJugadores) throws SQLException {
    	DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();
        String sql = prop.getProperty("crearPista");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setBoolean(2, disponible);
            ps.setBoolean(3, exterior);
            ps.setString(4, pista.name());
            ps.setInt(5, maxJugadores);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para crear un nuevo material y añadirlo a la lista de materiales.
     * 
     * @param id          Identificador del nuevo material.
     * @param tipo        Tipo del nuevo material.
     * @param usoExterior Indica si el material es para uso exterior.
     * @param estado      Estado del nuevo material.
     */
    public void crearMaterial(int id, TipoMaterial tipo, boolean usoExterior, EstadoMaterial estado) throws SQLException {
    	 DBConnection conexion = new DBConnection();
         con = (Connection) conexion.getConnection();
         String sql = prop.getProperty("crearMaterial");

         try (PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setInt(1, id);
             ps.setString(2, tipo.name());
             ps.setBoolean(3, usoExterior);
             ps.setString(4, estado.name());
             ps.executeUpdate();
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
    
    /**
     * Método para asociar un material a una pista disponible.
     * 
     * @param nombrePista Nombre de la pista a la que se quiere asociar el material.
     * @param idMaterial  ID del material a asociar.
     * @return True si la asociación fue exitosa, false en caso contrario.
     * @throws IllegalArgumentException Si la pista o el material no existen, o si la pista o el material no están disponibles.
     */
    public boolean asociarMaterialAPista(String nombrePista, int idMaterial) throws SQLException { //error enseñar angel
    	DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();
        String sql = prop.getProperty("asociarMaterialAPista");

        try {
            PistaDTO pistaSeleccionada = buscarPistaPorNombre(nombrePista);
            MaterialDTO materialSeleccionado = buscarMaterialPorId(idMaterial);

            if (pistaSeleccionada == null || !pistaSeleccionada.isDisponible() ||
                materialSeleccionado == null || materialSeleccionado.getEstado() != EstadoMaterial.DISPONIBLE) {
                throw new IllegalArgumentException("La pista o el material no están disponibles.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombrePista);
                ps.setInt(2, idMaterial);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método auxiliar para buscar una pista por su nombre.
     * 
     * @param nombrePista Nombre de la pista a buscar.
     * @return La pista correspondiente al nombre dado, o null si no se encuentra.
     */
    private PistaDTO buscarPistaPorNombre(String nombrePista) throws SQLException {
    	DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();
        String sql = prop.getProperty("buscarPistaPorNombre");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombrePista);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PistaDTO(rs.getString("nombre"), rs.getBoolean("disponible"),
                                        rs.getBoolean("exterior"), TamanoPista.valueOf(rs.getString("tamanoPista")),
                                        rs.getInt("maxJugadores"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método auxiliar para buscar un material por su ID.
     * 
     * @param idMaterial ID del material a buscar.
     * @return El material correspondiente al ID dado, o null si no se encuentra.
     */
    private MaterialDTO buscarMaterialPorId(int idMaterial) throws SQLException {
    	DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();
        String sql = prop.getProperty("buscarMaterialPorId");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMaterial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MaterialDTO(rs.getInt("idMaterial"), TipoMaterial.valueOf(rs.getString("tipo")),
                                           rs.getBoolean("usoExterior"), EstadoMaterial.valueOf(rs.getString("estado")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método para buscar todas las pistas disponibles.
     * 
     * @return Lista de pistas disponibles.
     */
    public List<PistaDTO> buscarPistasDisponibles() throws SQLException {
    	List<PistaDTO> pistas = new ArrayList<>();
        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();
        String sql = prop.getProperty("buscarPistasDisponibles");

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pistas.add(new PistaDTO(rs.getString("nombre"), rs.getBoolean("disponible"),
                                        rs.getBoolean("exterior"), TamanoPista.valueOf(rs.getString("tamanoPista")),
                                        rs.getInt("maxJugadores")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pistas;
    }

    /**
     * Método para listar todas las pistas no disponibles.
     * 
     * @return Lista de pistas no disponibles.
     */
    public List<PistaDTO> listarPistasNoDisponibles() throws SQLException {
    	List<PistaDTO> pistas = new ArrayList<>();
        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();
        String sql = prop.getProperty("listarPistasNoDisponibles");

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pistas.add(new PistaDTO(rs.getString("nombre"), rs.getBoolean("disponible"),
                                        rs.getBoolean("exterior"), TamanoPista.valueOf(rs.getString("tamanoPista")),
                                        rs.getInt("maxJugadores")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pistas;
    }

    /**
     * Método para buscar pistas disponibles según el número de jugadores y tipo de pista.
     * 
     * @param numJugadores Número de jugadores que se busca.
     * @param tipoPista    Tipo de pista que se busca.
     * @return Lista de pistas disponibles que cumplen con los criterios dados.
     */
    public List<PistaDTO> buscarPistasDisponibles(int numJugadores, TamanoPista tipoPista) throws SQLException {
        String sql = prop.getProperty("buscarPistasDisponibles");
        List<PistaDTO> pistasFiltradas = new ArrayList<>();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
        	ps.setInt(1, numJugadores);
        	ps.setString(2,  tipoPista.name());
        	ResultSet rs = ps.executeQuery();
        	
        	while(rs.next()) {
        		PistaDTO pista = new PistaDTO(rs.getString("nombre"), rs.getBoolean("disponible"), rs.getBoolean("exterior"), TamanoPista.valueOf(rs.getString("tamanoPista")), rs.getInt("maxJugadores"));
        		pista.setIdPista(rs.getInt("id"));
        		pistasFiltradas.add(pista);
        	}
        }
        return pistasFiltradas;
    }

    /**
     * Método para listar todas las pistas con sus detalles.
     * 
     * @return String con los detalles de todas las pistas.
     */
    public List<PistaDTO> listarPistas() throws SQLException {
        String sql = prop.getProperty("listarPistas");
        List<PistaDTO> pistas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pistas.add(new PistaDTO(rs.getString("nombre"), rs.getBoolean("disponible"), rs.getBoolean("exterior"), TamanoPista.valueOf(rs.getString("tamanoPista")), rs.getInt("maxJugadores")));
            }
        }
        return pistas;
    }

    /**
     * Método para buscar una pista por su ID.
     * 
     * @param idPista ID de la pista a buscar.
     * @return La pista correspondiente al ID dado, o null si no se encuentra.
     */
    public PistaDTO buscarPistaPorId(int idPista) throws  SQLException {
    	String sql = prop.getProperty("buscarPistaPorId");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
        	ps.setInt(1, idPista);
        	ResultSet rs = ps.executeQuery();
        	
        	if(rs.next()) {
        		PistaDTO pista = new PistaDTO(rs.getString("nombre"), rs.getBoolean("disponible"), rs.getBoolean("exterior"), TamanoPista.valueOf(rs.getString("tamanoPista")), rs.getInt("maxJugadores"));
        		pista.setIdPista(rs.getInt("id"));
        		return pista;
        	}
        }
        return null;
    }
}

