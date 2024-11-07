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
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

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

        this.reservaDTOs.add(reservaDTO);
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
    public void modificarReserva(ReservaDTO reservaDTO, PistaDTO pistaDTO, Date fechaHoraOriginal, Date nuevaFechaHora, int nuevaDuracionMinutos, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        JugadoresDAO jugadoresDAO = JugadoresDAO.getInstance();
        PistasDAO pistasDAO = PistasDAO.getInstance();

        JugadorDTO jugadorDTO = jugadoresDAO.buscarJugadorPorId(reservaDTO.getIdUsuario());
        PistaDTO pistaOriginal = pistasDAO.buscarPistaPorId(reservaDTO.getIdPista());

        if (jugadorDTO == null || pistaOriginal == null) {
            throw new IllegalArgumentException("No se pudo encontrar el jugador o la pista asociados a la reserva.");
        }

        if (!puedeModificarseOCancelarse(reservaDTO)) {
            throw new IllegalArgumentException("No se puede modificar la reserva, ya está dentro de las 24h antes de la hora de inicio.");
        }

        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);
        if (!cumpleCondicionesTipoReserva(pistaDTO, tipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + tipoReserva + "'.");
        }

        cancelarReserva(jugadorDTO, pistaOriginal, fechaHoraOriginal);

        ReservaDTO nuevaReserva;

        if (bono == null) {  
            nuevaReserva = ReservaGeneralFactory.crearReserva(
                tipoReserva,  
                jugadorDTO.getIdJugador(),  
                nuevaFechaHora,  
                nuevaDuracionMinutos,  
                pistaDTO.getIdPista(),  
                numeroAdultos,  
                numeroNinos,  
                jugadorDTO.calcularAntiguedad() > 2,  
                null,  
                0  
            );
        } else {  
            nuevaReserva = ReservaGeneralFactory.crearReserva(
                tipoReserva,  
                jugadorDTO.getIdJugador(),  
                nuevaFechaHora,  
                nuevaDuracionMinutos,  
                pistaDTO.getIdPista(),  
                numeroAdultos,  
                numeroNinos,  
                false,  
                bono,  
                numeroSesion  
            );
        }

        this.reservaDTOs.add(nuevaReserva);
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
    	
    	ReservaDTO reservaDTO = encontrarReserva(jugadorDTO.getIdJugador(), pistaDTO.getIdPista(), fechaHora);

        if (reservaDTO == null) {
            throw new IllegalArgumentException("Reserva no encontrada.");
        }

        if (!puedeModificarseOCancelarse(reservaDTO)) {
            throw new IllegalArgumentException("No se puede cancelar la reserva, ya está dentro de las 24h antes de la hora de inicio.");
        }

        this.reservaDTOs.remove(reservaDTO);
    }

    /**
     * Consulta las reservas futuras.
     *
     * @return Una lista de reservas futuras.
     */
    public List<ReservaDTO> consultarReservasFuturas() {
        Date fechaActual = new Date();
        return this.reservaDTOs.stream()
                .filter(reserva -> reserva.getFechaHora().after(fechaActual))
                .collect(Collectors.toList());
    }

    /**
     * Consulta las reservas para un día específico y una pista específica.
     *
     * @param dia La fecha del día para consultar reservas.
     * @param idPista El ID de la pista para consultar reservas.
     * @return Una lista de reservas para el día y la pista especificados.
     */
    public List<ReservaDTO> consultarReservasPorDiaYPista(Date dia, int idPista) {
        return this.reservaDTOs.stream()
                .filter(reserva -> esMismaFechaSinHora(reserva.getFechaHora(), dia) && reserva.getIdPista() == idPista)
                .collect(Collectors.toList());
    }

    /**
     * Encuentra una reserva por ID de usuario, ID de pista y fecha.
     *
     * @param idUsuario El ID del usuario que realizó la reserva.
     * @param idPista El ID de la pista de la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @return La reserva encontrada, o null si no se encuentra.
     */
    public ReservaDTO encontrarReserva(int idUsuario, int idPista, Date fechaHora) {
        for (ReservaDTO reservaDTO : reservaDTOs) {
            if (reservaDTO.getIdUsuario() == idUsuario && 
                reservaDTO.getIdPista() == idPista && 
                esMismaFecha(reservaDTO.getFechaHora(), fechaHora)) {
                return reservaDTO;
            }
        }
        return null;
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

    /**
     * Verifica si dos fechas son del mismo día sin considerar la hora.
     *
     * @param fecha1 La primera fecha.
     * @param fecha2 La segunda fecha.
     * @return true si son el mismo día; false en caso contrario.
     */
    private boolean esMismaFechaSinHora(Date fecha1, Date fecha2) {
        LocalDate localDate1 = convertirADia(fecha1);  
        LocalDate localDate2 = convertirADia(fecha2);
        return localDate1.equals(localDate2);  
    }

    /**
     * Convierte una fecha a un objeto LocalDate.
     *
     * @param fecha La fecha a convertir.
     * @return El LocalDate correspondiente a la fecha.
     */
    private LocalDate convertirADia(Date fecha) {
        return fecha.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
    }

    /**
     * Verifica si dos fechas son iguales (incluyendo hora).
     *
     * @param fecha1 La primera fecha.
     * @param fecha2 La segunda fecha.
     * @return true si son iguales; false en caso contrario.
     */
    private boolean esMismaFecha(Date fecha1, Date fecha2) {
        LocalDateTime localDateTime1 = convertirAFechaYHora(fecha1);
        LocalDateTime localDateTime2 = convertirAFechaYHora(fecha2);
        return localDateTime1.equals(localDateTime2);
    }

    /**
     * Convierte una fecha a un objeto LocalDateTime.
     *
     * @param fecha La fecha a convertir.
     * @return El LocalDateTime correspondiente a la fecha.
     */
    private LocalDateTime convertirAFechaYHora(Date fecha) {
        return fecha.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
    }
}
