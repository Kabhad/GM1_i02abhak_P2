package es.uco.pw.business.reserva;

import java.util.Date;

/**
 * Clase abstracta que representa una reserva de pista de baloncesto.
 * Contiene atributos comunes a todas las reservas y métodos para calcular precios.
 */
public abstract class ReservaDTO {
    // Atributos comunes a todas las reservas
	private int idReserva; // Nuevo campo
    private int idUsuario;
    private Date fechaHora;
    private int duracionMinutos;
    private int idPista;
    private float precio;
    private float descuento;

    /** 
     * Constructor vacío.
     */
    public ReservaDTO() {
    }

    /**
     * Constructor parametrizado.
     * 
     * @param idUsuario El identificador del usuario que realiza la reserva.
     * @param fechaHora La fecha y hora de la reserva.
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param idPista El identificador de la pista reservada.
     */
    public ReservaDTO(int idUsuario, Date fechaHora, int duracionMinutos, int idPista) {
        this.idUsuario = idUsuario;
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
        this.idPista = idPista;
        this.precio = calcularPrecio(duracionMinutos, 0); // Calcula el precio automáticamente sin descuento inicialmente
        this.descuento = 0; // El descuento se aplicará después si es necesario
    }
    
    /**
     * Cálculo de precios basado en la duración, aplicando el descuento si existe.
     * 
     * @param duracionMinutos La duración de la reserva en minutos.
     * @param descuento El porcentaje de descuento a aplicar.
     * @return El precio calculado de la reserva.
     * @throws IllegalArgumentException si la duración no es válida.
     */
    public static float calcularPrecio(int duracionMinutos, float descuento) {
        float precioBase;
        switch (duracionMinutos) {
            case 60: precioBase = 20.0f; break;
            case 90: precioBase = 30.0f; break;
            case 120: precioBase = 40.0f; break;
            default: throw new IllegalArgumentException("Duración no válida");
        }
        // Aplica el descuento si existe
        return precioBase * (1 - descuento);
    }

    // Métodos get y set para cada atributo

    /**
     * Obtiene el identificador del usuario que realizó la reserva.
     * 
     * @return El identificador del usuario.
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario que realiza la reserva.
     * 
     * @param idUsuario El nuevo identificador del usuario.
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene la fecha y hora de la reserva.
     * 
     * @return La fecha y hora de la reserva.
     */
    public Date getFechaHora() {
        return fechaHora;
    }

    /**
     * Establece la fecha y hora de la reserva.
     * 
     * @param fechaHora La nueva fecha y hora de la reserva.
     */
    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * Obtiene la duración de la reserva en minutos.
     * 
     * @return La duración de la reserva en minutos.
     */
    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    /**
     * Establece la duración de la reserva en minutos.
     * 
     * @param duracionMinutos La nueva duración de la reserva en minutos.
     */
    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    /**
     * Obtiene el identificador de la pista reservada.
     * 
     * @return El identificador de la pista.
     */
    public int getIdPista() {
        return idPista;
    }

    /**
     * Establece el identificador de la pista reservada.
     * 
     * @param idPista El nuevo identificador de la pista.
     */
    public void setIdPista(int idPista) {
        this.idPista = idPista;
    }

    /**
     * Obtiene el precio de la reserva.
     * 
     * @return El precio de la reserva.
     */
    public float getPrecio() {
        return precio;
    }

    /**
     * Establece el precio de la reserva.
     * 
     * @param precio El nuevo precio de la reserva.
     */
    public void setPrecio(float precio) {
        this.precio = precio;
    }

    /**
     * Obtiene el porcentaje de descuento aplicado a la reserva.
     * 
     * @return El porcentaje de descuento.
     */
    public float getDescuento() {
        return descuento;
    }
    
    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }


    /**
     * Establece el porcentaje de descuento aplicado a la reserva y recalcula el precio.
     * 
     * @param descuento El nuevo porcentaje de descuento.
     */
    public void setDescuento(float descuento) {
        this.descuento = descuento;
        this.precio = calcularPrecio(this.duracionMinutos, descuento); // Recalcular el precio al aplicar descuento
    }
    
    /**
     * Retorna una representación en cadena de la reserva.
     * 
     * @return Una cadena que representa la reserva.
     */
    @Override
    public String toString() {
        return "Reserva [idUsuario=" + idUsuario + ", fechaHora=" + fechaHora + ", duracionMinutos=" + duracionMinutos
                + ", idPista=" + idPista + ", precio=" + precio + ", descuento=" + descuento + "]";
    }
}
