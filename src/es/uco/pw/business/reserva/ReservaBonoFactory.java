package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase que representa una fábrica para crear reservas de tipo bono.
 * Extiende la clase ReservaFactory y permite crear reservas específicas 
 * (infantil, adulto, familiar) asociadas a un bono.
 */
public class ReservaBonoFactory extends ReservaFactory {

    /**
     * Método para crear una reserva genérica. 
     * Este método no es soportado por la ReservaBonoFactory.
     * 
     * @throws UnsupportedOperationException si se intenta llamar a este método.
     */
    @Override
    public ReservaDTO crearReserva(String tipoUsuario, int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, boolean tieneAntiguedad) {
        throw new UnsupportedOperationException("ReservaBonoFactory solo soporta reservas de bono.");
    }

    /**
     * Método para crear una reserva de bono con la reserva específica asociada.
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
     * @return Una nueva reserva de tipo bono con la reserva específica.
     * @throws IllegalArgumentException si el tipo de usuario no es válido.
     */
    @Override
    public ReservaDTO crearReservaBono(String tipoUsuario, int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        ReservaDTO reservaEspecifica;

        switch (tipoUsuario.toLowerCase()) {
            case "infantil":
                reservaEspecifica = new ReservaInfantil(idUsuario, fechaHora, duracionMinutos, idPista, numeroNinos);
                break;
            case "familiar":
                reservaEspecifica = new ReservaFamiliar(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos);
                break;
            case "adulto":
                reservaEspecifica = new ReservaAdulto(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos);
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario no válido: " + tipoUsuario);
        }
        
        reservaEspecifica.setDescuento(0.05f);  // Sincroniza el descuento en la reserva específica

        // Crear la reserva de bono con la reserva específica
        return new ReservaBono(idUsuario, fechaHora, duracionMinutos, idPista, bono, numeroSesion, reservaEspecifica);
    }
}
