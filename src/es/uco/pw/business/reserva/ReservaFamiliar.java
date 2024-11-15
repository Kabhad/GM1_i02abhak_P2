package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase que representa una reserva familiar en el sistema.
 * Extiende la clase abstracta Reserva, añadiendo atributos específicos para reservas familiares.
 */
public class ReservaFamiliar extends ReservaDTO {
    // Atributos específicos de ReservaFamiliar
    private int numeroAdultos;
    private int numeroNinos;

    // Constructor vacío, llama al constructor vacío de Reserva, la clase padre
    public ReservaFamiliar() {
        super();
    }

    // Constructor parametrizado
    public ReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
        this.numeroAdultos = numeroAdultos;
        this.numeroNinos = numeroNinos;
    }

    // Métodos get y set para numeroAdultos y numeroNinos
    public int getNumeroAdultos() {
        return numeroAdultos;
    }

    public void setNumeroAdultos(int numeroAdultos) {
        this.numeroAdultos = numeroAdultos;
    }

    public int getNumeroNinos() {
        return numeroNinos;
    }

    public void setNumeroNinos(int numeroNinos) {
        this.numeroNinos = numeroNinos;
    }

    @Override
    public String toString() {
        return "  Tipo de Reserva: Familiar\n" +
               "  Número de Adultos: " + numeroAdultos + "\n" +
               "  Número de Niños: " + numeroNinos;
    }


    /**
     * Método que devuelve una representación específica de la reserva familiar.
     *
     * @return Una cadena con el número de adultos y niños en la reserva.
     */
    public String toStringEspecifica() {
        return "numeroAdultos=" + numeroAdultos + ", numeroNinos=" + numeroNinos;
    }
}
