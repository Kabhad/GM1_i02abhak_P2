package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase que representa una reserva de pista de baloncesto para adultos.
 * Extiende la clase abstracta Reserva.
 */
public class ReservaAdulto extends ReservaDTO {
    // Atributo específico de ReservaAdulto
	/**
	 * Número de adultos incluidos en la reserva adulto.
	 */
    private int numeroAdultos;

    /**
     * Constructor vacío que llama al constructor vacío de la clase padre.
     */
    public ReservaAdulto() {
        super();
    }

    /**
     * Constructor parametrizado.
     * 
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     * @param numeroAdultos El número de adultos en la reserva.
     */
    public ReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
        this.numeroAdultos = numeroAdultos;
    }

    // Métodos get y set para numeroAdultos

    /**
     * Obtiene el número de adultos en la reserva.
     * 
     * @return El número de adultos.
     */
    public int getNumeroAdultos() {
        return numeroAdultos;
    }

    /**
     * Establece el número de adultos en la reserva.
     * 
     * @param numeroAdultos El nuevo número de adultos.
     */
    public void setNumeroAdultos(int numeroAdultos) {
        this.numeroAdultos = numeroAdultos;
    }

    /**
     * Retorna una representación en cadena de la reserva para adultos.
     * 
     * @return Una cadena que representa la reserva de adultos.
     */
    @Override
    public String toString() {
        return "  Tipo de Reserva: Adulto\n" +
               "  Número de Adultos: " + numeroAdultos;
    }


    /**
     * Retorna una representación específica del número de adultos en la reserva.
     * 
     * @return Una cadena que representa el número de adultos.
     */
    public String toStringEspecifica() {
        return "numeroAdultos=" + numeroAdultos;
    }
}
