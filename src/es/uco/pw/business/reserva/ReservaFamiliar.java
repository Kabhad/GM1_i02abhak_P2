package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase que representa una reserva familiar en el sistema.
 * Extiende la clase abstracta Reserva, añadiendo atributos específicos para reservas familiares.
 */
public class ReservaFamiliar extends ReservaDTO {
	/**
	 * Número de adultos incluidos en la reserva familiar.
	 */
	private int numeroAdultos;

	/**
	 * Número de niños incluidos en la reserva familiar.
	 */
	private int numeroNinos;


	/**
	 * Constructor vacío de la clase `ReservaFamiliar`.
	 * Llama al constructor vacío de la clase padre `ReservaDTO`.
	 */    public ReservaFamiliar() {
        super();
    }

	 /**
	  * Constructor parametrizado de la clase `ReservaFamiliar`.
	  * 
	  * @param idUsuario       El identificador del usuario que realiza la reserva.
	  * @param fechaHora       La fecha y hora de la reserva.
	  * @param duracionMinutos La duración de la reserva en minutos.
	  * @param idPista         El identificador de la pista reservada.
	  * @param numeroAdultos   El número de adultos incluidos en la reserva.
	  * @param numeroNinos     El número de niños incluidos en la reserva.
	  */
    public ReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
        this.numeroAdultos = numeroAdultos;
        this.numeroNinos = numeroNinos;
    }

    /**
     * Obtiene el número de adultos incluidos en la reserva familiar.
     * 
     * @return El número de adultos en la reserva.
     */
    public int getNumeroAdultos() {
        return numeroAdultos;
    }

    /**
     * Establece el número de adultos incluidos en la reserva familiar.
     * 
     * @param numeroAdultos El nuevo número de adultos en la reserva.
     */
    public void setNumeroAdultos(int numeroAdultos) {
        this.numeroAdultos = numeroAdultos;
    }

    /**
     * Obtiene el número de niños incluidos en la reserva familiar.
     * 
     * @return El número de niños en la reserva.
     */
    public int getNumeroNinos() {
        return numeroNinos;
    }

    /**
     * Establece el número de niños incluidos en la reserva familiar.
     * 
     * @param numeroNinos El nuevo número de niños en la reserva.
     */
    public void setNumeroNinos(int numeroNinos) {
        this.numeroNinos = numeroNinos;
    }

    @Override
    public String toString() {
        return "  Tipo de Reserva: Familiar\n" +
               "  Número de Adultos: " + numeroAdultos + "\n" +
               "  Número de Niños: " + numeroNinos;
    }


    /**
     * Método que devuelve una representación específica de la reserva familiar.
     *
     * @return Una cadena con el número de adultos y niños en la reserva.
     */
    public String toStringEspecifica() {
        return "numeroAdultos=" + numeroAdultos + ", numeroNinos=" + numeroNinos;
    }
}
