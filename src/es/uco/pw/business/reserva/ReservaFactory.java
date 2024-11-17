package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase abstracta que define el patrón de diseño Factory para la creación de diferentes tipos de reservas.
 * 
 * Esta clase proporciona los métodos abstractos para crear reservas de distintos tipos (Infantil, Familiar y Adulto),
 * tanto con bono como sin bono. Las clases concretas que extiendan esta clase deben implementar estos métodos para 
 * proporcionar la creación específica de cada tipo de reserva.
 */
public abstract class ReservaFactory {

    /**
     * Crea una reserva de tipo infantil sin bono.
     * 
     * Este método debe ser implementado en una clase concreta para crear una reserva de tipo infantil
     * con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, idPista y numeroNinos).
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva infantil.
     */
    public abstract ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos);

    /**
     * Crea una reserva de tipo familiar sin bono.
     * 
     * Este método debe ser implementado en una clase concreta para crear una reserva de tipo familiar
     * con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos y numeroNinos).
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva familiar.
     */
    public abstract ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos);

    /**
     * Crea una reserva de tipo adulto sin bono.
     * 
     * Este método debe ser implementado en una clase concreta para crear una reserva de tipo adulto
     * con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, idPista y numeroAdultos).
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva de adulto.
     */
    public abstract ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos);

    /**
     * Crea una reserva de tipo infantil con bono.
     * 
     * Este método debe ser implementado en una clase concreta para crear una reserva de tipo infantil
     * con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, idPista, numeroNinos), 
     * incluyendo el bono y el número de sesión asociados a la reserva.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @param bono El bono asociado a la reserva.
     * @param numeroSesion El número de sesión asociado al bono.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva infantil con bono.
     */
    public abstract ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos, Bono bono, int numeroSesion);

    /**
     * Crea una reserva de tipo familiar con bono.
     * 
     * Este método debe ser implementado en una clase concreta para crear una reserva de tipo familiar
     * con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos),
     * incluyendo el bono y el número de sesión asociados a la reserva.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @param bono El bono asociado a la reserva.
     * @param numeroSesion El número de sesión asociado al bono.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva familiar con bono.
     */
    public abstract ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion);

    /**
     * Crea una reserva de tipo adulto con bono.
     * 
     * Este método debe ser implementado en una clase concreta para crear una reserva de tipo adulto
     * con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos), 
     * incluyendo el bono y el número de sesión asociados a la reserva.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param bono El bono asociado a la reserva.
     * @param numeroSesion El número de sesión asociado al bono.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva de adulto con bono.
     */
    public abstract ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, Bono bono, int numeroSesion);
}
