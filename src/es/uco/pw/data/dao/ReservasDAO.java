package es.uco.pw.data.dao;

import es.uco.pw.business.jugador.JugadorDTO;
import es.uco.pw.business.pista.PistaDTO;
import es.uco.pw.business.pista.TamanoPista;
import es.uco.pw.business.reserva.*;
import es.uco.pw.data.common.DBConnection;

import java.util.*;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

/**
 * Clase que gestiona las reservas de pistas de baloncesto, incluyendo la carga y
 * almacenamiento de reservas en ficheros, así como la gestión de jugadores y pistas.
 */
public class ReservasDAO {
    private Connection con;
    private Properties prop;

    /**
     * Constructor privado para evitar instanciación directa.
     * Inicializa la conexion a la BD e instancia reservas.
     */
    public ReservasDAO() {
    	
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
     * Busca un jugador por su ID.
     * 
     * @param idJugador El ID del jugador a buscar.
     * @return El jugador encontrado o null si no se encuentra.
     */
    public static JugadorDTO buscarJugadorPorId(int idJugador) {
        JugadoresDAO jugadoresDAO = new JugadoresDAO();  // Crear una instancia de JugadoresDAO
        return jugadoresDAO.buscarJugadorPorId(idJugador);
    }
    
    private void insertarReservaFamiliar(int idReserva, int numeroAdultos, int numeroNinos) throws SQLException {
        String sql = prop.getProperty("insertarReservaFamiliar");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, numeroAdultos);
            ps.setInt(3, numeroNinos);
            ps.executeUpdate();
        }
    }

    private void insertarReservaAdulto(int idReserva, int numeroAdultos) throws SQLException {
        String sql = prop.getProperty("insertarReservaAdulto");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, numeroAdultos);
            ps.executeUpdate();
        }
    }

    private void insertarReservaInfantil(int idReserva, int numeroNinos) throws SQLException {
        String sql = prop.getProperty("insertarReservaInfantil");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, numeroNinos);
            ps.executeUpdate();
        }
    }

    
    public int insertarReserva(ReservaDTO reservaDTO) {
        int idReserva = -1;
        String sql = prop.getProperty("insertarReserva");
        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Inserción en la tabla `Reserva`
            ps.setInt(1, reservaDTO.getIdUsuario());
            ps.setInt(2, reservaDTO.getIdPista());
            ps.setTimestamp(3, new java.sql.Timestamp(reservaDTO.getFechaHora().getTime()));
            ps.setInt(4, reservaDTO.getDuracionMinutos());
            ps.setFloat(5, reservaDTO.getPrecio());
            ps.setFloat(6, reservaDTO.getDescuento());
            ps.setObject(7, reservaDTO instanceof ReservaBono ? ((ReservaBono) reservaDTO).getBono().getIdBono() : null);

            ps.executeUpdate();

            // Obtener el `idReserva` generado
            try (ResultSet rs = (ResultSet) ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idReserva = rs.getInt(1);
                }
            }

         // Si se generó el `idReserva`, se inserta en la tabla específica según el tipo de reserva
            if (idReserva != -1) {
                ReservaDTO reservaEspecifica = null;
                if (reservaDTO instanceof ReservaIndividual) {
                    reservaEspecifica = ((ReservaIndividual) reservaDTO).getReservaEspecifica();
                } else if (reservaDTO instanceof ReservaBono) {
                    reservaEspecifica = ((ReservaBono) reservaDTO).getReservaEspecifica();
                }

                if (reservaEspecifica != null) {
                    if (reservaEspecifica instanceof ReservaFamiliar) {
                        System.out.println("Entramos aquí en ReservaFamiliar");
                        insertarReservaFamiliar(idReserva, ((ReservaFamiliar) reservaEspecifica).getNumeroAdultos(), ((ReservaFamiliar) reservaEspecifica).getNumeroNinos());
                    } else if (reservaEspecifica instanceof ReservaAdulto) {
                        System.out.println("Entramos aquí en ReservaAdulto");
                        insertarReservaAdulto(idReserva, ((ReservaAdulto) reservaEspecifica).getNumeroAdultos());
                    } else if (reservaEspecifica instanceof ReservaInfantil) {
                        System.out.println("Entramos aquí en ReservaInfantil");
                        insertarReservaInfantil(idReserva, ((ReservaInfantil) reservaEspecifica).getNumeroNinos());
                    }
                } else {
                    System.out.println("Error: Tipo de reserva específica no encontrada para idReserva " + idReserva);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idReserva;
    }

    public Bono obtenerBono(int idBono) {
        Bono bono = null;
        String sql = prop.getProperty("obtenerBono");

        // Verifica que la conexión esté abierta, de lo contrario, abre una nueva
        if (con == null || con.isClosed()) {
            DBConnection conexion = new DBConnection();
            con = (Connection) conexion.getConnection();
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBono);
            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    int idJugador = rs.getInt("idJugador");
                    int numeroSesion = rs.getInt("numeroSesion");
                    Date fechaCaducidad = rs.getDate("fechaCaducidad");
                    bono = new Bono(idBono, idJugador, numeroSesion, fechaCaducidad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bono;
    }
    
    public Bono crearNuevoBono(int idUsuario) {
        Bono bono = new Bono();
        bono.setIdUsuario(idUsuario);
        bono.setSesionesRestantes(5); // Inicializamos con 5 sesiones restantes
        Date fechaPrimeraReserva = new Date(); // Fecha de creación como fecha de primera reserva
        bono.setFechaCaducidad(bono.calcularFechaCaducidad(fechaPrimeraReserva));

        String sql = prop.getProperty("insertarBono");

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, bono.getSesionesRestantes());
            ps.setDate(3, new java.sql.Date(bono.getFechaCaducidad().getTime()));

            ps.executeUpdate();

            // Obtener el `idBono` generado por la base de datos y asignarlo al bono
            try (ResultSet rs = (ResultSet) ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bono.setIdBono(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bono;
    }


    public void actualizarReserva(int idReserva, Date nuevaFechaHora, int nuevaDuracionMinutos, float nuevoPrecio, float nuevoDescuento, int nuevaIdPista, Integer numeroAdultos, Integer numeroNinos) {
        String sqlActualizarReserva = prop.getProperty("actualizarReserva");
        String sqlActualizarReservaInfantil = prop.getProperty("actualizarReservaInfantil");
        String sqlActualizarReservaFamiliar = prop.getProperty("actualizarReservaFamiliar");
        String sqlActualizarReservaAdulto = prop.getProperty("actualizarReservaAdulto");

        DBConnection conexion = new DBConnection();
        try (Connection con = (Connection) conexion.getConnection()) {  // Usa try-with-resources para asegurar el cierre de la conexión

            // Actualizar la tabla principal `Reserva`
            try (PreparedStatement ps = con.prepareStatement(sqlActualizarReserva)) {
                ps.setTimestamp(1, new java.sql.Timestamp(nuevaFechaHora.getTime()));
                ps.setInt(2, nuevaDuracionMinutos);
                ps.setFloat(3, nuevoPrecio);
                ps.setFloat(4, nuevoDescuento);
                ps.setInt(5, nuevaIdPista);
                ps.setInt(6, idReserva);
                int affectedRows = ps.executeUpdate();
                System.out.println("Actualización en tabla Reserva: " + affectedRows + " filas afectadas.");
            }

            // Actualizar tabla específica según el tipo de reserva
            if (numeroNinos != null && numeroAdultos == null) {  // Infantil
                try (PreparedStatement psInfantil = con.prepareStatement(sqlActualizarReservaInfantil)) {
                    psInfantil.setInt(1, numeroNinos);
                    psInfantil.setInt(2, idReserva);
                    int affectedRowsInfantil = psInfantil.executeUpdate();
                    System.out.println("Actualización en tabla ReservaInfantil: " + affectedRowsInfantil + " filas afectadas.");
                }
            } else if (numeroNinos != null && numeroAdultos != null) {  // Familiar
                try (PreparedStatement psFamiliar = con.prepareStatement(sqlActualizarReservaFamiliar)) {
                    psFamiliar.setInt(1, numeroAdultos);
                    psFamiliar.setInt(2, numeroNinos);
                    psFamiliar.setInt(3, idReserva);
                    int affectedRowsFamiliar = psFamiliar.executeUpdate();
                    System.out.println("Actualización en tabla ReservaFamiliar: " + affectedRowsFamiliar + " filas afectadas.");
                }
            } else if (numeroAdultos != null && numeroNinos == null) {  // Adulto
                try (PreparedStatement psAdulto = con.prepareStatement(sqlActualizarReservaAdulto)) {
                    psAdulto.setInt(1, numeroAdultos);
                    psAdulto.setInt(2, idReserva);
                    int affectedRowsAdulto = psAdulto.executeUpdate();
                    System.out.println("Actualización en tabla ReservaAdulto: " + affectedRowsAdulto + " filas afectadas.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    
    public void actualizarSesionesBono(int idBono) {
        String sql = prop.getProperty("actualizarSesionesBono");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBono);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void eliminarReserva(int idReserva) {
        String sqlEliminarReserva = prop.getProperty("eliminarReserva");
        DBConnection conexion = new DBConnection();

        try (Connection con = (Connection) conexion.getConnection()) {
            // Eliminar la reserva en la tabla principal `Reserva`
            try (PreparedStatement ps = con.prepareStatement(sqlEliminarReserva)) {
                ps.setInt(1, idReserva);
                int affectedRows = ps.executeUpdate();
                System.out.println("Eliminación en tabla Reserva: " + affectedRows + " filas afectadas.");
            }

            // Llamar al método para eliminar en las tablas específicas
            eliminarReservaEspecifica(idReserva);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void eliminarReservaEspecifica(int idReserva) {
        // Consultas SQL para eliminar la reserva en las tablas específicas
        String sqlEliminarReservaInfantil = prop.getProperty("eliminarReservaInfantil");
        String sqlEliminarReservaFamiliar = prop.getProperty("eliminarReservaFamiliar");
        String sqlEliminarReservaAdulto = prop.getProperty("eliminarReservaAdulto");

        DBConnection conexion = new DBConnection();

        try (Connection con = (Connection) conexion.getConnection()) {
            // Intentar eliminar en cada tabla específica. Solo una debería coincidir.
            try (PreparedStatement psInfantil = con.prepareStatement(sqlEliminarReservaInfantil)) {
                psInfantil.setInt(1, idReserva);
                int affectedRowsInfantil = psInfantil.executeUpdate();
                System.out.println("Intento de eliminación en ReservaInfantil: " + affectedRowsInfantil + " filas afectadas.");
            }

            try (PreparedStatement psFamiliar = con.prepareStatement(sqlEliminarReservaFamiliar)) {
                psFamiliar.setInt(1, idReserva);
                int affectedRowsFamiliar = psFamiliar.executeUpdate();
                System.out.println("Intento de eliminación en ReservaFamiliar: " + affectedRowsFamiliar + " filas afectadas.");
            }

            try (PreparedStatement psAdulto = con.prepareStatement(sqlEliminarReservaAdulto)) {
                psAdulto.setInt(1, idReserva);
                int affectedRowsAdulto = psAdulto.executeUpdate();
                System.out.println("Intento de eliminación en ReservaAdulto: " + affectedRowsAdulto + " filas afectadas.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    /**
     * Realiza una reserva individual para un jugador.
     * 
     * @param jugadorDTO El jugador que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param pistaDTO La pista a reservar.
     * @param numeroAdultos El número de adultos en la reserva.
     * @param numeroNinos El número de niños en la reserva.
     * @throws IllegalArgumentException Si la cuenta del jugador no es válida.
     */
    public int hacerReservaIndividual(JugadorDTO jugadorDTO, Date fechaHora, int duracionMinutos, PistaDTO pistaDTO, int numeroAdultos, int numeroNinos) {
        if (!jugadorDTO.isCuentaActiva()) {
            throw new IllegalArgumentException("La cuenta del jugador no está activa.");
        }

        // Validar que la reserva sea para una fecha futura con al menos 6 horas de antelación
        Date ahora = new Date();
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(ahora);
        calendario.add(Calendar.HOUR, 6);
        Date limiteMinimoReserva = calendario.getTime();

        if (fechaHora.before(limiteMinimoReserva)) {
            throw new IllegalArgumentException("La reserva debe realizarse con al menos 6 horas de antelación y no puede ser en una fecha pasada.");
        }

        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);
        if (!cumpleCondicionesTipoReserva(pistaDTO, tipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + tipoReserva + "'.");
        }

        // Calcular el descuento por antigüedad si el jugador tiene más de 2 años de inscripción
        float descuentoAntiguedad = (jugadorDTO.calcularAntiguedad() > 2) ? 0.1f : 0.0f;

        ReservaFactory reservaFactory = new ReservaIndividualFactory();
        ReservaDTO reservaDTO;

        switch (tipoReserva.toLowerCase()) {
            case "infantil":
                reservaDTO = reservaFactory.crearReservaInfantil(jugadorDTO.getIdJugador(), fechaHora, duracionMinutos, pistaDTO.getIdPista(), numeroNinos);
                break;
            case "familiar":
                reservaDTO = reservaFactory.crearReservaFamiliar(jugadorDTO.getIdJugador(), fechaHora, duracionMinutos, pistaDTO.getIdPista(), numeroAdultos, numeroNinos);
                break;
            case "adulto":
                reservaDTO = reservaFactory.crearReservaAdulto(jugadorDTO.getIdJugador(), fechaHora, duracionMinutos, pistaDTO.getIdPista(), numeroAdultos);
                break;
            default:
                throw new IllegalArgumentException("Tipo de reserva no válido: " + tipoReserva);
        }

        // Aplicar el descuento calculado a la reserva
        reservaDTO.setDescuento(descuentoAntiguedad);

        // Insertar la reserva en la base de datos
        ReservasDAO reservasDAO = new ReservasDAO();
        int idReserva = reservasDAO.insertarReserva(reservaDTO);
        reservaDTO.setIdReserva(idReserva);
        return idReserva;
    }

    /**
     * Realiza una reserva utilizando un bono para un jugador.
     *
     * @param jugadorDTO El jugador que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param pistaDTO La pista a reservar.
     * @param numeroAdultos El número de adultos en la reserva.
     * @param numeroNinos El número de niños en la reserva.
     * @param bono El bono a utilizar en la reserva.
     * @param numeroSesion El número de la sesión del bono.
     * @throws IllegalArgumentException Si la cuenta del jugador no está activa o si la pista no es válida para el tipo de reserva.
     */
    public void hacerReservaBono(JugadorDTO jugadorDTO, Date fechaHora, int duracionMinutos, PistaDTO pistaDTO, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        if (!jugadorDTO.isCuentaActiva()) {
            throw new IllegalArgumentException("La cuenta del jugador no está activa.");
        }

        // Validar que la reserva sea para una fecha futura con al menos 6 horas de antelación
        Date ahora = new Date();
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(ahora);
        calendario.add(Calendar.HOUR, 6);
        Date limiteMinimoReserva = calendario.getTime();

        if (fechaHora.before(limiteMinimoReserva)) {
            throw new IllegalArgumentException("La reserva debe realizarse con al menos 6 horas de antelación y no puede ser en una fecha pasada.");
        }

        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);
        if (!cumpleCondicionesTipoReserva(pistaDTO, tipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + tipoReserva + "'.");
        }

        if (bono.getSesionesRestantes() <= 0) {
            throw new IllegalArgumentException("El bono no tiene sesiones disponibles.");
        }

        ReservaFactory reservaFactory = new ReservaBonoFactory();
        ReservaDTO reservaDTO;

        switch (tipoReserva.toLowerCase()) {
            case "infantil":
                reservaDTO = reservaFactory.crearReservaInfantil(jugadorDTO.getIdJugador(), fechaHora, duracionMinutos, pistaDTO.getIdPista(), numeroNinos, bono, numeroSesion);
                break;
            case "familiar":
                reservaDTO = reservaFactory.crearReservaFamiliar(jugadorDTO.getIdJugador(), fechaHora, duracionMinutos, pistaDTO.getIdPista(), numeroAdultos, numeroNinos, bono, numeroSesion);
                break;
            case "adulto":
                reservaDTO = reservaFactory.crearReservaAdulto(jugadorDTO.getIdJugador(), fechaHora, duracionMinutos, pistaDTO.getIdPista(), numeroAdultos, bono, numeroSesion);
                break;
            default:
                throw new IllegalArgumentException("Tipo de reserva no válido: " + tipoReserva);
        }

        int idReserva = insertarReserva(reservaDTO);
        reservaDTO.setIdReserva(idReserva);
        actualizarSesionesBono(bono.getIdBono());
    }





    /**
     * Modifica una reserva existente.
     *
     * @param reservaDTO La reserva a modificar.
     * @param pistaDTO La nueva pista para la reserva.
     * @param fechaHoraOriginal La fecha y hora original de la reserva.
     * @param nuevaFechaHora La nueva fecha y hora de la reserva.
     * @param nuevaDuracionMinutos La nueva duración de la reserva en minutos.
     * @param numeroAdultos El nuevo número de adultos en la reserva.
     * @param numeroNinos El nuevo número de niños en la reserva.
     * @param bono El bono a utilizar en la nueva reserva, si aplica.
     * @param numeroSesion El número de la sesión del bono, si aplica.
     * @throws IllegalArgumentException Si no se encuentra el jugador o la pista, si no se puede modificar la reserva, o si la pista no es válida.
     */
    public void modificarReserva(JugadorDTO jugadorDTO, PistaDTO pistaOriginal, Date fechaHoraOriginal, PistaDTO nuevaPista, Date nuevaFechaHora, int nuevaDuracionMinutos, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        // Buscar la reserva existente en la base de datos
        ReservaDTO reservaExistente = encontrarReserva(jugadorDTO.getIdJugador(), pistaOriginal.getIdPista(), fechaHoraOriginal);

        if (reservaExistente == null) {
            throw new IllegalArgumentException("No se encontró la reserva original para modificar.");
        }

        // Verificar si puede modificarse
        if (!puedeModificarseOCancelarse(reservaExistente)) {
            throw new IllegalArgumentException("No se puede modificar la reserva, ya está dentro de las 24h antes de la hora de inicio.");
        }

        // Determinar el tipo de reserva con los nuevos parámetros de adultos y niños
        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);

        // Verificar si la nueva pista cumple las condiciones para el tipo de reserva
        if (!cumpleCondicionesTipoReserva(nuevaPista, tipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + tipoReserva + "'.");
        }

        // Calcular el nuevo precio y descuento de la reserva
        float nuevoPrecio = ReservaDTO.calcularPrecio(nuevaDuracionMinutos, reservaExistente.getDescuento());
        System.out.println("ID reserva: " + reservaExistente.getIdReserva());
        // Actualizar la reserva en la base de datos con los parámetros específicos
        actualizarReserva(reservaExistente.getIdReserva(), nuevaFechaHora, nuevaDuracionMinutos, nuevoPrecio, reservaExistente.getDescuento(), nuevaPista.getIdPista(), 
                          tipoReserva.equals("familiar") || tipoReserva.equals("adulto") ? numeroAdultos : null, 
                          tipoReserva.equals("familiar") || tipoReserva.equals("infantil") ? numeroNinos : null);

        // Opcional: actualizar el número de sesión en el bono si se ha decidido consumir otra sesión
        if (bono != null) {
            actualizarSesionesBono(bono.getIdBono());
        }
    }




    /**
     * Cancela una reserva existente.
     *
     * @param jugadorDTO El jugador que cancela la reserva.
     * @param pistaDTO La pista de la reserva a cancelar.
     * @param fechaHora La fecha y hora de la reserva a cancelar.
     * @throws IllegalArgumentException Si la cuenta del jugador no está activa, si no se encuentra la reserva, o si no se puede cancelar la reserva.
     */
    public void cancelarReserva(JugadorDTO jugadorDTO, PistaDTO pistaDTO, Date fechaHora) {
        if (!jugadorDTO.isCuentaActiva()) {
            throw new IllegalArgumentException("La cuenta del jugador no está activa.");
        }

        ReservasDAO reservasDAO = new ReservasDAO();
        
        // Buscar la reserva en la base de datos
        ReservaDTO reservaDTO = reservasDAO.encontrarReserva(jugadorDTO.getIdJugador(), pistaDTO.getIdPista(), fechaHora);

        if (reservaDTO == null) {
            throw new IllegalArgumentException("Reserva no encontrada.");
        }

        // Verificar si la reserva puede cancelarse
        if (!puedeModificarseOCancelarse(reservaDTO)) {
            throw new IllegalArgumentException("No se puede cancelar la reserva, ya está dentro de las 24h antes de la hora de inicio.");
        }

        // Eliminar la reserva de la base de datos
        reservasDAO.eliminarReserva(reservaDTO.getIdReserva());
    }

    /**
     * Consulta las reservas futuras.
     *
     * @return Una lista de reservas futuras.
     */
    public List<ReservaDTO> consultarReservasFuturas() {
        List<ReservaDTO> reservasFuturas = new ArrayList<>();
        Date fechaActual = new Date();
        String sql = prop.getProperty("consultarReservasFuturas");

        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, new java.sql.Timestamp(fechaActual.getTime()));

            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                while (rs.next()) {
                    int idReserva = rs.getInt("idReserva");
                    int idJugador = rs.getInt("idJugador");
                    int idPista = rs.getInt("idPista");
                    Date fechaHora = rs.getTimestamp("fechaHora");
                    int duracionMin = rs.getInt("duracionMin");
                    float precio = rs.getFloat("precio");
                    float descuento = rs.getFloat("descuento");
                    Integer idBono = rs.getObject("idBono") != null ? rs.getInt("idBono") : null;
                    Integer numeroSesion = rs.getObject("numeroSesion") != null ? rs.getInt("numeroSesion") : null;

                    ReservaFactory reservaFactory = (idBono != null) ? new ReservaBonoFactory() : new ReservaIndividualFactory();
                    ReservaDTO reservaDTO = null;

                    // Consulta para determinar si es una ReservaFamiliar
                    String sqlFamiliar = prop.getProperty("buscarReservaFamiliar");
                    try (PreparedStatement psFamiliar = con.prepareStatement(sqlFamiliar)) {
                        psFamiliar.setInt(1, idReserva);
                        try (ResultSet rsFamiliar = (ResultSet) psFamiliar.executeQuery()) {
                            if (rsFamiliar.next()) {
                                int numeroAdultos = rsFamiliar.getInt("numAdultos");
                                int numeroNinos = rsFamiliar.getInt("numNinos");
                                reservaDTO = (idBono != null)
                                        ? reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos, obtenerBono(idBono), numeroSesion)
                                        : reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos);
                            }
                        }
                    }

                    // Consulta para determinar si es una ReservaAdulto
                    if (reservaDTO == null) { // Solo si aún no se ha asignado
                        String sqlAdulto = prop.getProperty("buscarReservaAdulto");
                        try (PreparedStatement psAdulto = con.prepareStatement(sqlAdulto)) {
                            psAdulto.setInt(1, idReserva);
                            try (ResultSet rsAdulto = (ResultSet) psAdulto.executeQuery()) {
                                if (rsAdulto.next()) {
                                    int numeroAdultos = rsAdulto.getInt("numAdultos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos);
                                }
                            }
                        }
                    }

                    // Consulta para determinar si es una ReservaInfantil
                    if (reservaDTO == null) { // Solo si aún no se ha asignado
                        String sqlInfantil = prop.getProperty("buscarReservaInfantil");
                        try (PreparedStatement psInfantil = con.prepareStatement(sqlInfantil)) {
                            psInfantil.setInt(1, idReserva);
                            try (ResultSet rsInfantil = (ResultSet) psInfantil.executeQuery()) {
                                if (rsInfantil.next()) {
                                    int numeroNinos = rsInfantil.getInt("numNinos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos);
                                }
                            }
                        }
                    }

                    if (reservaDTO == null) {
                        System.out.println("Error: tipo de reserva específica no encontrada para el idReserva " + idReserva);
                        continue;
                    }

                    // Asignar precio y descuento a la reserva
                    reservaDTO.setIdReserva(idReserva);
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);

                    reservasFuturas.add(reservaDTO);
                }
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

        return reservasFuturas;
    }



    /**
     * Consulta las reservas para un día específico y una pista específica.
     *
     * @param dia La fecha del día para consultar reservas.
     * @param idPista El ID de la pista para consultar reservas.
     * @return Una lista de reservas para el día y la pista especificados.
     */

    public List<ReservaDTO> consultarReservasPorRangosDeFechaYPista(Date fechaInicio, Date fechaFin, int idPistaConsulta) {
        List<ReservaDTO> reservasPorFecha = new ArrayList<>();
        String sql = prop.getProperty("consultarReservasPorRangoDeFechasYPista");
        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();

        // Ajustar las fechas de inicio y fin
        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(fechaInicio);
        calInicio.set(Calendar.HOUR_OF_DAY, 0);
        calInicio.set(Calendar.MINUTE, 0);
        calInicio.set(Calendar.SECOND, 0);
        calInicio.set(Calendar.MILLISECOND, 0);
        Date fechaInicioAjustada = calInicio.getTime();

        Calendar calFin = Calendar.getInstance();
        calFin.setTime(fechaFin);
        calFin.set(Calendar.HOUR_OF_DAY, 23);
        calFin.set(Calendar.MINUTE, 59);
        calFin.set(Calendar.SECOND, 59);
        calFin.set(Calendar.MILLISECOND, 999);
        Date fechaFinAjustada = calFin.getTime();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, new java.sql.Timestamp(fechaInicioAjustada.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(fechaFinAjustada.getTime()));
            ps.setInt(3, idPistaConsulta);

            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                while (rs.next()) {
                    int idReserva = rs.getInt("idReserva");
                    int idJugador = rs.getInt("idJugador");
                    int idPista = rs.getInt("idPista");
                    Date fechaHora = rs.getTimestamp("fechaHora");
                    int duracionMin = rs.getInt("duracionMin");
                    float precio = rs.getFloat("precio");
                    float descuento = rs.getFloat("descuento");
                    Integer idBono = rs.getObject("idBono") != null ? rs.getInt("idBono") : null;
                    Integer numeroSesion = rs.getObject("numeroSesion") != null ? rs.getInt("numeroSesion") : null;

                    // Crear la fábrica adecuada
                    ReservaFactory reservaFactory = (idBono != null) ? new ReservaBonoFactory() : new ReservaIndividualFactory();
                    ReservaDTO reservaDTO = null;

                    // Verificar el tipo de reserva en la base de datos
                    String sqlFamiliar = prop.getProperty("buscarReservaFamiliar");
                    try (PreparedStatement psFamiliar = con.prepareStatement(sqlFamiliar)) {
                        psFamiliar.setInt(1, idReserva);
                        try (ResultSet rsFamiliar = (ResultSet) psFamiliar.executeQuery()) {
                            if (rsFamiliar.next()) {
                                int numeroAdultos = rsFamiliar.getInt("numAdultos");
                                int numeroNinos = rsFamiliar.getInt("numNinos");
                                reservaDTO = (idBono != null)
                                        ? reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos, obtenerBono(idBono), numeroSesion)
                                        : reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos);
                            }
                        }
                    }

                    // Si no es familiar, verificar si es adulto
                    if (reservaDTO == null) {
                        String sqlAdulto = prop.getProperty("buscarReservaAdulto");
                        try (PreparedStatement psAdulto = con.prepareStatement(sqlAdulto)) {
                            psAdulto.setInt(1, idReserva);
                            try (ResultSet rsAdulto = (ResultSet) psAdulto.executeQuery()) {
                                if (rsAdulto.next()) {
                                    int numeroAdultos = rsAdulto.getInt("numAdultos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos);
                                }
                            }
                        }
                    }

                    // Si no es familiar ni adulto, verificar si es infantil
                    if (reservaDTO == null) {
                        String sqlInfantil = prop.getProperty("buscarReservaInfantil");
                        try (PreparedStatement psInfantil = con.prepareStatement(sqlInfantil)) {
                            psInfantil.setInt(1, idReserva);
                            try (ResultSet rsInfantil = (ResultSet) psInfantil.executeQuery()) {
                                if (rsInfantil.next()) {
                                    int numeroNinos = rsInfantil.getInt("numNinos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos);
                                }
                            }
                        }
                    }

                    // Verificar si la reserva fue creada, si no, continuar con la siguiente
                    if (reservaDTO == null) {
                        System.out.println("Error: tipo de reserva específica no encontrada para el idReserva " + idReserva);
                        continue;
                    }

                    // Asignar precio y descuento a la reserva
                    reservaDTO.setIdReserva(idReserva);
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);

                    // Agregar la reserva a la lista de resultados
                    reservasPorFecha.add(reservaDTO);
                }
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

        return reservasPorFecha;
    }



    /**
     * Obtiene una reserva completa usando el patrón Factory.
     *
     * @param idReserva El ID de la reserva.
     * @return La instancia completa de ReservaDTO según el tipo (Infantil, Familiar o Adulto).
     */
    public ReservaDTO obtenerReservaCompleta(int idReserva) {
        String sqlBaseReserva = prop.getProperty("buscarReservaBase");
        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();
        
        try (PreparedStatement psBase = con.prepareStatement(sqlBaseReserva)) {
            psBase.setInt(1, idReserva);

            try (ResultSet rsBase = (ResultSet) psBase.executeQuery()) {
                if (rsBase.next()) {
                    // Datos básicos de la reserva
                    int idJugador = rsBase.getInt("idJugador");
                    Date fechaHora = rsBase.getTimestamp("fechaHora");
                    int duracionMin = rsBase.getInt("duracionMin");
                    int idPista = rsBase.getInt("idPista");
                    float precio = rsBase.getFloat("precio");
                    float descuento = rsBase.getFloat("descuento");
                    Integer idBono = rsBase.getObject("idBono") != null ? rsBase.getInt("idBono") : null;
                    Integer numeroSesion = rsBase.getObject("numeroSesion") != null ? rsBase.getInt("numeroSesion") : null;

                    // Seleccionar la fábrica según el tipo de reserva (con o sin bono)
                    ReservaFactory reservaFactory = (idBono != null) ? new ReservaBonoFactory() : new ReservaIndividualFactory();
                    ReservaDTO reservaDTO = null;

                    // Verificar si es una ReservaFamiliar
                    String sqlFamiliar = prop.getProperty("buscarReservaFamiliar");
                    try (PreparedStatement psFamiliar = con.prepareStatement(sqlFamiliar)) {
                        psFamiliar.setInt(1, idReserva);
                        try (ResultSet rsFamiliar = (ResultSet) psFamiliar.executeQuery()) {
                            if (rsFamiliar.next()) {
                                int numeroAdultos = rsFamiliar.getInt("numAdultos");
                                int numeroNinos = rsFamiliar.getInt("numNinos");
                                reservaDTO = (idBono != null)
                                        ? reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos, obtenerBono(idBono), numeroSesion)
                                        : reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos);
                            }
                        }
                    }

                    // Verificar si es una ReservaAdulto, si no se encontró como familiar
                    if (reservaDTO == null) {
                        String sqlAdulto = prop.getProperty("buscarReservaAdulto");
                        try (PreparedStatement psAdulto = con.prepareStatement(sqlAdulto)) {
                            psAdulto.setInt(1, idReserva);
                            try (ResultSet rsAdulto = (ResultSet) psAdulto.executeQuery()) {
                                if (rsAdulto.next()) {
                                    int numeroAdultos = rsAdulto.getInt("numAdultos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos);
                                }
                            }
                        }
                    }

                    // Verificar si es una ReservaInfantil, si no se encontró como familiar o adulto
                    if (reservaDTO == null) {
                        String sqlInfantil = prop.getProperty("buscarReservaInfantil");
                        try (PreparedStatement psInfantil = con.prepareStatement(sqlInfantil)) {
                            psInfantil.setInt(1, idReserva);
                            try (ResultSet rsInfantil = (ResultSet) psInfantil.executeQuery()) {
                                if (rsInfantil.next()) {
                                    int numeroNinos = rsInfantil.getInt("numNinos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos);
                                }
                            }
                        }
                    }

                    // Si no se encontró ningún tipo de reserva específico, lanzar error
                    if (reservaDTO == null) {
                        System.out.println("Error: tipo de reserva específica no encontrada para el idReserva " + idReserva);
                        return null;
                    }

                    // Asignar precio y descuento a la reserva
                    reservaDTO.setIdReserva(idReserva);
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);

                    return reservaDTO;
                }
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

        System.out.println("Error: reserva no encontrada en la base de datos.");
        return null;
    }

    /**
     * Encuentra una reserva completa en función del idJugador, idPista y fechaHora.
     *
     * @param idJugador El ID del jugador.
     * @param idPista El ID de la pista.
     * @param fechaHora La fecha y hora de la reserva.
     * @return La instancia completa de ReservaDTO según el tipo (Infantil, Familiar o Adulto).
     */
    public ReservaDTO encontrarReserva(int idJugador, int idPista, Date fechaHora) {
        ReservaDTO reservaDTO = null;
        String sqlBaseReserva = prop.getProperty("encontrarReserva");

        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sqlBaseReserva)) {
            ps.setInt(1, idJugador);
            ps.setInt(2, idPista);
            ps.setTimestamp(3, new java.sql.Timestamp(fechaHora.getTime()));

            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    // Obtener datos comunes de la reserva
                    int idReserva = rs.getInt("idReserva");
                    int duracionMin = rs.getInt("duracionMin");
                    float precio = rs.getFloat("precio");
                    float descuento = rs.getFloat("descuento");
                    Integer idBono = rs.getObject("idBono") != null ? rs.getInt("idBono") : null;
                    Integer numeroSesion = (idBono != null && rs.getObject("numeroSesion") != null) ? rs.getInt("numeroSesion") : null;

                    // Crear la instancia de la fábrica adecuada
                    ReservaFactory reservaFactory = (idBono != null) ? new ReservaBonoFactory() : new ReservaIndividualFactory();

                    // Intentar identificar la reserva como familiar
                    String sqlFamiliar = prop.getProperty("buscarReservaFamiliar");
                    try (PreparedStatement psFamiliar = con.prepareStatement(sqlFamiliar)) {
                        psFamiliar.setInt(1, idReserva);
                        try (ResultSet rsFamiliar = (ResultSet) psFamiliar.executeQuery()) {
                            if (rsFamiliar.next()) {
                                int numeroAdultos = rsFamiliar.getInt("numAdultos");
                                int numeroNinos = rsFamiliar.getInt("numNinos");
                                reservaDTO = (idBono != null)
                                        ? reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos, obtenerBono(idBono), numeroSesion)
                                        : reservaFactory.crearReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos);
                            }
                        }
                    }

                    // Si no es familiar, intentar identificar como adulto
                    if (reservaDTO == null) {
                        String sqlAdulto = prop.getProperty("buscarReservaAdulto");
                        try (PreparedStatement psAdulto = con.prepareStatement(sqlAdulto)) {
                            psAdulto.setInt(1, idReserva);
                            try (ResultSet rsAdulto = (ResultSet) psAdulto.executeQuery()) {
                                if (rsAdulto.next()) {
                                    int numeroAdultos = rsAdulto.getInt("numAdultos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numeroAdultos);
                                }
                            }
                        }
                    }

                    // Si no es ni familiar ni adulto, intentar identificar como infantil
                    if (reservaDTO == null) {
                        String sqlInfantil = prop.getProperty("buscarReservaInfantil");
                        try (PreparedStatement psInfantil = con.prepareStatement(sqlInfantil)) {
                            psInfantil.setInt(1, idReserva);
                            try (ResultSet rsInfantil = (ResultSet) psInfantil.executeQuery()) {
                                if (rsInfantil.next()) {
                                    int numeroNinos = rsInfantil.getInt("numNinos");
                                    reservaDTO = (idBono != null)
                                            ? reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos, obtenerBono(idBono), numeroSesion)
                                            : reservaFactory.crearReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numeroNinos);
                                }
                            }
                        }
                    }

                    // Verificar si se creó la reserva; si no, lanzar un error
                    if (reservaDTO == null) {
                        System.out.println("Error: tipo de reserva específica no encontrada para el idReserva " + idReserva);
                        return null;
                    }

                    reservaDTO.setIdReserva(idReserva);
                    // Asignar precio y descuento a la reserva
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);
                }
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

        return reservaDTO;
    }


    /**
     * Verifica si la pista cumple las condiciones para el tipo de reserva.
     *
     * @param pistaDTO La pista a verificar.
     * @param tipoReserva El tipo de reserva a verificar.
     * @return true si la pista cumple las condiciones; false en caso contrario.
     */
    private boolean cumpleCondicionesTipoReserva(PistaDTO pistaDTO, String tipoReserva) {
        switch (tipoReserva.toLowerCase()) {
            case "infantil":
                return pistaDTO.getPista() == TamanoPista.MINIBASKET;
            case "familiar":
                return pistaDTO.getPista() == TamanoPista.MINIBASKET || pistaDTO.getPista() == TamanoPista._3VS3;
            case "adulto":
                return pistaDTO.getPista() == TamanoPista.ADULTOS;
            default:
                return false;
        }
    }

    /**
     * Determina el tipo de reserva según el número de adultos y niños.
     *
     * @param numeroAdultos El número de adultos.
     * @param numeroNinos El número de niños.
     * @return El tipo de reserva (infantil, familiar o adulto).
     * @throws IllegalArgumentException Si no se proporciona un número válido de adultos o niños.
     */
    private String determinarTipoReserva(int numeroAdultos, int numeroNinos) {
        if (numeroAdultos > 0 && numeroNinos > 0) {
            return "familiar";
        } else if (numeroAdultos > 0) {
            return "adulto";
        } else if (numeroNinos > 0) {
            return "infantil";
        } else {
            throw new IllegalArgumentException("No se ha proporcionado un número válido de adultos o niños.");
        }
    }

    /**
     * Busca un jugador por su correo electrónico.
     *
     * @param correoElectronico El correo electrónico del jugador.
     * @return El jugador encontrado, o null si no se encuentra.
     */
    public JugadorDTO buscarJugadorPorCorreo(String correoElectronico) {
        JugadoresDAO jugadoresDAO = new JugadoresDAO();
        return jugadoresDAO.buscarJugadorPorCorreo(correoElectronico);
    }

    /**
     * Lista las pistas disponibles para un tipo de reserva específico.
     *
     * @param tipoReserva El tipo de reserva (infantil, familiar, adulto).
     * @return Una lista de pistas disponibles para el tipo de reserva dado.
     */
    public List<PistaDTO> listarPistasDisponibles(String tipoReserva) {
        PistasDAO pistasDAO = new PistasDAO(); // Instanciación de PistasDAO
        try {
            return pistasDAO.listarPistasDisponibles(tipoReserva); // Llamada al método en PistasDAO con el tipo de reserva
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Retorna una lista vacía en caso de excepción
        }
    }

    /**
     * Busca una pista por su ID.
     *
     * @param idPista El ID de la pista a buscar.
     * @return La pista encontrada, o null si no se encuentra.
     */
    public PistaDTO buscarPistaPorId(int idPista) {
        try {
            PistasDAO pistasDAO = new PistasDAO(); // Instanciación directa de PistasDAO
            return pistasDAO.buscarPistaPorId(idPista); // Llamada al método no estático
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si se puede modificar o cancelar una reserva.
     *
     * @param reservaDTO La reserva a verificar.
     * @return true si se puede modificar o cancelar; false en caso contrario.
     */
    private boolean puedeModificarseOCancelarse(ReservaDTO reservaDTO) {
        long MILISEGUNDOS_EN_24_HORAS = 24 * 60 * 60 * 1000;
        long diferenciaTiempo = reservaDTO.getFechaHora().getTime() - new Date().getTime();
        return diferenciaTiempo > MILISEGUNDOS_EN_24_HORAS;
    }
}
