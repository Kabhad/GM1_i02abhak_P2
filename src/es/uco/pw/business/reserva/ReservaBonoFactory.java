package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase concreta de la fábrica de reservas para el tipo de reserva con bono.
 * 
 * Esta clase extiende de {@link ReservaFactory} y se encarga de crear diferentes tipos de reservas 
 * (Infantil, Familiar, Adulto) que utilizan un bono. La implementación de los métodos de creación de reservas
 * sigue el patrón de diseño Factory, donde la creación de las reservas específicas depende del tipo de usuario 
 * y del bono asociado a la reserva.
 */
public class ReservaBonoFactory extends ReservaFactory {

    /**
     * Crea una reserva para un jugador infantil utilizando un bono.
     * 
     * Este método crea una reserva con los detalles comunes (idUsuario, fechaHora, duracionMinutos, idPista) 
     * y asigna los detalles específicos para una reserva infantil. El bono y el número de sesión se asocian
     * a la reserva base.
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
    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos, Bono bono, int numeroSesion) {
        // Crear la reserva base con los detalles comunes
        ReservaBono reservaBase = new ReservaBono(idUsuario, fechaHora, duracionMinutos, idPista, bono, numeroSesion);
        
        // Crear la reserva específica para niños
        ReservaDTO reservaEspecifica = new ReservaInfantil(idUsuario, fechaHora, duracionMinutos, idPista, numeroNinos);
        
        // Asignar la reserva específica a la base
        reservaBase.setReservaEspecifica(reservaEspecifica);
        
        return reservaBase;
    }

    /**
     * Crea una reserva para una familia utilizando un bono.
     * 
     * Este método crea una reserva con los detalles comunes (idUsuario, fechaHora, duracionMinutos, idPista) 
     * y asigna los detalles específicos para una reserva familiar. El bono y el número de sesión se asocian
     * a la reserva base.
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
    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        // Crear la reserva base con los detalles comunes
        ReservaBono reservaBase = new ReservaBono(idUsuario, fechaHora, duracionMinutos, idPista, bono, numeroSesion);
        
        // Crear la reserva específica para familia
        ReservaDTO reservaEspecifica = new ReservaFamiliar(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos);
        
        // Asignar la reserva específica a la base
        reservaBase.setReservaEspecifica(reservaEspecifica);
        
        return reservaBase;
    }

    /**
     * Crea una reserva para un jugador adulto utilizando un bono.
     * 
     * Este método crea una reserva con los detalles comunes (idUsuario, fechaHora, duracionMinutos, idPista) 
     * y asigna los detalles específicos para una reserva de adulto. El bono y el número de sesión se asocian
     * a la reserva base.
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
    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, Bono bono, int numeroSesion) {
        // Crear la reserva base con los detalles comunes
        ReservaBono reservaBase = new ReservaBono(idUsuario, fechaHora, duracionMinutos, idPista, bono, numeroSesion);
        
        // Crear la reserva específica para adultos
        ReservaDTO reservaEspecifica = new ReservaAdulto(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos);
        
        // Asignar la reserva específica a la base
        reservaBase.setReservaEspecifica(reservaEspecifica);
        
        return reservaBase;
    }

    /**
     * Método no implementado en esta fábrica. Intenta crear una reserva infantil sin bono, lo cual no está permitido.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @throws UnsupportedOperationException Siempre lanza esta excepción, indicando que no se permiten reservas sin bono en esta fábrica.
     */
    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos) {
        throw new UnsupportedOperationException("Reserva individual no permitida en ReservaBonoFactory");
    }

    /**
     * Método no implementado en esta fábrica. Intenta crear una reserva familiar sin bono, lo cual no está permitido.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @param numeroNinos El número de niños incluidos en la reserva.
     * @throws UnsupportedOperationException Siempre lanza esta excepción, indicando que no se permiten reservas sin bono en esta fábrica.
     */
    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos) {
        throw new UnsupportedOperationException("Reserva individual no permitida en ReservaBonoFactory");
    }

    /**
     * Método no implementado en esta fábrica. Intenta crear una reserva de adulto sin bono, lo cual no está permitido.
     * 
     * @param idUsuario El ID del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El ID de la pista a reservar.
     * @param numeroAdultos El número de adultos incluidos en la reserva.
     * @throws UnsupportedOperationException Siempre lanza esta excepción, indicando que no se permiten reservas sin bono en esta fábrica.
     */
    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos) {
        throw new UnsupportedOperationException("Reserva individual no permitida en ReservaBonoFactory");
    }
}
