package es.uco.pw.data.dao;

import es.uco.pw.business.jugador.JugadorDTO;
import es.uco.pw.business.pista.PistaDTO;
import es.uco.pw.business.pista.TamanoPista;
import es.uco.pw.business.reserva.*;
import es.uco.pw.data.common.DBConnection;

import java.util.*;
import java.io.*;

import java.sql.*;
import java.util.Date;

/**
 * Clase que gestiona las reservas de pistas de baloncesto, incluyendo la carga y
 * almacenamiento de reservas en ficheros, así como la gestión de jugadores y pistas.
 */
public class ReservasDAO {
	/**
	 * Conexión actual con la base de datos.
	 */
	private Connection con;

	/**
	 * Propiedades con las consultas SQL necesarias para el funcionamiento del DAO.
	 */
	private Properties prop;


    /**
     * Constructor que inicializa la conexión a la base de datos y carga las propiedades SQL.
     */
    public ReservasDAO() {
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
     * Busca un jugador por su ID utilizando JugadoresDAO.
     *
     * @param idJugador El ID del jugador a buscar.
     * @return El jugador encontrado o null si no se encuentra.
     */
    public static JugadorDTO buscarJugadorPorId(int idJugador) {
        JugadoresDAO jugadoresDAO = new JugadoresDAO();
        return jugadoresDAO.buscarJugadorPorId(idJugador);
    }

    /**
     * Inserta una reserva familiar en la base de datos.
     *
     * @param idReserva     El ID de la reserva.
     * @param numeroAdultos El número de adultos en la reserva.
     * @param numeroNinos   El número de niños en la reserva.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    private void insertarReservaFamiliar(int idReserva, int numeroAdultos, int numeroNinos) throws SQLException {
        String sql = prop.getProperty("insertarReservaFamiliar");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, numeroAdultos);
            ps.setInt(3, numeroNinos);
            ps.executeUpdate();
        }
    }

    /**
     * Inserta una reserva de adultos en la base de datos.
     *
     * @param idReserva     El ID de la reserva.
     * @param numeroAdultos El número de adultos en la reserva.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    private void insertarReservaAdulto(int idReserva, int numeroAdultos) throws SQLException {
        String sql = prop.getProperty("insertarReservaAdulto");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, numeroAdultos);
            ps.executeUpdate();
        }
    }

    /**
     * Inserta una reserva infantil en la base de datos.
     *
     * @param idReserva   El ID de la reserva.
     * @param numeroNinos El número de niños en la reserva.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    private void insertarReservaInfantil(int idReserva, int numeroNinos) throws SQLException {
        String sql = prop.getProperty("insertarReservaInfantil");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, numeroNinos);
            ps.executeUpdate();
        }
    }
    
    /**
     * Inserta una reserva específica en la base de datos según su tipo.
     *
     * @param tipoReserva   El tipo de reserva (familiar, adulto o infantil).
     * @param idReserva     El ID de la reserva.
     * @param numeroAdultos El número de adultos en la reserva (opcional).
     * @param numeroNinos   El número de niños en la reserva (opcional).
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     * @throws IllegalArgumentException Si el tipo de reserva no es válido.
     */
    public void insertarReservaEspecifica(String tipoReserva, int idReserva, Integer numeroAdultos, Integer numeroNinos) throws SQLException {
        if (con == null || con.isClosed()) {
            DBConnection conexion = new DBConnection();
            con = (Connection) conexion.getConnection(); // Inicializar conexión global
        }
        switch (tipoReserva.toLowerCase()) {
            case "familiar":
                insertarReservaFamiliar(idReserva, numeroAdultos, numeroNinos);
                break;
            case "adulto":
                insertarReservaAdulto(idReserva, numeroAdultos);
                break;
            case "infantil":
                insertarReservaInfantil(idReserva, numeroNinos);
                break;
            default:
                throw new IllegalArgumentException("Tipo de reserva no válido: " + tipoReserva);
        }
    }


    /**
     * Inserta una nueva reserva en la base de datos.
     *
     * @param reservaDTO La reserva a insertar.
     * @return El ID de la reserva generada, o -1 si falla.
     */
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

            // Obtener el ID de reserva generado
            try (ResultSet rs = (ResultSet) ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idReserva = rs.getInt(1);
                }
            }

