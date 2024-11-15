package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * La clase {@code ReservaIndividual} representa una reserva individual
 * de baloncesto que puede contener una reserva específica, que puede ser
 * de tipo infantil, adulto o familiar.
 */

public class ReservaIndividual extends ReservaDTO {

    /**
     * Constructor para crear una nueva reserva individual.
     *
     * @param idUsuario       El ID del usuario que realiza la reserva.
     * @param fechaHora       La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista         El ID de la pista que se está reservando.
     */
    public ReservaIndividual(int idUsuario, Date fechaHora, int duracionMinutos, int idPista) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
    }

    /**
     * Devuelve una representación en cadena de la reserva individual.
     *
     * @return Una cadena que representa la reserva individual.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Reserva Individual:\n");
        sb.append(super.toString()); // Llama al toString de ReservaDTO para los detalles comunes
        return sb.toString();
    }


}




