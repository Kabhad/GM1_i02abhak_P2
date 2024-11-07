package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * La clase {@code ReservaIndividual} representa una reserva individual
 * de baloncesto que puede contener una reserva específica, que puede ser
 * de tipo infantil, adulto o familiar.
 */
public class ReservaIndividual extends ReservaDTO {
    private ReservaDTO reservaEspecifica; // Contendrá la reserva específica (infantil, adulto, familiar)

    /**
     * Constructor para crear una nueva reserva individual con una reserva específica.
     *
     * @param idUsuario        El ID del usuario que realiza la reserva.
     * @param fechaHora        La fecha y hora de la reserva.
     * @param duracionMinutos  La duración de la reserva en minutos.
     * @param idPista         El ID de la pista que se está reservando.
     * @param reservaEspecifica La reserva específica asociada (infantil, adulto, familiar).
     */
    public ReservaIndividual(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, ReservaDTO reservaEspecifica) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
        this.reservaEspecifica = reservaEspecifica; // Guardar la reserva específica
    }

    /**
     * Obtiene la reserva específica asociada a esta reserva individual.
     *
     * @return La reserva específica (infantil, adulto, familiar).
     */
    public ReservaDTO getReservaEspecifica() {
        return reservaEspecifica;
    }

    public void setReservaEspecifica(ReservaDTO reservaEspecifica) {
		this.reservaEspecifica = reservaEspecifica;
	}

	/**
     * Aplica un descuento tanto a la reserva individual como a la reserva específica.
     *
     * @param descuento El porcentaje de descuento a aplicar.
     */
    @Override
    public void setDescuento(float descuento) {
        super.setDescuento(descuento);  // Aplica descuento a la reserva principal
        if (this.reservaEspecifica != null) {
            this.reservaEspecifica.setDescuento(descuento);  // Aplica el descuento a la reserva específica
        }
    }

    /**
     * Devuelve una representación en cadena de la reserva individual,
     * incluyendo detalles de la reserva específica.
     *
     * @return Una cadena que representa la reserva individual.
     */
    @Override
    public String toString() {
        String detallesEspecificos = "";

        if (reservaEspecifica instanceof ReservaInfantil) {
            detallesEspecificos = ((ReservaInfantil) reservaEspecifica).toStringEspecifica();
        } else if (reservaEspecifica instanceof ReservaAdulto) {
            detallesEspecificos = ((ReservaAdulto) reservaEspecifica).toStringEspecifica();
        } else if (reservaEspecifica instanceof ReservaFamiliar) {
            detallesEspecificos = ((ReservaFamiliar) reservaEspecifica).toStringEspecifica();
        }

        return "ReservaIndividual [" + super.toString() + 
               ", reservaEspecifica=" + detallesEspecificos + "]";
    }
}