            // Insertar en la tabla específica según el tipo de reserva
            if (idReserva != -1) {
                ReservaDTO reservaEspecifica = null;
                if (reservaDTO instanceof ReservaIndividual) {
                    reservaEspecifica = ((ReservaIndividual) reservaDTO).getReservaEspecifica();
                } else if (reservaDTO instanceof ReservaBono) {
                    reservaEspecifica = ((ReservaBono) reservaDTO).getReservaEspecifica();
                }

                if (reservaEspecifica != null) {
                    if (reservaEspecifica instanceof ReservaFamiliar) {
                        insertarReservaFamiliar(idReserva, ((ReservaFamiliar) reservaEspecifica).getNumeroAdultos(), ((ReservaFamiliar) reservaEspecifica).getNumeroNinos());
                    } else if (reservaEspecifica instanceof ReservaAdulto) {
                        insertarReservaAdulto(idReserva, ((ReservaAdulto) reservaEspecifica).getNumeroAdultos());
                    } else if (reservaEspecifica instanceof ReservaInfantil) {
                        insertarReservaInfantil(idReserva, ((ReservaInfantil) reservaEspecifica).getNumeroNinos());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idReserva;
    }

    /**
     * Obtiene un bono por su ID.
     *
	 * @param idBono El ID del bono a obtener.
	 * @return El bono obtenido o null si no se encuentra.
     */
    public Bono obtenerBono(int idBono) {
        Bono bono = null;
        String sql = prop.getProperty("obtenerBono");

        try {
            if (con == null || con.isClosed()) {
                DBConnection conexion = new DBConnection();
                con = (Connection) conexion.getConnection();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al verificar o reestablecer la conexión con la base de datos.", e);
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

    /**
     * Crea un nuevo bono para un jugador.
     *
     * @param idUsuario El ID del usuario que recibirá el bono.
     * @return El bono creado.
     */
    public Bono crearNuevoBono(int idUsuario) {
        Bono bono = new Bono();
        bono.setIdUsuario(idUsuario);
        bono.setSesionesRestantes(5); // Inicializamos con 5 sesiones restantes

        Date fechaPrimeraReserva = new Date(); // Fecha de creación como fecha de la primera reserva
        bono.setFechaCaducidad(bono.calcularFechaCaducidad(fechaPrimeraReserva)); // Calcular la fecha de caducidad

        String sql = prop.getProperty("insertarBono");

        // Abrir conexión
        DBConnection conexion = new DBConnection();
        Connection con = (Connection) conexion.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Configurar los parámetros de la consulta
            ps.setInt(1, idUsuario); // Asignar el idUsuario (idJugador)
            ps.setInt(2, 0); // Inicializar numeroSesion a 0
            ps.setDate(3, new java.sql.Date(bono.getFechaCaducidad().getTime())); // Fecha de caducidad

            ps.executeUpdate();

            // Obtener el `idBono` generado automáticamente por la base de datos
            try (ResultSet rs = (ResultSet) ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bono.setIdBono(rs.getInt(1)); // Asignar el idBono generado automáticamente
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar conexión
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return bono;
    }




    
    
    /**
     * Actualiza una reserva en la base de datos con nueva información, incluyendo la fecha, duración, precio, 
     * descuento, y tipo de reserva (infantil, familiar o adulto).
     *
     * @param idReserva             El ID de la reserva a actualizar.
     * @param nuevaFechaHora        La nueva fecha y hora de la reserva.
     * @param nuevaDuracionMinutos  La nueva duración de la reserva en minutos.
     * @param nuevoPrecio           El nuevo precio de la reserva.
     * @param nuevoDescuento        El nuevo descuento aplicado a la reserva.
     * @param nuevaIdPista          El ID de la nueva pista asociada a la reserva.
     * @param numeroAdultos         El nuevo número de adultos para la reserva (solo para reservas familiares o de adultos).
     * @param numeroNinos           El nuevo número de niños para la reserva (solo para reservas familiares o infantiles).
     *
     *
     * <p>La función utiliza sentencias SQL preparadas para asegurar la actualización correcta y segura de los datos.
     * Dependiendo de los parámetros proporcionados, se actualizan las tablas correspondientes:
     * - Si solo se especifica el número de niños, se actualiza la tabla de reservas infantiles.
     * - Si se especifican tanto el número de adultos como de niños, se actualiza la tabla de reservas familiares.
     * - Si solo se especifica el número de adultos, se actualiza la tabla de reservas de adultos.
     * </p>
     */
    public void actualizarReserva(int idReserva, Date nuevaFechaHora, int nuevaDuracionMinutos, float nuevoPrecio, float nuevoDescuento, int nuevaIdPista, Integer numeroAdultos, Integer numeroNinos) {
        String sqlActualizarReserva = prop.getProperty("actualizarReserva");
        String sqlActualizarReservaInfantil = prop.getProperty("actualizarReservaInfantil");
        String sqlActualizarReservaFamiliar = prop.getProperty("actualizarReservaFamiliar");
        String sqlActualizarReservaAdulto = prop.getProperty("actualizarReservaAdulto");

        DBConnection conexion = new DBConnection();
        try (Connection con = (Connection) conexion.getConnection()) {  // Usa try-with-resources para cerrar automáticamente la conexión

            // Actualizar la tabla principal `Reserva`
            try (PreparedStatement ps = con.prepareStatement(sqlActualizarReserva)) {
                ps.setTimestamp(1, new java.sql.Timestamp(nuevaFechaHora.getTime()));  // Fecha y hora
                ps.setInt(2, nuevaDuracionMinutos);  // Duración
                ps.setFloat(3, nuevoPrecio);  // Precio
                ps.setFloat(4, nuevoDescuento);  // Descuento
                ps.setInt(5, nuevaIdPista);  // Pista
                ps.setInt(6, idReserva);  // ID de reserva

                int filasActualizadas = ps.executeUpdate();
                if (filasActualizadas == 0) {
                    throw new IllegalStateException("No se pudo actualizar la reserva principal con ID: " + idReserva);
                }
            }

            // Actualizar tabla específica según el tipo de reserva
            if (numeroNinos != null && numeroAdultos == null) {  // Infantil
                try (PreparedStatement psInfantil = con.prepareStatement(sqlActualizarReservaInfantil)) {
                    psInfantil.setInt(1, numeroNinos);
                    psInfantil.setInt(2, idReserva);
                    psInfantil.executeUpdate();
                }
            } else if (numeroNinos != null && numeroAdultos != null) {  // Familiar
                try (PreparedStatement psFamiliar = con.prepareStatement(sqlActualizarReservaFamiliar)) {
                    psFamiliar.setInt(1, numeroAdultos);
                    psFamiliar.setInt(2, numeroNinos);
                    psFamiliar.setInt(3, idReserva);
                    psFamiliar.executeUpdate();
                }
            } else if (numeroAdultos != null && numeroNinos == null) {  // Adulto
                try (PreparedStatement psAdulto = con.prepareStatement(sqlActualizarReservaAdulto)) {
                    psAdulto.setInt(1, numeroAdultos);
                    psAdulto.setInt(2, idReserva);
                    psAdulto.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error al actualizar la reserva: " + e.getMessage(), e);
        }
    }


    /**
     * Actualiza las sesiones restantes de un bono.
     *
     * @param idBono El ID del bono a actualizar.
     */
    public void actualizarSesionesBono(int idBono) {
        String sql = prop.getProperty("actualizarSesionesBono");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBono);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Decrementa las sesiones utilizadas de un bono.
     *
     * @param idBono El ID del bono a actualizar.
     */
    public void decrementarSesionesBono(int idBono) {
        String sql = prop.getProperty("actualizarSesionesBonoDecrementar");

        DBConnection conexion = new DBConnection();
        try (Connection con = (Connection) conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBono);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Elimina una reserva de la base de datos.
     *
     * @param idReserva El ID de la reserva a eliminar.
     */
    public void eliminarReserva(int idReserva) {
        String sqlEliminarReserva = prop.getProperty("eliminarReserva");
        DBConnection conexion = new DBConnection();

        try (Connection con = (Connection) conexion.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sqlEliminarReserva)) {
                ps.setInt(1, idReserva);
                ps.executeUpdate();
            }
            eliminarReservaEspecifica(idReserva);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina una reserva específica en las tablas correspondientes.
     *
     * @param idReserva El ID de la reserva a eliminar.
     */
    public void eliminarReservaEspecifica(int idReserva) {
        String sqlEliminarReservaInfantil = prop.getProperty("eliminarReservaInfantil");
        String sqlEliminarReservaFamiliar = prop.getProperty("eliminarReservaFamiliar");
        String sqlEliminarReservaAdulto = prop.getProperty("eliminarReservaAdulto");

        DBConnection conexion = new DBConnection();

        try (Connection con = (Connection) conexion.getConnection()) {
            try (PreparedStatement psInfantil = con.prepareStatement(sqlEliminarReservaInfantil)) {
                psInfantil.setInt(1, idReserva);
                psInfantil.executeUpdate();
            }
            try (PreparedStatement psFamiliar = con.prepareStatement(sqlEliminarReservaFamiliar)) {
                psFamiliar.setInt(1, idReserva);
                psFamiliar.executeUpdate();
            }
            try (PreparedStatement psAdulto = con.prepareStatement(sqlEliminarReservaAdulto)) {
                psAdulto.setInt(1, idReserva);
                psAdulto.executeUpdate();
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
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @return El ID de la reserva creada.
     * @throws IllegalArgumentException Si la cuenta del jugador no está activa, si los parámetros son inválidos, o si la pista no cumple las condiciones para el tipo de reserva.
     */
    public int hacerReservaIndividual(JugadorDTO jugadorDTO, Date fechaHora, int duracionMinutos, PistaDTO pistaDTO, int numeroAdultos, int numeroNinos) {
        if (!jugadorDTO.isCuentaActiva()) {
            throw new IllegalArgumentException("La cuenta del jugador no está activa.");
        }

        // Validaciones comunes
        validarFechaHora(fechaHora);
        validarMaximoJugadores(pistaDTO, numeroAdultos, numeroNinos);
        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);

        // Validaciones exclusivas y creación de la reserva
        float descuentoAntiguedad = (jugadorDTO.calcularAntiguedad() > 2) ? 0.1f : 0.0f;

        ReservaFactory reservaFactory = new ReservaIndividualFactory();
        ReservaDTO reservaDTO;

        switch (tipoReserva) {
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
                throw new IllegalArgumentException("Tipo de reserva no válido.");
        }

        reservaDTO.setDescuento(descuentoAntiguedad);
        actualizarFechaInscripcionSiEsNecesario(jugadorDTO);
        return insertarReserva(reservaDTO);
    }

    /**
     * Realiza una reserva utilizando un bono para un jugador.
     *
     * @param jugadorDTO El jugador que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param pistaDTO La pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @return {@code true} si la reserva se realiza correctamente, {@code false} en caso contrario.
     * @throws IllegalArgumentException Si la cuenta del jugador no está activa, si los parámetros son inválidos, o si la pista no cumple las condiciones para el tipo de reserva.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws IllegalStateException Si no se puede crear un bono válido para el jugador.
     */
    public boolean hacerReservaBono(JugadorDTO jugadorDTO, Date fechaHora, int duracionMinutos, PistaDTO pistaDTO, int numeroAdultos, int numeroNinos) throws SQLException {
        if (!jugadorDTO.isCuentaActiva()) {
            throw new IllegalArgumentException("La cuenta del jugador no está activa.");
        }

        // Validación de fecha y hora
        validarFechaHora(fechaHora);

        // Obtener bono asociado o crear uno nuevo si no existe
        Bono bono = obtenerBonoPorJugador(jugadorDTO.getIdJugador());
        if (bono == null || bono.estaCaducado() || bono.getSesionesRestantes() <= 0) {
            bono = crearNuevoBono(jugadorDTO.getIdJugador());
        }

        if (bono == null || bono.estaCaducado() || bono.getSesionesRestantes() <= 0) {
            throw new IllegalStateException("No se pudo crear un bono válido.");
        }

        // Calcular número de sesión
        int numeroSesion = 5 - bono.getSesionesRestantes();

        // Validación de condiciones de la pista
        String tipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);
        if (!cumpleCondicionesTipoReserva(pistaDTO, tipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + tipoReserva + "'.");
        }

        // Crear reserva usando la fábrica
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

        // Insertar la reserva en la base de datos
        int idReserva = insertarReserva(reservaDTO);
        reservaDTO.setIdReserva(idReserva);

        // Actualizar sesiones restantes del bono
        actualizarSesionesBono(bono.getIdBono());
        return true;
    }

    /**
     * Modifica una reserva existente.
     *
     * @param jugadorDTO El jugador asociado a la reserva que se va a modificar.
     * @param pistaOriginal La pista original en la que se realizó la reserva.
     * @param fechaHoraOriginal La fecha y hora originales de la reserva.
     * @param nuevaPista La nueva pista donde se realizará la reserva.
     * @param nuevaFechaHora La nueva fecha y hora de la reserva.
     * @param nuevaDuracionMinutos La nueva duración de la reserva, en minutos.
     * @param numeroAdultos El nuevo número de adultos en la reserva.
     * @param numeroNinos El nuevo número de niños en la reserva.
     * @param bono El bono a utilizar en la nueva reserva, si aplica.
     * @param numeroSesion El número de la sesión del bono, si aplica.
     * @throws IllegalArgumentException Si no se encuentra la reserva, si no se puede modificar, si la pista no es válida o si los parámetros son inválidos.
     * @throws IllegalStateException Si ocurre un error al actualizar la base de datos.
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

        // Validar la nueva fecha y hora
        validarFechaHora(nuevaFechaHora);

        // Determinar el nuevo tipo de reserva basado en los parámetros
        String nuevoTipoReserva = determinarTipoReserva(numeroAdultos, numeroNinos);

        // Validar si la nueva pista cumple las condiciones para el tipo de reserva
        if (!cumpleCondicionesTipoReserva(nuevaPista, nuevoTipoReserva)) {
            throw new IllegalArgumentException("La pista seleccionada no es válida para el tipo de reserva '" + nuevoTipoReserva + "'.");
        }

        // Validar el número máximo de jugadores en la nueva pista
        validarMaximoJugadores(nuevaPista, numeroAdultos, numeroNinos);

        // Calcular el nuevo precio
        float nuevoPrecio = ReservaDTO.calcularPrecio(nuevaDuracionMinutos, reservaExistente.getDescuento());

        // Actualizar los datos comunes de la reserva en la tabla principal
        actualizarReserva(reservaExistente.getIdReserva(), nuevaFechaHora, nuevaDuracionMinutos, nuevoPrecio, reservaExistente.getDescuento(), nuevaPista.getIdPista(), 
                          nuevoTipoReserva.equals("familiar") || nuevoTipoReserva.equals("adulto") ? numeroAdultos : null, 
                          nuevoTipoReserva.equals("familiar") || nuevoTipoReserva.equals("infantil") ? numeroNinos : null);

	        switch (nuevoTipoReserva.toLowerCase()) {
	        case "familiar":
	            try {
	                eliminarReservaEspecifica(reservaExistente.getIdReserva());
	                insertarReservaFamiliar(reservaExistente.getIdReserva(), numeroAdultos, numeroNinos);
	            } catch (SQLException e) {
	                throw new IllegalStateException("Error al actualizar la reserva como Familiar: " + e.getMessage(), e);
	            }
	            break;
	
	        case "adulto":
	            try {
	                eliminarReservaEspecifica(reservaExistente.getIdReserva());
	                insertarReservaAdulto(reservaExistente.getIdReserva(), numeroAdultos);
	            } catch (SQLException e) {
	                throw new IllegalStateException("Error al actualizar la reserva como Adulto: " + e.getMessage(), e);
	            }
	            break;
	
	        case "infantil":
	            try {
	                eliminarReservaEspecifica(reservaExistente.getIdReserva());
	                insertarReservaInfantil(reservaExistente.getIdReserva(), numeroNinos);
	            } catch (SQLException e) {
	                throw new IllegalStateException("Error al actualizar la reserva como Infantil: " + e.getMessage(), e);
	            }
	            break;
	
	        default:
	            throw new IllegalArgumentException("Tipo de reserva no válido: " + nuevoTipoReserva);
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
        if (reservaDTO instanceof ReservaBono) {
            ReservaBono reservaBono = (ReservaBono) reservaDTO;
            decrementarSesionesBono(reservaBono.getBono().getIdBono());
        }
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
                    if(reservaDTO != null)
                    {
                    // Asignar precio y descuento a la reserva
                    reservaDTO.setIdReserva(idReserva);
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);

                    reservasFuturas.add(reservaDTO);
                    }
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
     * @param fechaInicio Fecha inicio para consultar reservas.
     * @param fechaFin Fecha fin para consultar reservas..
     * @param idPistaConsulta El id de la pista para consultar las reservas.
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

                    if(reservaDTO != null)
                    {
                    // Asignar precio y descuento a la reserva
                    reservaDTO.setIdReserva(idReserva);
                    reservaDTO.setPrecio(precio);
                    reservaDTO.setDescuento(descuento);

                    // Agregar la reserva a la lista de resultados
                    reservasPorFecha.add(reservaDTO);
                    }
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
     * @return La instancia completa de ReservaDTO según el tipo (Infantil, Familiar o Adulto),
     *         o null si no se encuentra la reserva.
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

        return null;
    }

    /**
     * Encuentra una reserva completa en función del idJugador, idPista y fechaHora.
     *
     * @param idJugador El ID del jugador.
     * @param idPista El ID de la pista.
     * @param fechaHora La fecha y hora de la reserva.
     * @return La instancia completa de ReservaDTO según el tipo (Infantil, Familiar o Adulto),
     *         o null si no se encuentra la reserva.
     */
    public ReservaDTO encontrarReserva(int idJugador, int idPista, Date fechaHora) {
        ReservaDTO reservaDTO = null;
        String sqlBaseReserva = prop.getProperty("encontrarReserva");

        DBConnection conexion = new DBConnection();
        Connection con = (Connection) conexion.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sqlBaseReserva)) {
            ps.setInt(1, idJugador);
            ps.setInt(2, idPista);
            ps.setTimestamp(3, new java.sql.Timestamp(fechaHora.getTime()));

            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    // datos comunes de la reserva
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
                        try (ResultSet rsFamiliar = (ResultSet)psFamiliar.executeQuery()) {
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

                    if (reservaDTO == null) {
                        return null;
                    }

                    // Asignar precio y descuento a la reserva
                    reservaDTO.setIdReserva(idReserva);
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
     * @return El tipo de reserva: "familiar", "adulto" o "infantil".
     * @throws IllegalArgumentException Si no se puede determinar el tipo de reserva.
     */
    public String determinarTipoReserva(int numeroAdultos, int numeroNinos) {
        if (numeroAdultos > 0 && numeroNinos > 0) {
            return "familiar";
        } else if (numeroAdultos > 0) {
            return "adulto";
        } else if (numeroNinos > 0) {
            return "infantil";
        } else {
            throw new IllegalArgumentException("El número de adultos y niños no puede ser ambos cero.");
        }
    }
    
    /**
     * Valida que la fecha y hora de la reserva sea válida.
     * 
     * @param fechaHora La fecha y hora de la reserva.
     * @throws IllegalArgumentException Si la fecha y hora no es válida.
     */
    private void validarFechaHora(Date fechaHora) {
        Date ahora = new Date();

        // Validar que la reserva sea futura con al menos 6 horas de antelación
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(ahora);
        calendario.add(Calendar.HOUR, 6);
        Date limiteMinimoReserva = calendario.getTime();

        if (fechaHora.before(limiteMinimoReserva)) {
            throw new IllegalArgumentException("La reserva debe realizarse con al menos 6 horas de antelación.");
        }

        // Validar horario permitido (9:00 a 20:30)
        Calendar horaReserva = Calendar.getInstance();
        horaReserva.setTime(fechaHora);
        int hora = horaReserva.get(Calendar.HOUR_OF_DAY);
        int minuto = horaReserva.get(Calendar.MINUTE);

        if (hora < 9 || (hora == 20 && minuto > 30) || hora > 20) {
            throw new IllegalArgumentException("La reserva solo puede realizarse entre las 9:00 y las 20:30.");
        }
    }
    
    /**
     * Valida que el número de jugadores no exceda el máximo permitido para la pista.
     *
     * @param pistaDTO La pista a reservar.
     * @param numeroAdultos El número de adultos en la reserva.
     * @param numeroNinos El número de niños en la reserva.
     * @throws IllegalArgumentException Si el número de jugadores excede el máximo permitido.
     */
    private void validarMaximoJugadores(PistaDTO pistaDTO, int numeroAdultos, int numeroNinos) {
        int maxJugadores = pistaDTO.getMax_jugadores();
        int totalJugadores = numeroAdultos + numeroNinos;

        if (totalJugadores > maxJugadores) {
            throw new IllegalArgumentException("El número total de jugadores excede el máximo permitido para la pista.");
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
    
    /**
     * Actualiza la fecha de inscripción del jugador si está en estado NULL.
     *
     * @param jugadorDTO El jugador cuyo estado de inscripción será actualizado.
     */
    public void actualizarFechaInscripcionSiEsNecesario(JugadorDTO jugadorDTO) {
        if (jugadorDTO.getFechaInscripcion() == null) {
            JugadoresDAO jugadoresDAO = new JugadoresDAO();
            jugadoresDAO.actualizarFechaInscripcion(jugadorDTO.getCorreoElectronico());
        }
    }
    
    /**
     * Obtiene un bono asociado a un jugador por su ID.
     *
     * @param idJugador El ID del jugador.
     * @return El bono asociado, o null si no se encuentra.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public Bono obtenerBonoPorJugador(int idJugador) throws SQLException {
        Bono bono = null;
        String sql = prop.getProperty("obtenerBonoPorJugador"); // Define esta consulta en `sql.properties`

        // Abrir la conexión a la base de datos
        DBConnection conexion = new DBConnection();
        Connection con = (Connection) conexion.getConnection();

        if (con == null) {
            throw new SQLException("Error: No se pudo establecer la conexión a la base de datos.");
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    bono = new Bono();
                    bono.setIdBono(rs.getInt("idBono"));
                    bono.setIdUsuario(idJugador);

                    // Recuperar numeroSesion y calcular sesionesRestantes
                    int numeroSesion = rs.getInt("numeroSesion");
                    bono.setSesionesRestantes(5 - numeroSesion);

                    bono.setFechaCaducidad(rs.getDate("fechaCaducidad"));
                }
            }
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return bono;
    }
    
    /**
     * Obtiene una reserva por su ID.
     *
     * @param idReserva El ID de la reserva a buscar.
     * @return La instancia de ReservaDTO encontrada, o null si no existe.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public ReservaDTO obtenerReservaPorId(int idReserva) throws SQLException {
        ReservaDTO reservaDTO = null;
        String sqlBaseReserva = prop.getProperty("buscarReservaPorId"); // Define esta consulta en `sql.properties`

        DBConnection conexion = new DBConnection();
        Connection con = (Connection) conexion.getConnection();

        if (con == null) {
            throw new SQLException("Error: No se pudo establecer la conexión a la base de datos.");
        }

        try (PreparedStatement ps = con.prepareStatement(sqlBaseReserva)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    // Obtener datos comunes de la reserva
                    int idJugador = rs.getInt("idJugador");
                    int idPista = rs.getInt("idPista");
                    Date fechaHora = rs.getTimestamp("fechaHora");
                    
                    // Usar el método `encontrarReserva` para obtener la reserva completa
                    reservaDTO = encontrarReserva(idJugador, idPista, fechaHora);
                }
            }
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return reservaDTO;
    }
    
    /**
     * Obtiene una reserva asociada a un bono y un usuario.
     *
     * @param idUsuario El ID del usuario.
     * @param bono El bono asociado.
     * @return La reserva encontrada, o null si no existe.
     */
    public ReservaDTO obtenerReservaPorIdBono(int idUsuario, Bono bono) {
        ReservaDTO reservaDTO = null;
        String sqlBaseReserva = prop.getProperty("encontrarReservaPorIdBono");
        if (sqlBaseReserva == null) {
            throw new IllegalStateException("La consulta 'encontrarReservaPorIdBono' no está definida.");
        }

        DBConnection conexion = new DBConnection();
        Connection con = (Connection) conexion.getConnection();

        try (PreparedStatement ps = con.prepareStatement(sqlBaseReserva)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, bono.getIdBono());

            try (ResultSet rs = (ResultSet) ps.executeQuery()) {
                if (rs.next()) {
                    // Datos comunes de la reserva
                    int idReserva = rs.getInt("idReserva");
                    int idPista = rs.getInt("idPista");
                    Date fechaHora = rs.getTimestamp("fechaHora");
                    int duracionMin = rs.getInt("duracionMin");
                    float precio = rs.getFloat("precio");
                    float descuento = rs.getFloat("descuento");

                    // Crear la instancia de la fábrica adecuada
                    ReservaFactory reservaFactory = new ReservaBonoFactory();

                    // Usar numeroSesion del objeto Bono
                    int numeroSesion = 5 - bono.getSesionesRestantes();

                    // Intentar identificar la reserva como familiar
                    String sqlFamiliar = prop.getProperty("buscarReservaFamiliar");
                    try (PreparedStatement psFamiliar = con.prepareStatement(sqlFamiliar)) {
                        psFamiliar.setInt(1, idReserva);
                        try (ResultSet rsFamiliar = (ResultSet) psFamiliar.executeQuery()) {
                            if (rsFamiliar.next()) {
                                int numeroAdultos = rsFamiliar.getInt("numAdultos");
                                int numeroNinos = rsFamiliar.getInt("numNinos");
                                reservaDTO = reservaFactory.crearReservaFamiliar(idUsuario, fechaHora, duracionMin, idPista, numeroAdultos, numeroNinos, bono, numeroSesion);
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
                                    reservaDTO = reservaFactory.crearReservaAdulto(idUsuario, fechaHora, duracionMin, idPista, numeroAdultos, bono, numeroSesion);
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
                                    reservaDTO = reservaFactory.crearReservaInfantil(idUsuario, fechaHora, duracionMin, idPista, numeroNinos, bono, numeroSesion);
                                }
                            }
                        }
                    }

                    if (reservaDTO == null) {
                        return null;
                    }

                    // Asignar precio y descuento a la reserva
                    reservaDTO.setIdReserva(idReserva);
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

}
