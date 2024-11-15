package es.uco.pw.business.reserva;

import java.util.Date;

public class ReservaIndividualFactory extends ReservaFactory {

    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos) {
        ReservaIndividual reservaBase = new ReservaIndividual(idUsuario, fechaHora, duracionMinutos, idPista);
        ReservaDTO reservaEspecifica = new ReservaInfantil(idUsuario, fechaHora, duracionMinutos, idPista, numeroNinos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos) {
        ReservaIndividual reservaBase = new ReservaIndividual(idUsuario, fechaHora, duracionMinutos, idPista);
        ReservaDTO reservaEspecifica = new ReservaFamiliar(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos) {
        ReservaIndividual reservaBase = new ReservaIndividual(idUsuario, fechaHora, duracionMinutos, idPista);
        ReservaDTO reservaEspecifica = new ReservaAdulto(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos);
        reservaBase.setReservaEspecifica(reservaEspecifica);
        return reservaBase;
    }

    // Métodos para reservas con bono no implementados en esta fábrica
    @Override
    public ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos, Bono bono, int numeroSesion) {
        throw new UnsupportedOperationException("No se puede crear una reserva con bono en ReservaIndividualFactory");
    }

    @Override
    public ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion) {
        throw new UnsupportedOperationException("No se puede crear una reserva con bono en ReservaIndividualFactory");
    }

    @Override
    public ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, Bono bono, int numeroSesion) {
        throw new UnsupportedOperationException("No se puede crear una reserva con bono en ReservaIndividualFactory");
    }
}
