package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * La clase {@code ReservaGeneralFactory} es una fábrica para crear reservas
 * de baloncesto, ya sea para usuarios individuales o a través de bonos.
 * Utiliza el patrón de diseño Factory para encapsular la lógica de creación
 * de diferentes tipos de reservas.
 */
public class ReservaGeneralFactory {

    /**
     * Crea una reserva de baloncesto basada en el tipo de usuario y si se 
     * utiliza un bono.
     *
     * @param tipoUsuario      El tipo de usuario (infantil, familiar, adulto).
     * @param idUsuario        El ID del usuario que realiza la reserva.
     * @param fechaHora        La fecha y hora de la reserva.
     * @param duracionMinutos  La duración de la reserva en minutos.
     * @param idPista         El ID de la pista que se está reservando.
     * @param numeroAdultos    El número de adultos (si aplica).
     * @param numeroNinos      El número de niños (si aplica).
     * @param tieneAntiguedad  Indica si el usuario tiene antigüedad (true/false).
     * @param bono             El objeto Bono que se está utilizando (puede ser null).
     * @param numeroSesion     El número de sesión del bono que se está utilizando.
     * @return                La reserva creada, ya sea individual o de bono.
     * @throws IllegalArgumentException Si el tipo de usuario no es válido
     *                                  o si la duración no es válida.
     */
    public static ReservaDTO crearReserva(String tipoUsuario, int idUsuario, Date fechaHora, 
                                        int duracionMinutos, int idPista, 
                                        int numeroAdultos, int numeroNinos, 
                                        boolean tieneAntiguedad, Bono bono, 
                                        int numeroSesion) {
        ReservaFactory factory;

        if (bono == null) {  // Si no hay bono, es una reserva individual
            factory = new ReservaIndividualFactory();
            return factory.crearReserva(tipoUsuario, idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos, tieneAntiguedad);
        } else {  // Si hay bono, es una reserva de bono
            factory = new ReservaBonoFactory();
            return factory.crearReservaBono(tipoUsuario, idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos, bono, numeroSesion);
        }
    }
}
