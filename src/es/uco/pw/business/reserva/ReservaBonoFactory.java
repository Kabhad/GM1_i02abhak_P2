package es.uco.pw.business.reserva;

import java.util.Date;

public class ReservaBonoFactory extends ReservaFactory {

    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos, Bono bono, int numeroSesion) {
        validarBono(bono);
        ReservaBono reservaBase = new ReservaBono(idUsuario, fechaHora, duracionMinutos, idPista, bono, numeroSesion);
        ReservaDTO reservaEspecifica = new ReservaInfantil(idUsuario, fechaHora, duracionMinutos, idPista, numeroNinos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        validarBono(bono);
        ReservaBono reservaBase = new ReservaBono(idUsuario, fechaHora, duracionMinutos, idPista, bono, numeroSesion);
        ReservaDTO reservaEspecifica = new ReservaFamiliar(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, Bono bono, int numeroSesion) {
        validarBono(bono);
        ReservaBono reservaBase = new ReservaBono(idUsuario, fechaHora, duracionMinutos, idPista, bono, numeroSesion);
        ReservaDTO reservaEspecifica = new ReservaAdulto(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    // Métodos para reservas sin bono no implementados en esta fábrica
    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos) {
        throw new UnsupportedOperationException("Reserva individual no permitida en ReservaBonoFactory");
    }

    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos) {
        throw new UnsupportedOperationException("Reserva individual no permitida en ReservaBonoFactory");
    }

    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos) {
        throw new UnsupportedOperationException("Reserva individual no permitida en ReservaBonoFactory");
    }
}
