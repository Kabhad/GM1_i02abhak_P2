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
        return JugadoresDAO.getInstance().buscarJugadorPorId(idJugador);
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
                if (reservaDTO instanceof ReservaFamiliar) {
                    insertarReservaFamiliar(idReserva, ((ReservaFamiliar) reservaDTO).getNumeroAdultos(), ((ReservaFamiliar) reservaDTO).getNumeroNinos());
                } else if (reservaDTO instanceof ReservaAdulto) {
                    insertarReservaAdulto(idReserva, ((ReservaAdulto) reservaDTO).getNumeroAdultos());
                } else if (reservaDTO instanceof ReservaInfantil) {
                    insertarReservaInfantil(idReserva, ((ReservaInfantil) reservaDTO).getNumeroNinos());
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

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBono);
            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    int idJugador = rs.getInt("idJugador");
                    int sesionesRestantes = rs.getInt("sesionesRestantes");
                    Date fechaCaducidad = rs.getDate("fechaCaducidad");
                    bono = new Bono(idBono, idJugador, sesionesRestantes, fechaCaducidad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bono;
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
        String sql = prop.getProperty("eliminarReserva");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.executeUpdate();
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

        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);
        if (!cumpleCondicionesTipoReserva(pistaDTO, tipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + tipoReserva + "'.");
        }

        boolean tieneAntiguedad = jugadorDTO.calcularAntiguedad() > 2;

        ReservaDTO reservaDTO = ReservaGeneralFactory.crearReserva(
                tipoReserva,
                jugadorDTO.getIdJugador(),
                fechaHora,
                duracionMinutos,
                pistaDTO.getIdPista(),
                numeroAdultos,
                numeroNinos,
                tieneAntiguedad,
                null,
                0
        );

        // Inserta la reserva en la base de datos
        // Crea una instancia de ReservasDAO para llamar a insertarReserva
        ReservasDAO reservasDAO = new ReservasDAO();
        int idReserva = reservasDAO.insertarReserva(reservaDTO); // Llama al método no estático
        reservaDTO.setIdReserva(idReserva); // Asigna el idReserva después de la inserción
        return 1;
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

        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);
        if (!cumpleCondicionesTipoReserva(pistaDTO, tipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + tipoReserva + "'.");
        }

        // Validar que el bono tenga sesiones restantes
        if (bono.getSesionesRestantes() <= 0) {
            throw new IllegalArgumentException("El bono no tiene sesiones disponibles.");
        }

        // Crear el objeto de reserva
        ReservaDTO reservaDTO = ReservaGeneralFactory.crearReserva(
            tipoReserva,
            jugadorDTO.getIdJugador(),
            fechaHora,
            duracionMinutos,
            pistaDTO.getIdPista(),
            numeroAdultos,
            numeroNinos,
            false,
            bono,
            numeroSesion
        );

        // Insertar la reserva en la base de datos
        int idReserva = insertarReserva(reservaDTO);
        reservaDTO.setIdReserva(idReserva);

        // Restar una sesión del bono
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

        // Cancelar la reserva original en la base de datos
        cancelarReserva(jugadorDTO, pistaOriginal, fechaHoraOriginal);

        // Crear una nueva reserva con los parámetros actualizados sin asignar reserva específica aún
        ReservaDTO nuevaReserva;
        if (bono == null) {
            nuevaReserva = new ReservaIndividual(jugadorDTO.getIdJugador(), nuevaFechaHora, nuevaDuracionMinutos, nuevaPista.getIdPista(), null);
        } else {
            nuevaReserva = new ReservaBono(jugadorDTO.getIdJugador(), nuevaFechaHora, nuevaDuracionMinutos, nuevaPista.getIdPista(), bono, numeroSesion, null);
        }

        // Insertar la nueva reserva en la base de datos y obtener su ID
        int idNuevaReserva = insertarReserva(nuevaReserva);
        nuevaReserva.setIdReserva(idNuevaReserva);

        // Asignar la reserva específica usando el ID de la nueva reserva
        ReservaDTO reservaEspecifica = obtenerReservaEspecifica(idNuevaReserva);
        if (nuevaReserva instanceof ReservaBono) {
            ((ReservaBono) nuevaReserva).setReservaEspecifica(reservaEspecifica);
        } else if (nuevaReserva instanceof ReservaIndividual) {
            ((ReservaIndividual) nuevaReserva).setReservaEspecifica(reservaEspecifica);
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
                    int idJugador = rs.getInt("idJugador");  // Asegúrate de que esta columna existe
                    int idPista = rs.getInt("idPista");
                    Date fechaHora = rs.getTimestamp("fechaHora");
                    int duracionMin = rs.getInt("duracionMin");
                    float precio = rs.getFloat("precio");
                    float descuento = rs.getFloat("descuento");
                    Integer idBono = (Integer) rs.getObject("idBono");

                    ReservaDTO reservaDTO;
                    if (idBono != null) {
                        Bono bono = obtenerBono(idBono);
                        ReservaDTO reservaEspecifica = obtenerReservaEspecifica(idReserva);
                        
                        // Calcular el numeroSesion basado en sesionesRestantes
                        int numeroSesion = 5 - bono.getSesionesRestantes();
                        
                        if (reservaEspecifica == null) {
                            System.out.println("Error: reserva específica no encontrada.");
                            continue;  // Saltar esta iteración si no se encuentra la reserva específica
                        }
                        reservaDTO = new ReservaBono(idJugador, fechaHora, duracionMin, idPista, bono, numeroSesion, reservaEspecifica);
                    } else {
                        reservaDTO = new ReservaIndividual(idJugador, fechaHora, duracionMin, idPista, obtenerReservaEspecifica(idReserva));
                    }

                    reservaDTO.setIdReserva(idReserva);
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);

                    reservasFuturas.add(reservaDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    public List<ReservaDTO> consultarReservasPorDiaYPista(Date dia, int idPista) {
        List<ReservaDTO> reservasPorDiaYPista = new ArrayList<>();
        String sql = prop.getProperty("consultarReservasPorDiaYPista");
        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();

        try (
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Definir las fechas de inicio y fin del día especificado para la consulta
            ps.setDate(1, new java.sql.Date(dia.getTime()));
            ps.setDate(2, new java.sql.Date(dia.getTime()));
            ps.setInt(3, idPista);

            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                while (rs.next()) {
                    int idReserva = rs.getInt("idReserva");
                    int idJugador = rs.getInt("idJugador");
                    Date fechaHora = rs.getTimestamp("fechaHora");
                    int duracionMin = rs.getInt("duracionMin");
                    float precio = rs.getFloat("precio");
                    float descuento = rs.getFloat("descuento");
                    Integer idBono = rs.getObject("idBono", Integer.class);

                    ReservaDTO reserva = (idBono != null)
                        ? new ReservaBono(idJugador, fechaHora, duracionMin, idPista, obtenerBono(idBono), rs.getInt("numeroSesion"), obtenerReservaEspecifica(idReserva))
                        : new ReservaIndividual(idJugador, fechaHora, duracionMin, idPista, obtenerReservaEspecifica(idReserva));
                    
                    reserva.setPrecio(precio);
                    reserva.setDescuento(descuento);

                    reservasPorDiaYPista.add(reserva);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservasPorDiaYPista;
    }


    /**
     * Encuentra una reserva por ID de usuario, ID de pista y fecha.
     *
     * @param idJugador El ID del usuario que realizó la reserva.
     * @param idPista El ID de la pista de la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @return La reserva encontrada, o null si no se encuentra.
     */
    
    private ReservaDTO obtenerReservaEspecifica(int idReserva) {
        // Consulta básica para obtener los datos comunes de la reserva
        String sqlBaseReserva = "SELECT idJugador, fechaHora, duracionMin, idPista, precio, descuento FROM Reserva WHERE idReserva = ?";
        
        try (PreparedStatement psBase = con.prepareStatement(sqlBaseReserva)) {
            psBase.setInt(1, idReserva);
            
            try (ResultSet rsBase = (ResultSet) psBase.executeQuery()) {
                if (rsBase.next()) {
                    int idJugador = rsBase.getInt("idJugador");
                    Date fechaHora = rsBase.getDate("fechaHora");
                    int duracionMin = rsBase.getInt("duracionMin");
                    int idPista = rsBase.getInt("idPista");
                    float precio = rsBase.getFloat("precio");
                    float descuento = rsBase.getFloat("descuento");

                    // Verificar si es una reserva familiar
                    String sqlFamiliar = prop.getProperty("buscarReservaFamiliar");
                    try (PreparedStatement psFamiliar = con.prepareStatement(sqlFamiliar)) {
                        psFamiliar.setInt(1, idReserva);
                        try (ResultSet rsFamiliar = (ResultSet) psFamiliar.executeQuery()) {
                            if (rsFamiliar.next()) {
                                int numAdultos = rsFamiliar.getInt("numAdultos");
                                int numNinos = rsFamiliar.getInt("numNinos");
                                ReservaFamiliar reservaFamiliar = new ReservaFamiliar(idJugador, fechaHora, duracionMin, idPista, numAdultos, numNinos);
                                reservaFamiliar.setPrecio(precio);
                                reservaFamiliar.setDescuento(descuento);
                                return reservaFamiliar;
                            }
                        }
                    }

                    // Verificar si es una reserva adulto
                    String sqlAdulto = prop.getProperty("buscarReservaAdulto");
                    try (PreparedStatement psAdulto = con.prepareStatement(sqlAdulto)) {
                        psAdulto.setInt(1, idReserva);
                        try (ResultSet rsAdulto = (ResultSet) psAdulto.executeQuery()) {
                            if (rsAdulto.next()) {
                                int numAdultos = rsAdulto.getInt("numAdultos");
                                ReservaAdulto reservaAdulto = new ReservaAdulto(idJugador, fechaHora, duracionMin, idPista, numAdultos);
                                reservaAdulto.setPrecio(precio);
                                reservaAdulto.setDescuento(descuento);
                                return reservaAdulto;
                            }
                        }
                    }

                    // Verificar si es una reserva infantil
                    String sqlInfantil = prop.getProperty("buscarReservaInfantil");
                    try (PreparedStatement psInfantil = con.prepareStatement(sqlInfantil)) {
                        psInfantil.setInt(1, idReserva);
                        try (ResultSet rsInfantil = (ResultSet) psInfantil.executeQuery()) {
                            if (rsInfantil.next()) {
                                int numNinos = rsInfantil.getInt("numNinos");
                                ReservaInfantil reservaInfantil = new ReservaInfantil(idJugador, fechaHora, duracionMin, idPista, numNinos);
                                reservaInfantil.setPrecio(precio);
                                reservaInfantil.setDescuento(descuento);
                                return reservaInfantil;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Error: reserva específica no encontrada.");
        return null;
    }



    
    public ReservaDTO encontrarReserva(int idJugador, int idPista, Date fechaHora) {
        ReservaDTO reservaDTO = null;
        String sql = prop.getProperty("encontrarReserva");

        // Obtener conexión a la base de datos
        DBConnection conexion = new DBConnection();
        con = (Connection) conexion.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            ps.setInt(2, idPista);
            ps.setTimestamp(3, new java.sql.Timestamp(fechaHora.getTime()));

            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    int idReserva = rs.getInt("idReserva");
                    int duracionMin = rs.getInt("duracionMin");
                    float precio = rs.getFloat("precio");
                    float descuento = rs.getFloat("descuento");
                    Integer idBono = rs.getObject("idBono", Integer.class);

                    // Determinar si es una ReservaBono o una ReservaIndividual
                    if (idBono != null) {
                        Bono bono = obtenerBono(idBono);
                        reservaDTO = new ReservaBono(idJugador, fechaHora, duracionMin, idPista, bono, rs.getInt("numeroSesion"), obtenerReservaEspecifica(idReserva));
                    } else {
                        reservaDTO = new ReservaIndividual(idJugador, fechaHora, duracionMin, idPista, obtenerReservaEspecifica(idReserva));
                    }
                    
                    // Asignar precio y descuento al objeto de reserva creado
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        return JugadoresDAO.getInstance().buscarJugadorPorCorreo(correoElectronico);
    }

    /**
     * Lista las pistas disponibles.
     *
     * @return Una lista de pistas disponibles.
     */
    public List<PistaDTO> listarPistasDisponibles() {
        return PistasDAO.getInstance().buscarPistasDisponibles();
    }

    /**
     * Busca una pista por su ID.
     *
     * @param idPista El ID de la pista a buscar.
     * @return La pista encontrada, o null si no se encuentra.
     */
    public static PistaDTO buscarPistaPorId(int idPista) {
        return PistasDAO.getInstance().buscarPistaPorId(idPista);
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
