package es.uco.pw.business.reserva;

import java.util.Date;

public abstract class ReservaFactory {

    // Métodos abstractos para reservas sin bono
    public abstract ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos);
    public abstract ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos);
    public abstract ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos);

    // Métodos abstractos para reservas con bono
    public abstract ReservaDTO crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos, Bono bono, int numeroSesion);
    public abstract ReservaDTO crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos, Bono bono, int numeroSesion);
    public abstract ReservaDTO crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, Bono bono, int numeroSesion);

    // Método de validación de bonos
    protected void validarBono(Bono bono) {
        if (bono.estaCaducado()) {
            throw new IllegalArgumentException("El bono ha caducado y no puede ser utilizado para esta reserva.");
        }
        if (bono.getSesionesRestantes() <= 0) {
            throw new IllegalStateException("El bono no tiene sesiones restantes disponibles.");
        }
    }
}
