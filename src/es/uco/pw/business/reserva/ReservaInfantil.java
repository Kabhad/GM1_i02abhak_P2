package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * La clase {@code ReservaInfantil} representa una reserva de baloncesto
 * específica para un grupo de niños. Hereda de la clase abstracta {@code Reserva}.
 */
public class ReservaInfantil extends ReservaDTO {
	/**
	 * Número de infantes incluidos en la reserva infantil.
	 */
    private int numeroNinos;

    /**
     * Constructor vacío que llama al constructor vacío de la clase padre {@code Reserva}.
     */
    public ReservaInfantil() {
        super();
    }

    /**
     * Constructor parametrizado para crear una reserva infantil.
     *
     * @param idUsuario       El ID del usuario que realiza la reserva.
     * @param fechaHora       La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista        El ID de la pista que se está reservando.
     * @param numeroNinos     El número de niños incluidos en la reserva.
     */
    public ReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
        this.numeroNinos = numeroNinos;
    }

    /**
     * Obtiene el número de niños en la reserva.
     *
     * @return El número de niños.
     */
    public int getNumeroNinos() {
        return numeroNinos;
    }

    /**
     * Establece el número de niños en la reserva.
     *
     * @param numeroNinos El número de niños a establecer.
     */
    public void setNumeroNinos(int numeroNinos) {
        this.numeroNinos = numeroNinos;
    }

    /**
     * Devuelve una representación en cadena de la reserva infantil,
     * incluyendo detalles específicos.
     *
     * @return Una cadena que representa la reserva infantil.
     */
    @Override
    public String toString() {
        return "  Tipo de Reserva: Infantil\n" +
               "  Número de Niños: " + numeroNinos;
    }

    /**
     * Devuelve una representación específica de la reserva infantil.
     *
     * @return Una cadena que representa los detalles específicos de la reserva infantil.
     */
    public String toStringEspecifica() {
        return "numeroNinos=" + numeroNinos;
    }
}
