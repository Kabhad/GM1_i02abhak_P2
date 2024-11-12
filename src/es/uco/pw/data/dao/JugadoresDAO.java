package es.uco.pw.data.dao;


import java.util.Date;
import java.util.List;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import es.uco.pw.business.jugador.JugadorDTO;
import es.uco.pw.data.common.DBConnection;

/**
 * Clase que gestiona los jugadores registrados en el sistema. Permite operaciones como
 * alta, baja, modificación, y almacenamiento de jugadores en un archivo CSV.
 * Implementa el patrón Singleton para garantizar una única instancia del gestor.
 */
public class JugadoresDAO {
    
	private List<JugadorDTO> listaJugadores;
    private Connection con;
    private Properties prop;
    private static JugadoresDAO instancia; // Singleton

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
     * Constructor
     */
    public JugadoresDAO() {
    	prop = new Properties();
    	
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader("sql.properties"));
    		prop.load(reader);
    		reader.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }


    /**
     * Da de alta a un nuevo jugador en el sistema. Si el correo ya existe, reactiva la cuenta y actualiza los datos.
     *
     * @param nuevoJugador El nuevo jugador a registrar.
     * @return Mensaje indicando el resultado de la operación.
     */
    public String altaJugador(JugadorDTO nuevoJugador) {
    	// Establecemos la conexión
    	DBConnection connection = new DBConnection();
    	con = (Connection) connection.getConnection();
    	try {
    		// Paso 1: Comprobar si el jugador ya existe en la base de datos
    		PreparedStatement psExistencia = (PreparedStatement) con.prepareStatement(prop.getProperty("consultaExistenciaPorCorreo"));
    		psExistencia.setString(1, nuevoJugador.getCorreoElectronico());
    		
    		ResultSet rs = psExistencia.executeQuery();
    		
    		if (rs.next()) { // Si existe el registro
    			boolean cuentaActiva = rs.getInt("cuentaActiva") == 1;
    			
    			if (cuentaActiva) {
    				// Paso 2: Si la cuenta está activa, devolver el mensaje de error
    				return "Error: El correo ya está registrado y en uso";
    			} else {
    				// Paso 3: Si la cuenta está inactiva, reactivarla y actualizar los datos
    				PreparedStatement psReactivar = (PreparedStatement) con.prepareStatement(prop.getProperty("reactivarCuenta"));
    				
    				psReactivar.setString(1,nuevoJugador.getNombreApellidos());
    				psReactivar.setDate(2, new java.sql.Date(nuevoJugador.getFechaNacimiento().getTime()));
    				psReactivar.setDate(3, new java.sql.Date(new Date().getTime())); // Actualizar la fecha de inscripción a la actual
    				psReactivar.setString(4, nuevoJugador.getCorreoElectronico());
    				
    				psReactivar.executeUpdate();
    				return "Cuenta reactivada y datos actualizados con éxito";
    				
    			}
    		} else {
    			// Paso 4: Si el jugador no existe, insertarlo como nuevo registro
    			PreparedStatement psAlta = (PreparedStatement) con.prepareStatement(prop.getProperty("altaJugador"));
    			
    			//psAlta.setInt(1,nuevoJugador.getIdJugador());
    			psAlta.setString(1,nuevoJugador.getNombreApellidos());
    			psAlta.setDate(2, new java.sql.Date(nuevoJugador.getFechaNacimiento().getTime()));
    			psAlta.setDate(3, new java.sql.Date(new Date().getTime())); // Fecha actual como fecha de inscripción
    			psAlta.setString(4, nuevoJugador.getCorreoElectronico());
    			
    			// Convertimos el booleano a int: true -> 1, false -> 0
                int cuentaActivaValor = nuevoJugador.isCuentaActiva() ? 1 : 0;
                psAlta.setInt(5, cuentaActivaValor); // Añade el campo cuentaActiva como entero
                
                psAlta.executeUpdate();
                return "Jugador registrado con éxito.";
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return "Error en la Base de Datos: " + e.getMessage();
    	} finally {
    		// Asegurar la desconexión de la base de datos
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Log para cualquier error al cerrar la conexión
                }
            }
    	}
    }

    /**
     * Da de baja a un jugador desactivando su cuenta.
     *
     * @param correoElectronico El correo del jugador a dar de baja.
     * @return Mensaje indicando el resultado de la operación.
     */
    public String bajaJugador(String correoElectronico) {
    	// Establecemos la conexión
    	DBConnection connection = new DBConnection();
    	con = (Connection) connection.getConnection();
    	
    	try {
    		// Paso 1: Verificar si el jugador existe y si está activo
    		PreparedStatement psExistencia = (PreparedStatement) con.prepareStatement(prop.getProperty("consultaExistenciaPorCorreo"));
    		psExistencia.setString(1, correoElectronico);
    		
    		ResultSet rs = psExistencia.executeQuery();
    		
    		if (rs.next()) {
    			boolean cuentaActiva = rs.getInt("cuentaActiva") == 1;
    			
    			if (!cuentaActiva) {
    				// Paso 2: Si la cuenta ya está inactiva, devolver un mensaje de error
    				return "Error: El jugador ya está dado de baja";
    			} else {
    				// Paso 3: Si la cuenta está activa, desactivarla
    				PreparedStatement psBaja = (PreparedStatement) con.prepareStatement(prop.getProperty("desactivarCuenta"));
    				psBaja.setString(1,correoElectronico);
    				
    				int filasActualizadas = psBaja.executeUpdate();
    				
    				if (filasActualizadas > 0) {
                        return "Jugador dado de baja correctamente.";
                    } else {
                        return "Error al dar de baja al jugador.";
                    }
    			}
    		} else {
    			return "Error: No se encontró al jugador en la Base de Datos.";
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return "Error en la Base de Datos: " + e.getMessage();
    	} finally {
    		// Cerrar la conexión a la base de datos
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    	}
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
    	// Establecemos la conexión
    	DBConnection connection = new DBConnection();
    	con = (Connection) connection.getConnection();
    	
    	try {
    		// Paso 1: Verificar si el jugador existe y está activo
    		PreparedStatement psExistencia = (PreparedStatement) con.prepareStatement(prop.getProperty("consultaExistenciaPorCorreo"));
    		psExistencia.setString(1,correoElectronico);
    		ResultSet rs = psExistencia.executeQuery();
    		
    		if (!rs.next()) {
    			return "Error: No se encontró el jugador en la base de datos.";
    		} else { 
    			boolean cuentaActiva = rs.getInt("cuentaActiva") == 1;
    			if (!cuentaActiva) {
    				return "Error: La cuenta del jugador no está activa.";
    			}
    		}
    		
    		// Paso 2: Verificar que el nuevo correo no esté en uso por otro jugador
    		PreparedStatement psVerificarCorreo = (PreparedStatement) con.prepareStatement(prop.getProperty("verificarCorreo"));
    		psVerificarCorreo.setString(1,nuevoCorreo);
    		psVerificarCorreo.setString(2,correoElectronico);
    		ResultSet rsCorreo = psVerificarCorreo.executeQuery();
    		
    		if (rsCorreo.next()) {
    			return "Error: El nuevo correo ya está en uso por otro jugador.";
    		}
    		
    		// Paso 3: Actualizar la información del jugador
    		PreparedStatement psModificar = (PreparedStatement) con.prepareStatement(prop.getProperty("actualizarInfo"));
    		psModificar.setString(1, nuevoNombre);
            psModificar.setDate(2, new java.sql.Date(nuevaFechaNacimiento.getTime()));
            psModificar.setString(3, nuevoCorreo);
            psModificar.setString(4, correoElectronico);
            
            int filasActualizadas = psModificar.executeUpdate();

            if (filasActualizadas > 0) {
                return "Modificación realizada con éxito.";
            } else {
                return "Error: No se pudo actualizar el jugador.";
            }
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return "Error en la Base de Datos: " + e.getMessage();
    	} finally {
    		// Cerrar la conexión a la base de datos
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    	}
    }

    /**
     * Lista todos los jugadores activos en el sistema.
     *
     * @return Cadena con la lista de jugadores activos o un mensaje si no hay jugadores activos.
     */
    public String listarJugadores() {
    	// Establecemos la conexión
    	DBConnection connection = new DBConnection();
    	con = (Connection) connection.getConnection();
    	StringBuilder resultado = new StringBuilder("Listando jugadores activos:\n");
        boolean hayJugadoresActivos = false;
        
        try {
        	// Preparar la consulta para seleccionar jugadores activos
        	PreparedStatement ps = (PreparedStatement) con.prepareStatement("SELECT * FROM Jugador WHERE cuentaActiva = 1");

        	ResultSet rs = ps.executeQuery();
        	
        	// Recorrer los resultados de la consulta
        	while (rs.next()) {
                hayJugadoresActivos = true;
                resultado.append("ID: ").append(rs.getInt("idJugador")).append("\n")
                         .append("Nombre: ").append(rs.getString("nombreApellidos")).append("\n")
                         .append("Fecha de Nacimiento: ").append(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fechaNacimiento"))).append("\n")
                         .append("Fecha de Inscripción: ").append(rs.getDate("fechaInscripcion") != null ?
                             new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("fechaInscripcion")) : "No inscrito").append("\n")
                         .append("Correo Electrónico: ").append(rs.getString("correo")).append("\n")
                         .append("----------------------------------\n");
            }
        	
        	// Si no hay jugadores activos, retornar un mensaje adecuado
            if (!hayJugadoresActivos) {
                return "No hay jugadores activos en la base de datos.";
            }
            
            return resultado.toString();
        } catch (SQLException e) {
        	e.printStackTrace();
        	return "Error en la Base de Datos: " + e.getMessage();
        } finally {
        	// Cerrar la conexión a la base de datos
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
