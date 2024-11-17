package es.uco.pw.business.reserva;

import java.util.Date;
import java.util.Calendar;

/**
 * Clase que representa un bono de reservas de pistas de baloncesto.
 * Cada bono permite un número limitado de sesiones y tiene una fecha de caducidad.
 */
public class Bono {
    /**
     * Identificador único del bono.
     */
    private int idBono;

    /**
     * Identificador del usuario asociado al bono.
     */
    private int idUsuario;

    /**
     * Número de sesiones restantes en el bono. Comienza con 5 sesiones.
     */
    private int sesionesRestantes = 5; // Comienza con 5 sesiones

    /**
     * Fecha de caducidad del bono, que es un año desde la fecha de la primera reserva.
     */
    private Date fechaCaducidad; // Un año desde la primera reserva

    /** 
     * Constructor vacío.
     */
    public Bono() {
    }

    /**
     * Constructor parametrizado.
     * 
     * @param idBono El identificador del bono.
     * @param idUsuario El identificador del usuario asociado al bono.
     * @param numeroSesion El número de la sesión que se está utilizando.
     * @param fechaPrimeraReserva La fecha de la primera reserva.
     */
    public Bono(int idBono, int idUsuario, int numeroSesion, Date fechaPrimeraReserva) {
        this.idBono = idBono;
        this.idUsuario = idUsuario;
        this.sesionesRestantes = 5 - numeroSesion; // Calcular sesiones restantes
        this.fechaCaducidad = calcularFechaCaducidad(fechaPrimeraReserva);
    }

    /**
     * Calcula la fecha de caducidad del bono a partir de la fecha de la primera reserva.
     * 
     * @param fechaPrimeraReserva La fecha de la primera reserva.
     * @return La fecha de caducidad del bono.
     */
    public Date calcularFechaCaducidad(Date fechaPrimeraReserva) {
        // Lógica para calcular la fecha de caducidad a partir de la primera reserva (un año)
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaPrimeraReserva);
        cal.add(Calendar.YEAR, 1); // Añade un año
        return cal.getTime();
    }

    /**
     * Verifica si el bono ha caducado.
     * 
     * @return true si el bono está caducado, false en caso contrario.
     */
    public boolean estaCaducado() {
        return new Date().after(fechaCaducidad);
    }

    /**
     * Consume una sesión del bono. 
     * Si no quedan sesiones, lanza una excepción.
     * 
     * @throws IllegalStateException si no quedan sesiones en el bono.
     */
    public void consumirSesion() {
        if (sesionesRestantes > 0) {
            sesionesRestantes--;
        } else {
            throw new IllegalStateException("No quedan sesiones en el bono");
        }
    }

    // Getters y Setters

    /**
     * Obtiene el identificador del bono.
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
     * Obtiene el identificador del usuario asociado al bono.
     * 
     * @return El identificador del usuario.
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario asociado al bono.
     * 
     * @param idUsuario El nuevo identificador del usuario.
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el número de sesiones restantes en el bono.
     * 
     * @return El número de sesiones restantes.
     */
    public int getSesionesRestantes() {
        return sesionesRestantes;
    }

    /**
     * Establece el número de sesiones restantes en el bono.
     * 
     * @param sesionesRestantes El nuevo número de sesiones restantes.
     */
    public void setSesionesRestantes(int sesionesRestantes) {
        this.sesionesRestantes = sesionesRestantes;
    }

    /**
     * Obtiene la fecha de caducidad del bono.
     * 
     * @return La fecha de caducidad del bono.
     */
    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    /**
     * Establece la fecha de caducidad del bono.
     * 
     * @param fechaCaducidad La nueva fecha de caducidad.
     */
    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     * Retorna una representación en cadena del bono.
     * 
     * @return Una cadena que representa el bono.
     */
    @Override
    public String toString() {
        return "[ID Bono: " + idBono +
               ", ID Usuario: " + idUsuario +
               ", Sesiones Restantes: " + sesionesRestantes +
               ", Fecha de Caducidad: " + fechaCaducidad + "]";
    }

}
