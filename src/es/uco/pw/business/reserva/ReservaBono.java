package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase que representa una reserva de pista de baloncesto utilizando un bono.
 * Extiende la clase Reserva y utiliza sesiones de un bono.
 */
public class ReservaBono extends ReservaDTO {
    /**
     * Identificador único del bono.
     */
    private int idBono;
    /**
     * Número de sesiones consumidas en el bono..
     */
    private int numeroSesion;
    /**
     * Objeto bono, con eso se hacen reservas de bono y se asocia la info del bono a una reserva.
     */
    private Bono bono;

    /**
     * Constructor para crear una reserva con bono.
     *
     * @param idUsuario       El identificador del usuario que realiza la reserva.
     * @param fechaHora       La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista         El identificador de la pista reservada.
     * @param bono            El bono utilizado para la reserva.
     * @param numeroSesion    El número de sesión del bono.
     */
    public ReservaBono(int idUsuario, Date fechaHora, int duracionMinutos, int idPista, Bono bono, int numeroSesion) {
        super(idUsuario, fechaHora, duracionMinutos, idPista);
        this.bono = bono;
        this.idBono = bono.getIdBono();
        this.numeroSesion = numeroSesion;
        this.setDescuento(0.05f); // Descuento del 5% para todas las reservas de bono
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Reserva de Bono:\n");
        sb.append(super.toString()) // Llama al toString de ReservaDTO para obtener los detalles comunes
          .append("\n  Bono ID: ").append(idBono)
          .append("\n  Sesión número: ").append(numeroSesion)
          .append("\n  Sesiones restantes: ").append(bono.getSesionesRestantes())
          .append("\n  Fecha de Caducidad del Bono: ").append(bono.getFechaCaducidad());
        
        return sb.toString();
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
