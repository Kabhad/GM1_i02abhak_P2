package es.uco.pw.displays.reservas;

import es.uco.pw.business.jugador.JugadorDTO;
import es.uco.pw.business.pista.PistaDTO;
import es.uco.pw.business.reserva.Bono;
import es.uco.pw.business.reserva.ReservaDTO;
import es.uco.pw.business.reserva.ReservaBono;
import es.uco.pw.business.reserva.ReservaIndividual;
import es.uco.pw.data.dao.ReservasDAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


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
     */
    public static void main(Scanner sc) {
        ReservasDAO reservasDAO = ReservasDAO.getInstance();  // Obtener la instancia del gestor (Singleton)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int opcion;
        boolean continuar = true;

        while (continuar) {
            imprimirMenu();  // Mostrar el menú de reservas
            opcion = sc.nextInt();  // Leer la opción del usuario
            sc.nextLine();  // Limpiar el buffer

            switch (opcion) {
                case 1:
                    // Hacer una reserva individual
                    System.out.println("Iniciando proceso para hacer una reserva individual...");
                    try {
                        System.out.print("Ingrese el correo del usuario: ");
                        String correo = sc.nextLine();
                        JugadorDTO jugadorDTO = reservasDAO.buscarJugadorPorCorreo(correo);
                        if (jugadorDTO == null) {
                            System.out.println("Jugador no encontrado.");
                            break;
                        }

                        List<PistaDTO> pistasDisponibles = reservasDAO.listarPistasDisponibles();
                        if (pistasDisponibles.isEmpty()) {
                            System.out.println("No hay pistas disponibles.");
                            break;
                        }

                        System.out.println("Pistas disponibles para reservar:");
                        for (PistaDTO pistaDTO : pistasDisponibles) {
                            System.out.println("ID: " + pistaDTO.getIdPista() + ", Nombre: " + pistaDTO.getNombrePista() + ", Máx. Jugadores: " + pistaDTO.getMax_jugadores());
                        }

                        System.out.print("Ingrese ID de la pista: ");
                        int idPista = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer
                        PistaDTO pistaDTO = pistasDisponibles.stream().filter(p -> p.getIdPista() == idPista).findFirst().orElse(null);
                        if (pistaDTO == null) {
                            System.out.println("Pista no válida.");
                            break;
                        }

                        System.out.print("Ingrese fecha y hora (yyyy-MM-dd HH:mm): ");
                        Date fechaHora = dateFormat.parse(sc.nextLine());

                        System.out.print("Ingrese duración en minutos (60, 90, 120): ");
                        int duracionMinutos = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese número de adultos: ");
                        int numeroAdultos = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese número de niños: ");
                        int numeroNinos = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        try {
                            reservasDAO.hacerReservaIndividual(jugadorDTO, fechaHora, duracionMinutos, pistaDTO, numeroAdultos, numeroNinos);
                            System.out.println("Reserva individual creada correctamente.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error al crear la reserva: " + e.getMessage());
                        }
                    } catch (ParseException e) {
                        System.out.println("Formato de fecha incorrecto.");
                    }
                    break;

                case 2:
                    // Hacer una reserva con bono
                    System.out.println("Iniciando proceso para hacer una reserva con bono...");
                    try {
                        System.out.print("Ingrese el correo del usuario: ");
                        String correoBono = sc.nextLine();
                        JugadorDTO jugadorBono = reservasDAO.buscarJugadorPorCorreo(correoBono);
                        if (jugadorBono == null) {
                            System.out.println("Jugador no encontrado.");
                            break;
                        }

                        List<PistaDTO> pistasDisponiblesBono = reservasDAO.listarPistasDisponibles();
                        if (pistasDisponiblesBono.isEmpty()) {
                            System.out.println("No hay pistas disponibles.");
                            break;
                        }

                        System.out.println("Pistas disponibles:");
                        for (PistaDTO pistaDTO : pistasDisponiblesBono) {
                            System.out.println("ID: " + pistaDTO.getIdPista() + ", Nombre: " + pistaDTO.getNombrePista() + ", Máx. Jugadores: " + pistaDTO.getMax_jugadores());
                        }

                        System.out.print("Ingrese ID de la pista: ");
                        int idPistaBono = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer
                        PistaDTO pistaBono = pistasDisponiblesBono.stream().filter(p -> p.getIdPista() == idPistaBono).findFirst().orElse(null);
                        if (pistaBono == null) {
                            System.out.println("Pista no válida.");
                            break;
                        }

                        System.out.print("Ingrese fecha y hora de la reserva (yyyy-MM-dd HH:mm): ");
                        Date fechaHoraBono = dateFormat.parse(sc.nextLine());

                        System.out.print("Ingrese duración en minutos (60, 90, 120): ");
                        int duracionMinutosBono = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese número de adultos: ");
                        int numeroAdultosBono = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese número de niños: ");
                        int numeroNinosBono = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese ID del bono: ");
                        int idBono = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese número de la sesión del bono (1 a 5): ");
                        int numeroSesion = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer
                        Bono bono = new Bono(idBono, jugadorBono.getIdJugador(), numeroSesion, fechaHoraBono);

                        try {
                            reservasDAO.hacerReservaBono(jugadorBono, fechaHoraBono, duracionMinutosBono, pistaBono, numeroAdultosBono, numeroNinosBono, bono, numeroSesion);
                            System.out.println("Reserva de bono creada correctamente.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error al crear la reserva de bono: " + e.getMessage());
                        }
                    } catch (ParseException e) {
                        System.out.println("Formato de fecha incorrecto.");
                    }
                    break;

                case 3:
                    // Modificar una reserva
                    System.out.println("Iniciando proceso para modificar una reserva...");
                    try {
                        System.out.print("Ingrese el correo del usuario: ");
                        String correoModificacion = sc.nextLine();
                        JugadorDTO jugadorModificacion = reservasDAO.buscarJugadorPorCorreo(correoModificacion);
                        if (jugadorModificacion == null) {
                            System.out.println("Jugador no encontrado.");
                            break;
                        }

                        System.out.print("Ingrese ID de la pista: ");
                        int idPistaModificacion = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer
                        PistaDTO pistaModificacion = reservasDAO.listarPistasDisponibles().stream()
                            .filter(p -> p.getIdPista() == idPistaModificacion)
                            .findFirst().orElse(null);
                        if (pistaModificacion == null) {
                            System.out.println("Pista no válida.");
                            break;
                        }

                        System.out.print("Ingrese fecha y hora originales (yyyy-MM-dd HH:mm): ");
                        Date fechaHoraModificacion = dateFormat.parse(sc.nextLine());

                        ReservaDTO reservaDTO = reservasDAO.encontrarReserva(jugadorModificacion.getIdJugador(), pistaModificacion.getIdPista(), fechaHoraModificacion);
                        if (reservaDTO == null) {
                            System.out.println("Reserva no encontrada.");
                            break;
                        }

                        System.out.print("Ingrese nueva fecha y hora (yyyy-MM-dd HH:mm): ");
                        Date nuevaFechaHora = dateFormat.parse(sc.nextLine());

                        System.out.print("Ingrese nueva duración en minutos (60, 90, 120): ");
                        int nuevaDuracion = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese nuevo número de adultos: ");
                        int nuevosAdultos = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        System.out.print("Ingrese nuevo número de niños: ");
                        int nuevosNinos = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        Bono bono = null;
                        int numeroSesion = 0;

                        if (reservaDTO instanceof ReservaBono) {
                            bono = ((ReservaBono) reservaDTO).getBono();
                            numeroSesion = ((ReservaBono) reservaDTO).getNumeroSesion();
                        }

                        try {
                            reservasDAO.modificarReserva(
                                reservaDTO, pistaModificacion, fechaHoraModificacion, nuevaFechaHora, nuevaDuracion, nuevosAdultos, nuevosNinos, bono, numeroSesion
                            );
                            System.out.println("Reserva modificada exitosamente.");
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

                        try {
                            reservasDAO.cancelarReserva(jugadorCancelacion, reservasDAO.listarPistasDisponibles().stream().filter(p -> p.getIdPista() == idPistaCancelacion).findFirst().orElse(null), fechaHoraCancelacion);
                            System.out.println("Reserva cancelada exitosamente.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error al cancelar la reserva: " + e.getMessage());
                        }
                    } catch (ParseException e) {
                        System.out.println("Formato de fecha incorrecto.");
                    }
                    break;

                case 5:
                    // No se hacen cambios en este case según tus indicaciones.
                    System.out.println("Consultando reservas futuras...");
                    List<ReservaDTO> reservasFuturas = reservasDAO.consultarReservasFuturas();
                    
                    if (reservasFuturas.isEmpty()) {
                        System.out.println("No hay reservas futuras.");
                    } else {
                        for (ReservaDTO reservaDTO : reservasFuturas) {
                            if (reservaDTO instanceof ReservaBono) {
                                ReservaBono reservaBono = (ReservaBono) reservaDTO;
                                System.out.println("Reserva de Bono:");
                                System.out.println("  ID Usuario: " + reservaBono.getIdUsuario());
                                System.out.println("  Fecha y Hora: " + reservaBono.getFechaHora());
                                System.out.println("  Pista: " + reservaBono.getIdPista());
                                System.out.println("  Duración: " + reservaBono.getDuracionMinutos() + " minutos");
                                System.out.println("  Bono ID: " + reservaBono.getIdBono());
                                System.out.println("  Sesión número: " + reservaBono.getNumeroSesion());
                                System.out.println("  Sesiones restantes: " + reservaBono.getBono().getSesionesRestantes());

                                ReservaDTO reservaEspecifica = reservaBono.getReservaEspecifica();
                                if (reservaEspecifica != null) {
                                    System.out.println("  Reserva Específica: " + reservaEspecifica.toString());
                                    System.out.println("  Precio Específico: " + reservaEspecifica.getPrecio());
                                    System.out.println("  Descuento aplicado: " + (reservaEspecifica.getDescuento() * 100) + "%");
                                }

                                System.out.println("  Precio Total: " + reservaEspecifica.getPrecio());
                                System.out.println("  Descuento aplicado en Bono: " + (reservaEspecifica.getDescuento() * 100) + "%");
                                System.out.println("------------------------------------");

                            } else if (reservaDTO instanceof ReservaIndividual) {
                                ReservaIndividual reservaIndividual = (ReservaIndividual) reservaDTO;
                                System.out.println("Reserva Individual:");
                                System.out.println("  ID Usuario: " + reservaIndividual.getIdUsuario());
                                System.out.println("  Fecha y Hora: " + reservaIndividual.getFechaHora());
                                System.out.println("  Pista: " + reservaIndividual.getIdPista());
                                System.out.println("  Duración: " + reservaIndividual.getDuracionMinutos() + " minutos");

                                ReservaDTO reservaEspecifica = reservaIndividual.getReservaEspecifica();
                                if (reservaEspecifica != null) {
                                    System.out.println("  Reserva Específica: " + reservaEspecifica.toString());
                                    System.out.println("  Precio Específico: " + reservaEspecifica.getPrecio());
                                    System.out.println("  Descuento aplicado: " + (reservaEspecifica.getDescuento() * 100) + "%");
                                }

                                System.out.println("  Precio Total: " + reservaEspecifica.getPrecio());
                                System.out.println("  Descuento aplicado en Individual: " + (reservaEspecifica.getDescuento() * 100) + "%");
                                System.out.println("------------------------------------");
                            }
                        }
                    }
                    break;

                case 6:
                    // Consultar reservas por día y pista
                    System.out.println("Consultando reservas por día y pista...");
                    try {
                        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

                        System.out.print("Ingrese la fecha (yyyy-MM-dd): ");
                        Date dia = formatoFecha.parse(sc.nextLine());

                        System.out.print("Ingrese ID de la pista: ");
                        int idPistaConsulta = sc.nextInt();
                        sc.nextLine(); // Limpiar buffer

                        List<ReservaDTO> reservasPorDiaYPista = reservasDAO.consultarReservasPorDiaYPista(dia, idPistaConsulta);
                        if (reservasPorDiaYPista.isEmpty()) {
                            System.out.println("No hay reservas para ese día y pista.");
                        } else {
                            for (ReservaDTO reservaDTO : reservasPorDiaYPista) {
                                System.out.println(reservaDTO);
                            }
                        }
                    } catch (ParseException e) {
                        System.out.println("Formato de fecha incorrecto.");
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