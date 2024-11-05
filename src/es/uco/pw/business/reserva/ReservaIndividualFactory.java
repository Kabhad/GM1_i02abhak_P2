package es.uco.pw.business.reserva;

import java.util.Date;

import es.uco.pw.business.jugador.JugadorDTO;
import es.uco.pw.data.dao.JugadoresDAO;

/**
 * La clase {@code ReservaIndividualFactory} es una fábrica que crea
 * instancias de {@code ReservaIndividual} con reservas específicas
 * basadas en el tipo de usuario (infantil, familiar, adulto).
 */
public class ReservaIndividualFactory extends ReservaFactory {

    /**
     * Crea una reserva individual basada en el tipo de usuario y otros parámetros.
     *
     * @param tipoUsuario       El tipo de usuario (infantil, familiar, adulto).
     * @param idUsuario         El ID del usuario que realiza la reserva.
     * @param fechaHora         La fecha y hora de la reserva.
     * @param duracionMinutos   La duración de la reserva en minutos.
     * @param idPista          El ID de la pista que se está reservando.
     * @param numeroAdultos     El número de adultos en la reserva (si aplica).
     * @param numeroNinos       El número de niños en la reserva (si aplica).
     * @param tieneAntiguedad   Indica si el jugador tiene antigüedad para aplicar descuento.
     * @return Una instancia de {@code ReservaIndividual} con la reserva específica creada.
     * @throws IllegalArgumentException Si el tipo de usuario no es válido.
     */
    @Override
    public ReservaDTO crearReserva(String tipoUsuario, int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, boolean tieneAntiguedad) {
        ReservaDTO reservaEspecifica;
        JugadoresDAO jugadoresDAO = JugadoresDAO.getInstance();
        JugadorDTO jugadorDTO = jugadoresDAO.buscarJugadorPorId(idUsuario);

        // Crea la reserva específica según el tipo de usuario (infantil, familiar, adulto)
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

        // Verificar si el jugador fue encontrado antes de continuar
        if (jugadorDTO != null) {
            // Calculamos si el jugador tiene más de 2 años de antigüedad
            if (jugadorDTO.calcularAntiguedad() > 2) {
                reservaEspecifica.setDescuento(0.10f);  // Aplicar 10% de descuento
                System.out.println("Descuento del 10% aplicado para el jugador con ID: " + idUsuario);
            } else {
                System.out.println("El jugador con ID " + idUsuario + " no tiene antigüedad suficiente para el descuento.");
            }
        } else {
            System.out.println("Jugador no encontrado para el ID: " + idUsuario);
        }

        // Retornar una instancia de ReservaIndividual con la reserva específica y posible descuento aplicado
        return new ReservaIndividual(idUsuario, fechaHora, duracionMinutos, idPista, reservaEspecifica);
    }

    /**
     * Este método no está soportado en {@code ReservaIndividualFactory} y lanzará una excepción si se llama.
     *
     * @throws UnsupportedOperationException Siempre se lanza si se intenta crear una reserva de bono.
     */
    @Override
    public ReservaDTO crearReservaBono(String tipoUsuario, int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        throw new UnsupportedOperationException("ReservaIndividualFactory no puede crear reservas de bono.");
    }
}
