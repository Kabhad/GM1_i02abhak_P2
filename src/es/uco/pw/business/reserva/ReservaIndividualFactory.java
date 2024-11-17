package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Fábrica concreta que implementa la creación de reservas sin bono.
 * 
 * La clase {@link ReservaIndividualFactory} es una implementación de la clase abstracta {@link ReservaFactory} 
 * que se encarga de crear reservas individuales de tipo Infantil, Familiar y Adulto. Esta fábrica no permite la creación 
 * de reservas con bono, ya que los métodos correspondientes lanzan una excepción {@link UnsupportedOperationException}.
 * 
 * @see ReservaFactory
 */
public class ReservaIndividualFactory extends ReservaFactory {

    /**
     * Crea una reserva de tipo infantil sin bono.
     * 
     * Este método crea una reserva de tipo infantil con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, 
     * idPista y numeroNinos). La reserva se crea como una instancia de {@link ReservaIndividual} y se le asigna una 
     * reserva específica de tipo {@link ReservaInfantil}.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva infantil.
     */
    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos) {
        ReservaIndividual reservaBase = new ReservaIndividual(idUsuario, fechaHora, duracionMinutos, idPista);
        ReservaDTO reservaEspecifica = new ReservaInfantil(idUsuario, fechaHora, duracionMinutos, idPista, numeroNinos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    /**
     * Crea una reserva de tipo familiar sin bono.
     * 
     * Este método crea una reserva de tipo familiar con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, 
     * idPista, numeroAdultos y numeroNinos). La reserva se crea como una instancia de {@link ReservaIndividual} y se le asigna 
     * una reserva específica de tipo {@link ReservaFamiliar}.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva familiar.
     */
    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos) {
        ReservaIndividual reservaBase = new ReservaIndividual(idUsuario, fechaHora, duracionMinutos, idPista);
        ReservaDTO reservaEspecifica = new ReservaFamiliar(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    /**
     * Crea una reserva de tipo adulto sin bono.
     * 
     * Este método crea una reserva de tipo adulto con los detalles proporcionados (idUsuario, fechaHora, duracionMinutos, 
     * idPista y numeroAdultos). La reserva se crea como una instancia de {@link ReservaIndividual} y se le asigna una reserva 
     * específica de tipo {@link ReservaAdulto}.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @return Una instancia de {@link ReservaDTO} que representa la reserva de adulto.
     */
    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos) {
        ReservaIndividual reservaBase = new ReservaIndividual(idUsuario, fechaHora, duracionMinutos, idPista);
        ReservaDTO reservaEspecifica = new ReservaAdulto(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    /**
     * Método no implementado que crea una reserva de tipo infantil con bono.
     * 
     * Este método lanza una excepción {@link UnsupportedOperationException} porque no está permitido crear 
     * reservas con bono en la clase {@link ReservaIndividualFactory}.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @param bono El bono asociado a la reserva.
     * @param numeroSesion El número de sesión asociado al bono.
     * @throws UnsupportedOperationException Si se intenta crear una reserva con bono.
     */
    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos, Bono bono, int numeroSesion) {
        throw new UnsupportedOperationException("No se puede crear una reserva con bono en ReservaIndividualFactory");
    }

    /**
     * Método no implementado que crea una reserva de tipo familiar con bono.
     * 
     * Este método lanza una excepción {@link UnsupportedOperationException} porque no está permitido crear 
     * reservas con bono en la clase {@link ReservaIndividualFactory}.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @param bono El bono asociado a la reserva.
     * @param numeroSesion El número de sesión asociado al bono.
     * @throws UnsupportedOperationException Si se intenta crear una reserva con bono.
     */
    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        throw new UnsupportedOperationException("No se puede crear una reserva con bono en ReservaIndividualFactory");
    }

    /**
     * Método no implementado que crea una reserva de tipo adulto con bono.
     * 
     * Este método lanza una excepción {@link UnsupportedOperationException} porque no está permitido crear 
     * reservas con bono en la clase {@link ReservaIndividualFactory}.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param bono El bono asociado a la reserva.
     * @param numeroSesion El número de sesión asociado al bono.
     * @throws UnsupportedOperationException Si se intenta crear una reserva con bono.
     */
    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, Bono bono, int numeroSesion) {
        throw new UnsupportedOperationException("No se puede crear una reserva con bono en ReservaIndividualFactory");
    }
}
