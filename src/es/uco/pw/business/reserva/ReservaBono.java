package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase que representa una reserva de pista de baloncesto utilizando un bono.
 * Extiende la clase Reserva y permite realizar reservas específicas (infantil, adulto, familiar) 
 * utilizando sesiones de un bono.
 */
public class ReservaBono extends ReservaDTO {
    private int idBono;
    private int numeroSesion;
    private Bono bono;
    private ReservaDTO reservaEspecifica; // Contiene la reserva específica (infantil, adulto, familiar)
    private boolean confirmada = false;

    /**
     * Constructor que incluye la reserva específica.
     * 
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     * @param bono El bono utilizado para la reserva.
     * @param numeroSesion El número de sesión del bono.
     * @param reservaEspecifica La reserva específica (infantil, adulto, familiar).
     */
    public ReservaBono(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, Bono bono, int numeroSesion, ReservaDTO reservaEspecifica) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
        this.bono = bono;
        this.idBono = bono.getIdBono();
        this.numeroSesion = numeroSesion;
        this.reservaEspecifica = reservaEspecifica; // Guardar la reserva específica (infantil, adulto, familiar)
        this.setDescuento(0.05f); // Descuento del 5% para todas las reservas de bono
    }

    /**
     * Obtiene la reserva específica asociada a esta reserva de bono.
     * 
     * @return La reserva específica.
     */
    public ReservaDTO getReservaEspecifica() {
        return reservaEspecifica;
    }

    /**
     * Retorna una representación en cadena de la reserva de bono.
     * 
     * @return Una cadena que representa la reserva de bono.
     */
    @Override
    public String toString() {
        String detallesEspecificos = "";

        if (reservaEspecifica instanceof ReservaInfantil) {
            detallesEspecificos = ((ReservaInfantil) reservaEspecifica).toStringEspecifica();
        } else if (reservaEspecifica instanceof ReservaAdulto) {
            detallesEspecificos = ((ReservaAdulto) reservaEspecifica).toStringEspecifica();
        } else if (reservaEspecifica instanceof ReservaFamiliar) {
            detallesEspecificos = ((ReservaFamiliar) reservaEspecifica).toStringEspecifica();
        }

        return "ReservaBono [" + super.toString() + 
               ", idBono=" + idBono +
               ", numeroSesion=" + numeroSesion +
               ", bono=" + bono +
               ", reservaEspecifica=" + detallesEspecificos + "]";
    }

    /**
     * Confirma la reserva y consume una sesión del bono.
     * 
     * @throws IllegalStateException si la reserva ya ha sido confirmada.
     */
    public void confirmarReserva() {
        if (!confirmada) {
            bono.consumirSesion(); // Consume la sesión solo si la reserva se confirma
            confirmada = true; // Cambia el estado a confirmado
        } else {
            throw new IllegalStateException("La reserva ya ha sido confirmada.");
        }
    }

    /**
     * Método para crear una reserva de bono de tipo infantil.
     * 
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     * @param numeroNinos El número de niños en la reserva.
     * @return Una nueva reserva infantil.
     */
    public ReservaInfantil crearReservaInfantil(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroNinos) {
        ReservaInfantil reserva = new ReservaInfantil(idUsuario, fechaHora, duracionMinutos, idPista, numeroNinos);
        reserva.setDescuento(0.05f); // Descuento para bono
        this.consumirSesion(); // Consume una sesión del bono
        return reserva;
    }

    /**
     * Método para crear una reserva de bono de tipo familiar.
     * 
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     * @param numeroAdultos El número de adultos en la reserva.
     * @param numeroNinos El número de niños en la reserva.
     * @return Una nueva reserva familiar.
     */
    public ReservaFamiliar crearReservaFamiliar(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos, int numeroNinos) {
        ReservaFamiliar reserva = new ReservaFamiliar(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos, numeroNinos);
        reserva.setDescuento(0.05f); // Descuento para bono
        this.consumirSesion(); // Consume una sesión del bono
        return reserva;
    }

    /**
     * Método para crear una reserva de bono de tipo adulto.
     * 
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     * @param numeroAdultos El número de adultos en la reserva.
     * @return Una nueva reserva para adultos.
     */
    public ReservaAdulto crearReservaAdulto(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, int numeroAdultos) {
        ReservaAdulto reserva = new ReservaAdulto(idUsuario, fechaHora, duracionMinutos, idPista, numeroAdultos);
        reserva.setDescuento(0.05f); // Descuento para bono
        this.consumirSesion(); // Consume una sesión del bono
        return reserva;
    }

    // Otros métodos de la clase

    /**
     * Obtiene el identificador del bono asociado a esta reserva.
     * 
     * @return El identificador del bono.
     */
    public int getIdBono() {
        return idBono;
    }

    /**
     * Establece el identificador del bono.
     * 
     * @param idBono El nuevo identificador del bono.
     */
    public void setIdBono(int idBono) {
        this.idBono = idBono;
    }

    /**
     * Obtiene el número de sesión del bono.
     * 
     * @return El número de sesión.
     */
    public int getNumeroSesion() {
        return numeroSesion;
    }

    /**
     * Establece el número de sesión del bono.
     * 
     * @param numeroSesion El nuevo número de sesión.
     */
    public void setNumeroSesion(int numeroSesion) {
        this.numeroSesion = numeroSesion;
    }

    /**
     * Obtiene el bono asociado a esta reserva.
     * 
     * @return El bono.
     */
    public Bono getBono() {
        return bono;
    }

    /**
     * Establece el bono asociado a esta reserva.
     * 
     * @param bono El nuevo bono.
     */
    public void setBono(Bono bono) {
        this.bono = bono;
    }

    /**
     * Verifica si la reserva ha sido confirmada.
     * 
     * @return true si la reserva está confirmada, false en caso contrario.
     */
    public boolean estaConfirmada() {
        return confirmada;
    }

    /**
     * Establece el descuento y lo aplica a la reserva específica si existe.
     * 
     * @param descuento El porcentaje de descuento a aplicar.
     */
    @Override
    public void setDescuento(float descuento) {
        super.setDescuento(descuento);
        if (reservaEspecifica != null) {
            reservaEspecifica.setDescuento(descuento); // Aplicar descuento también a la reserva específica
        }
    }

    /**
     * Consume una sesión del bono asociado a esta reserva.
     * 
     * @throws IllegalStateException si no quedan sesiones en el bono.
     */
    public void consumirSesion() {
        if (bono.getSesionesRestantes() > 0) {
            bono.consumirSesion();
        } else {
            throw new IllegalStateException("No quedan sesiones en el bono");
        }
    }
}
