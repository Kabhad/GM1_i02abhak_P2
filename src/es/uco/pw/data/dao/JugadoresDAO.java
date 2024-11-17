package es.uco.pw.data.dao;

import java.util.Date;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;
import es.uco.pw.data.common.DBConnection;
import es.uco.pw.business.jugador.JugadorDTO;


/**
 * Clase que gestiona los jugadores registrados en el sistema. Permite operaciones como
 * alta, baja, modificación, y almacenamiento de jugadores.
 * Implementa el patrón Singleton para garantizar una única instancia del gestor.
 */
public class JugadoresDAO {

    /**
     * Conexión activa con la base de datos.
     */
    private Connection con;
    /**
     * Propiedades que contienen las consultas SQL cargadas desde el archivo `sql.properties`.
     */
    private Properties prop;

    /**
     * Constructor que carga las propiedades SQL desde el archivo `sql.properties`.
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
        DBConnection connection = new DBConnection();
        con = (Connection) connection.getConnection();
        try {
            PreparedStatement psExistencia = (PreparedStatement) con.prepareStatement(prop.getProperty("consultaExistenciaPorCorreo"));
            psExistencia.setString(1, nuevoJugador.getCorreoElectronico());
            ResultSet rs = psExistencia.executeQuery();

            if (rs.next()) {
                boolean cuentaActiva = rs.getInt("cuentaActiva") == 1;
                if (cuentaActiva) {
                    return "Error: El correo ya está registrado y en uso";
                } else {
                    PreparedStatement psReactivar = (PreparedStatement) con.prepareStatement(prop.getProperty("reactivarCuenta"));
                    psReactivar.setString(1, nuevoJugador.getNombreApellidos());
                    psReactivar.setDate(2, new java.sql.Date(nuevoJugador.getFechaNacimiento().getTime()));
                    psReactivar.setNull(3, java.sql.Types.DATE); // Fecha de inscripción como NULL
                    psReactivar.setString(4, nuevoJugador.getCorreoElectronico());
                    psReactivar.executeUpdate();
                    return "Cuenta reactivada y datos actualizados con éxito";
                }
            } else {
                PreparedStatement psAlta = (PreparedStatement) con.prepareStatement(prop.getProperty("altaJugador"));
                psAlta.setString(1, nuevoJugador.getNombreApellidos());
                psAlta.setDate(2, new java.sql.Date(nuevoJugador.getFechaNacimiento().getTime()));
                psAlta.setNull(3, java.sql.Types.DATE); // Fecha de inscripción como NULL
                psAlta.setString(4, nuevoJugador.getCorreoElectronico());
                int cuentaActivaValor = nuevoJugador.isCuentaActiva() ? 1 : 0;
                psAlta.setInt(5, cuentaActivaValor);
                psAlta.executeUpdate();
                return "Jugador registrado con éxito.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error en la Base de Datos: " + e.getMessage();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
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
        DBConnection connection = new DBConnection();
        con = (Connection) connection.getConnection();
        try {
            PreparedStatement psExistencia = (PreparedStatement) con.prepareStatement(prop.getProperty("consultaExistenciaPorCorreo"));
            psExistencia.setString(1, correoElectronico);
            ResultSet rs = psExistencia.executeQuery();

            if (rs.next()) {
                boolean cuentaActiva = rs.getInt("cuentaActiva") == 1;
                if (!cuentaActiva) {
                    return "Error: El jugador ya está dado de baja";
                } else {
                    PreparedStatement psBaja = (PreparedStatement) con.prepareStatement(prop.getProperty("desactivarCuenta"));
                    psBaja.setString(1, correoElectronico);
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
        DBConnection connection = new DBConnection();
        con = (Connection) connection.getConnection();
        try {
            PreparedStatement psExistencia = (PreparedStatement) con.prepareStatement(prop.getProperty("consultaExistenciaPorCorreo"));
            psExistencia.setString(1, correoElectronico);
            ResultSet rs = psExistencia.executeQuery();

            if (!rs.next()) {
                return "Error: No se encontró el jugador en la base de datos.";
            } else {
                boolean cuentaActiva = rs.getInt("cuentaActiva") == 1;
                if (!cuentaActiva) {
                    return "Error: La cuenta del jugador no está activa.";
                }
            }

            PreparedStatement psVerificarCorreo = (PreparedStatement) con.prepareStatement(prop.getProperty("verificarCorreo"));
            psVerificarCorreo.setString(1, nuevoCorreo);
            psVerificarCorreo.setString(2, correoElectronico);
            ResultSet rsCorreo = psVerificarCorreo.executeQuery();

            if (rsCorreo.next()) {
                return "Error: El nuevo correo ya está en uso por otro jugador.";
            }

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
        DBConnection connection = new DBConnection();
        con = (Connection) connection.getConnection();
        StringBuilder resultado = new StringBuilder("Listando jugadores activos:\n");
        boolean hayJugadoresActivos = false;

        try {
            PreparedStatement ps = (PreparedStatement) con.prepareStatement("SELECT * FROM Jugador WHERE cuentaActiva = 1");
            ResultSet rs = ps.executeQuery();

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

            if (!hayJugadoresActivos) {
                return "No hay jugadores activos en la base de datos.";
            }

            return resultado.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error en la Base de Datos: " + e.getMessage();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Busca un jugador en la base de datos por su ID.
     *
     * @param idJugador El ID del jugador a buscar.
     * @return Un objeto JugadorDTO con los datos del jugador si se encuentra, o null si no existe.
     */
    public JugadorDTO buscarJugadorPorId(int idJugador) {
        DBConnection connection = new DBConnection();
        con = (Connection) connection.getConnection();

        try {
            PreparedStatement ps = (PreparedStatement) con.prepareStatement(prop.getProperty("buscarJugadorPorId"));
            ps.setInt(1, idJugador);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JugadorDTO jugador = new JugadorDTO();
                jugador.setIdJugador(rs.getInt("idJugador"));
                jugador.setNombreApellidos(rs.getString("nombreApellidos"));
                jugador.setFechaNacimiento(rs.getDate("fechaNacimiento"));
                jugador.setFechaInscripcion(rs.getDate("fechaInscripcion"));
                jugador.setCorreoElectronico(rs.getString("correo"));
                jugador.setCuentaActiva(rs.getInt("cuentaActiva") == 1);
                return jugador;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Busca un jugador en la base de datos por su correo electrónico.
     *
     * @param correoElectronico El correo electrónico del jugador a buscar.
     * @return Un objeto JugadorDTO con los datos del jugador si se encuentra, o null si no existe.
     */
    public JugadorDTO buscarJugadorPorCorreo(String correoElectronico) {
        DBConnection connection = new DBConnection();
        con = (Connection) connection.getConnection();

        try {
            PreparedStatement ps = (PreparedStatement) con.prepareStatement(prop.getProperty("buscarJugadorInscritoPorCorreo"));
            ps.setString(1, correoElectronico);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JugadorDTO jugador = new JugadorDTO();
                jugador.setIdJugador(rs.getInt("idJugador"));
                jugador.setNombreApellidos(rs.getString("nombreApellidos"));
                jugador.setFechaNacimiento(rs.getDate("fechaNacimiento"));
                jugador.setFechaInscripcion(rs.getDate("fechaInscripcion"));
                jugador.setCorreoElectronico(rs.getString("correo"));
                jugador.setCuentaActiva(rs.getInt("cuentaActiva") == 1);
                return jugador;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
	/**
	 * Actualiza la fecha de inscripción de un jugador en la base de datos.
	 *
	 * @param correoElectronico El correo del jugador cuya fecha de inscripción se actualizará.
	 * @return Mensaje indicando el resultado de la operación.
	 */
    public String actualizarFechaInscripcion(String correoElectronico) {
        DBConnection connection = new DBConnection();
        con = (Connection) connection.getConnection();
        try {
            PreparedStatement psActualizarFecha = (PreparedStatement) con.prepareStatement(prop.getProperty("actualizarFechaInscripcion"));
            psActualizarFecha.setDate(1, new java.sql.Date(new Date().getTime()));
            psActualizarFecha.setString(2, correoElectronico);
            psActualizarFecha.executeUpdate();
            return "Fecha de inscripción actualizada con éxito.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error en la Base de Datos: " + e.getMessage();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
