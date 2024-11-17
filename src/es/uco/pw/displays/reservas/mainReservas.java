package es.uco.pw.displays.reservas;

import es.uco.pw.business.jugador.JugadorDTO;
import es.uco.pw.business.pista.PistaDTO;
import es.uco.pw.business.reserva.*;
import es.uco.pw.data.dao.ReservasDAO;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * Clase principal para gestionar reservas de pistas.
 * Esta clase permite al usuario realizar reservas individuales y con bono, 
 * así como modificar, cancelar y consultar reservas.
 */
public class mainReservas {

	/**
     * Imprime el menú principal del gestor de reservas.
     */
    public static void imprimirMenu() {
        System.out.println("=====================================");
        System.out.println("        GESTOR DE RESERVAS");
        System.out.println("=====================================");
        System.out.println("1. Hacer reserva individual");
        System.out.println("2. Hacer reserva con bono");
        System.out.println("3. Modificar reserva");
        System.out.println("4. Cancelar reserva");
        System.out.println("5. Consultar reservas futuras");
        System.out.println("6. Consultar reservas por día y pista");
        System.out.println("0. Volver al menú principal");
        System.out.println("=====================================");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Método principal que inicia el gestor de reservas.
     * 
     * @param sc Scanner para la entrada del usuario.
     * @throws SQLException 
     */
    public static void main(Scanner sc, ReservasDAO reservasDAO) throws SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int opcion;
        boolean continuar = true;

        while (continuar) {
            imprimirMenu();  // Mostrar el menú de reservas
            opcion = sc.nextInt();  // Leer la opción del usuario
            sc.nextLine();  // Limpiar el buffer

            switch (opcion) {
            case 1:
                System.out.println("Iniciando proceso para hacer una reserva individual...");
                try {
                    // Solicitar correo del usuario
                    System.out.print("Ingrese el correo del usuario: ");
                    String correo = sc.nextLine();
                    JugadorDTO jugadorDTO = reservasDAO.buscarJugadorPorCorreo(correo);
                    if (jugadorDTO == null) {
                        System.out.println("Jugador no encontrado.");
                        break;
                    }
                    
                    System.out.print("Ingrese número de adultos: ");
                    int numeroAdultos = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer

                    System.out.print("Ingrese número de niños: ");
                    int numeroNinos = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer
                    
                    
                    String tipoReserva = reservasDAO.determinarTipoReserva(numeroAdultos, numeroNinos);

                    // Número total de jugadores para la reserva
                    int totalJugadores = numeroAdultos + numeroNinos;

                    // Listar pistas disponibles según el tipo de reserva y capacidad
                    List<PistaDTO> pistasDisponibles = reservasDAO.listarPistasDisponibles(tipoReserva).stream()
                        .filter(pista -> pista.getMax_jugadores() >= totalJugadores)
                        .collect(Collectors.toList());

                    if (pistasDisponibles.isEmpty()) {
                        System.out.println("No hay pistas disponibles para el tipo de reserva '" + tipoReserva + "' con un máximo de " + totalJugadores + " jugadores.");
                        break;
                    }

                    // Mostrar las pistas disponibles
                    System.out.println("Pistas disponibles para reservar:");
                    for (PistaDTO pistaDTO : pistasDisponibles) {
                        System.out.println("ID: " + pistaDTO.getIdPista() + ", Nombre: " + pistaDTO.getNombrePista() + ", Máx. Jugadores: " + pistaDTO.getMax_jugadores());
                    }

                    // Solicitar ID de la pista
                    System.out.print("Ingrese ID de la pista: ");
                    int idPista = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer

                    // Validar que el ID de pista seleccionado esté en la lista de disponibles
                    PistaDTO pistaDTO = pistasDisponibles.stream().filter(p -> p.getIdPista() == idPista).findFirst().orElse(null);
                    if (pistaDTO == null) {
                        System.out.println("Pista no válida. Seleccione una pista de la lista mostrada.");
                        break;
                    }

                    // Solicitar detalles adicionales de la reserva
                    System.out.print("Ingrese fecha y hora (yyyy-MM-dd HH:mm): ");
                    Date fechaHora = dateFormat.parse(sc.nextLine());

                    System.out.print("Ingrese duración en minutos (60, 90, 120): ");
                    int duracionMinutos = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer


                    try {
                        // Crear la reserva individual
                        int idReserva = reservasDAO.hacerReservaIndividual(jugadorDTO, fechaHora, duracionMinutos, pistaDTO, numeroAdultos, numeroNinos);

                        // Mostrar los detalles de la reserva creada
                        ReservaDTO reserva = reservasDAO.obtenerReservaPorId(idReserva);
                        System.out.println("Reserva individual creada correctamente:");
                        System.out.println(reserva.toString());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error al crear la reserva: " + e.getMessage());
                    }
                } catch (ParseException e) {
                    System.out.println("Formato de fecha incorrecto.");
                }
                break;

                
            case 2:
                System.out.println("Iniciando proceso para hacer una reserva con bono...");
                try {
                    // Solicitar correo del usuario
                    System.out.print("Ingrese el correo del usuario: ");
                    String correoBono = sc.nextLine();
                    JugadorDTO jugadorBono = reservasDAO.buscarJugadorPorCorreo(correoBono);
                    if (jugadorBono == null) {
                        System.out.println("Jugador no encontrado.");
                        break;
                    }

                    System.out.print("Ingrese número de adultos: ");
                    int numeroAdultosBono = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer

                    System.out.print("Ingrese número de niños: ");
                    int numeroNinosBono = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer
                    
                    
                    String tipoReservaBono = reservasDAO.determinarTipoReserva(numeroAdultosBono, numeroNinosBono);

                 // Número total de jugadores para la reserva
                    int totalJugadores = numeroAdultosBono + numeroNinosBono;

                    // Listar pistas disponibles según el tipo de reserva y capacidad
                    List<PistaDTO> pistasDisponiblesBono = reservasDAO.listarPistasDisponibles(tipoReservaBono).stream()
                        .filter(pista -> pista.getMax_jugadores() >= totalJugadores)
                        .collect(Collectors.toList());

                    if (pistasDisponiblesBono.isEmpty()) {
                        System.out.println("No hay pistas disponibles para el tipo de reserva '" + tipoReservaBono + "' con un máximo de " + totalJugadores + " jugadores.");
                        break;
                    }

                    // Mostrar las pistas disponibles
                    System.out.println("Pistas disponibles para reservar con bono:");
                    for (PistaDTO pistaDTO : pistasDisponiblesBono) {
                        System.out.println("ID: " + pistaDTO.getIdPista() + ", Nombre: " + pistaDTO.getNombrePista() + ", Máx. Jugadores: " + pistaDTO.getMax_jugadores());
                    }

                    // Solicitar ID de la pista
                    System.out.print("Ingrese ID de la pista: ");
                    int idPistaBono = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer

                    // Validar que el ID de pista seleccionado esté en la lista de disponibles
                    PistaDTO pistaBono = pistasDisponiblesBono.stream().filter(p -> p.getIdPista() == idPistaBono).findFirst().orElse(null);
                    if (pistaBono == null) {
                        System.out.println("Pista no válida. Seleccione una pista de la lista mostrada.");
                        break;
                    }

                    // Solicitar detalles adicionales de la reserva
                    System.out.print("Ingrese fecha y hora de la reserva (yyyy-MM-dd HH:mm): ");
                    Date fechaHoraBono = dateFormat.parse(sc.nextLine());

                    System.out.print("Ingrese duración en minutos (60, 90, 120): ");
                    int duracionMinutosBono = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer


                    try {
                        // Crear la reserva con bono
                        boolean reservaConBonoExitosa = reservasDAO.hacerReservaBono(jugadorBono, fechaHoraBono, duracionMinutosBono, pistaBono, numeroAdultosBono, numeroNinosBono);

                        // Mostrar los detalles de la reserva creada si fue exitosa
                        if (reservaConBonoExitosa) {
                            ReservaDTO reserva = reservasDAO.obtenerReservaPorIdBono(jugadorBono.getIdJugador(), reservasDAO.obtenerBonoPorJugador(jugadorBono.getIdJugador()));
                            if (reserva != null) {
                                System.out.println("Reserva con bono creada correctamente:");
                                System.out.println(reserva.toString());
                            } else {
                                System.out.println("Error al recuperar la reserva de bono creada.");
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error al crear la reserva con bono: " + e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("Error en la base de datos: " + e.getMessage());
                    }
                } catch (ParseException e) {
                    System.out.println("Formato de fecha incorrecto.");
                }
                break;


            case 3:
                // Modificar una reserva
                System.out.println("Iniciando proceso para modificar una reserva...");
                try {
                    // Ingresar el correo del jugador
                    System.out.print("Ingrese el correo del usuario: ");
                    String correoModificacion = sc.nextLine();
                    JugadorDTO jugadorModificacion = reservasDAO.buscarJugadorPorCorreo(correoModificacion);
                    if (jugadorModificacion == null) {
                        System.out.println("Jugador no encontrado.");
                        break;
                    }

                    // Ingresar los detalles de la pista actual
                    System.out.print("Ingrese ID de la pista actual: ");
                    int idPistaModificacion = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer
                    PistaDTO pistaModificacion = reservasDAO.buscarPistaPorId(idPistaModificacion);
                    if (pistaModificacion == null) {
                        System.out.println("Pista no válida.");
                        break;
                    }

                    // Ingresar fecha y hora originales
                    System.out.print("Ingrese fecha y hora originales (yyyy-MM-dd HH:mm): ");
                    Date fechaHoraModificacion = dateFormat.parse(sc.nextLine());

                    // Buscar la reserva original
                    ReservaDTO reservaDTO = reservasDAO.encontrarReserva(jugadorModificacion.getIdJugador(), idPistaModificacion, fechaHoraModificacion);
                    if (reservaDTO == null) {
                        System.out.println("Reserva no encontrada.");
                        break;
                    }
                    
                    System.out.print("Ingrese nuevo número de adultos: ");
                    int nuevosAdultos = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer

                    System.out.print("Ingrese nuevo número de niños: ");
                    int nuevosNinos = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer
                    
                    // Determinar el tipo de reserva basado en los parámetros originales
                    String tipoReserva = reservasDAO.determinarTipoReserva(nuevosAdultos, nuevosNinos);

                    // Número total de jugadores
                    int totalJugadores = nuevosAdultos + nuevosNinos;

                    // Decidir si cambiar de pista
                    System.out.print("¿Desea cambiar de pista? (s/n): ");
                    String cambiarPista = sc.nextLine();
                    PistaDTO nuevaPista = pistaModificacion; // Por defecto, se mantiene la pista actual

                    if (cambiarPista.equalsIgnoreCase("s")) {
                        // Listar pistas disponibles según el tipo de reserva y capacidad
                        List<PistaDTO> pistasDisponibles = reservasDAO.listarPistasDisponibles(tipoReserva).stream()
                            .filter(pista -> pista.getMax_jugadores() >= totalJugadores)
                            .collect(Collectors.toList());

                        if (pistasDisponibles.isEmpty()) {
                            System.out.println("No hay pistas disponibles para el tipo de reserva '" + tipoReserva + "' con un máximo de " + totalJugadores + " jugadores.");
                            break;
                        }

                        // Mostrar las pistas disponibles
                        System.out.println("Pistas disponibles para cambiar:");
                        for (PistaDTO pistaDTO : pistasDisponibles) {
                            System.out.println("ID: " + pistaDTO.getIdPista() + ", Nombre: " + pistaDTO.getNombrePista() + ", Máx. Jugadores: " + pistaDTO.getMax_jugadores());
                        }

                        // Solicitar ID de la nueva pista
                        System.out.print("Ingrese el nuevo ID de la pista: ");
                        int nuevoIdPista = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        // Validar que el ID de pista seleccionado esté en la lista de disponibles
                        nuevaPista = pistasDisponibles.stream().filter(p -> p.getIdPista() == nuevoIdPista).findFirst().orElse(null);
                        if (nuevaPista == null) {
                            System.out.println("Pista no válida. Seleccione una pista de la lista mostrada.");
                            break;
                        }
                    }

                    // Ingresar nuevos detalles de la reserva
                    System.out.print("Ingrese nueva fecha y hora (yyyy-MM-dd HH:mm): ");
                    Date nuevaFechaHora = dateFormat.parse(sc.nextLine());

                    System.out.print("Ingrese nueva duración en minutos (60, 90, 120): ");
                    int nuevaDuracion = sc.nextInt();
                    sc.nextLine(); // Limpiar buffer

                    // Manejo del bono y la sesión (si aplica)
                    Bono bono = null;
                    int numeroSesion = 0;
                    if (reservaDTO instanceof ReservaBono) {
                        bono = ((ReservaBono) reservaDTO).getBono();
                        numeroSesion = ((ReservaBono) reservaDTO).getNumeroSesion();
                    }

                    // Modificar la reserva
                    try {
                        reservasDAO.modificarReserva(
                            jugadorModificacion,        // JugadorDTO
                            pistaModificacion,          // PistaDTO para la pista original
                            fechaHoraModificacion,      // Fecha y hora originales
                            nuevaPista,                 // Nueva PistaDTO (puede ser la misma)
                            nuevaFechaHora,             // Nueva fecha y hora
                            nuevaDuracion,              // Nueva duración en minutos
                            nuevosAdultos,              // Nuevo número de adultos
                            nuevosNinos,                // Nuevo número de niños
                            bono,                       // Bono (si aplica)
                            numeroSesion                // Número de sesión del bono (si aplica)
                        );

                        // Obtener la reserva modificada y mostrar los detalles
                        ReservaDTO reservaModificada = reservasDAO.encontrarReserva(jugadorModificacion.getIdJugador(), nuevaPista.getIdPista(), nuevaFechaHora);
                        if (reservaModificada != null) {
                            System.out.println("Reserva modificada correctamente:");
                            System.out.println(reservaModificada.toString());
                        } else {
                            System.out.println("La reserva fue modificada, pero no se pudo recuperar para mostrar los detalles.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error al modificar la reserva: " + e.getMessage());
                    }
                } catch (ParseException e) {
                    System.out.println("Formato de fecha incorrecto.");
                }
                break;



                case 4:
                    // Cancelar una reserva
                    System.out.println("Iniciando proceso para cancelar una reserva...");
                    try {
                        System.out.print("Ingrese el correo del usuario: ");
                        String correoCancelacion = sc.nextLine();
                        JugadorDTO jugadorCancelacion = reservasDAO.buscarJugadorPorCorreo(correoCancelacion);
                        if (jugadorCancelacion == null) {
                            System.out.println("Jugador no encontrado.");
                            break;
                        }

                        System.out.print("Ingrese ID de la pista: ");
                        int idPistaCancelacion = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese fecha y hora de la reserva (yyyy-MM-dd HH:mm): ");
                        Date fechaHoraCancelacion = dateFormat.parse(sc.nextLine());

                        // Obtener la pista directamente usando el método buscarPistaPorId
                        PistaDTO pistaCancelacion = reservasDAO.buscarPistaPorId(idPistaCancelacion);
                        if (pistaCancelacion == null) {
                            System.out.println("Pista no encontrada.");
                            break;
                        }

                        try {
                            // Cancelar la reserva usando los detalles del jugador, pista y fecha/hora de la reserva
                        	reservasDAO.cancelarReserva(jugadorCancelacion, pistaCancelacion, fechaHoraCancelacion);
                            System.out.println("Reserva cancelada exitosamente.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error al cancelar la reserva: " + e.getMessage());
                        }
                    } catch (ParseException e) {
                        System.out.println("Formato de fecha incorrecto.");
                    }
                    break;

                case 5:
                    System.out.println("Consultando reservas futuras...");
                    List<ReservaDTO> reservasFuturas = reservasDAO.consultarReservasFuturas();

                    if (reservasFuturas.isEmpty()) {
                        System.out.println("No hay reservas futuras.");
                    } else {
                    	for (ReservaDTO reserva : reservasFuturas) {
                    	    System.out.println("Número de Reserva: " + reserva.getIdReserva());
                    	    System.out.println(reserva.toString());
                    	    System.out.println("------------------------------------");
                    	}
                    }
                    break;

                    
                case 6:
                    System.out.println("Consultando reservas por rango de fechas y pista...");
                    try {
                        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

                        System.out.print("Ingrese la fecha de inicio (yyyy-MM-dd): ");
                        Date fechaInicio = formatoFecha.parse(sc.nextLine());

                        System.out.print("Ingrese la fecha de fin (yyyy-MM-dd): ");
                        Date fechaFin = formatoFecha.parse(sc.nextLine());

                        System.out.print("Ingrese ID de la pista: ");
                        int idPistaConsulta = sc.nextInt();
                        sc.nextLine();

                        List<ReservaDTO> reservasPorRangoDeFechasYPista = reservasDAO.consultarReservasPorRangosDeFechaYPista(fechaInicio, fechaFin, idPistaConsulta);

                        if (reservasPorRangoDeFechasYPista.isEmpty()) {
                            System.out.println("No hay reservas para ese rango de fechas y pista.");
                        } else {
                            System.out.println("------------------------------------");
                            for (ReservaDTO reservaDTO : reservasPorRangoDeFechasYPista) {
                                System.out.println(reservaDTO.toString());
                                System.out.println("------------------------------------");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al consultar reservas: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;




                case 0:
                    // Volver al menú principal
                    System.out.println("Volviendo al menú principal...");
                    continuar = false;
                    break;

                default:
                    System.out.println("Opción no válida. Por favor, intente nuevamente.");
            }
        }
    }
}
