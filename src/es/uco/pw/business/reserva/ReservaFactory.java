package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase abstracta que define una fábrica para crear reservas.
 * Proporciona métodos abstractos para crear reservas individuales 
 * y reservas de tipo bono, que deben ser implementados por las subclases.
 */
public abstract class ReservaFactory {
    
    /**
     * Método abstracto para crear reservas individuales.
     *
     * @param tipoUsuario El tipo de usuario para la reserva (infantil, familiar, adulto).
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     * @param numeroAdultos El número de adultos en la reserva (relevante para reservas familiares y adultas).
     * @param numeroNinos El número de niños en la reserva (relevante para reservas infantiles y familiares).
     * @param tieneAntiguedad Indica si el usuario tiene antigüedad para posibles descuentos.
     * @return Una nueva reserva de tipo Reserva.
     */
    public abstract ReservaDTO crearReserva(String tipoUsuario, int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, boolean tieneAntiguedad);

    /**
     * Método abstracto para crear reservas de tipo bono.
     *
     * @param tipoUsuario El tipo de usuario para la reserva (infantil, familiar, adulto).
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     * @param numeroAdultos El número de adultos en la reserva (relevante para reservas familiares y adultas).
     * @param numeroNinos El número de niños en la reserva (relevante para reservas infantiles y familiares).
     * @param bono El bono que se utilizará para la reserva.
     * @param numeroSesion El número de sesión del bono a consumir.
     * @return Una nueva reserva de tipo bono.
     */
    public abstract ReservaDTO crearReservaBono(String tipoUsuario, int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion);
}
