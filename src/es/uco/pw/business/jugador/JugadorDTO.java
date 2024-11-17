package es.uco.pw.business.jugador;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Representa un jugador con atributos como nombre, fecha de nacimiento, correo electrónico, 
 * y otros detalles relacionados con su cuenta y su inscripción en el sistema.
 * Cada jugador tiene un ID único que se genera automáticamente.
 */
public class JugadorDTO {

    // Atributos

    /**
     * ID único del jugador.
     */
    private int idJugador;

    /**
     * Nombre y apellidos del jugador.
     */
    private String nombreApellidos;

    /**
     * Fecha de nacimiento del jugador.
     */
    private Date fechaNacimiento;

    /**
     * Fecha de inscripción del jugador en el sistema. 
     * Puede ser null si el jugador no está inscrito.
     */
    private Date fechaInscripcion;

    /**
     * Correo electrónico del jugador.
     */
    private String correoElectronico;

    /**
     * Indica si la cuenta del jugador está activa. 
     * Por defecto, es true.
     */
    private boolean cuentaActiva = true; // Campo para indicar si la cuenta está activa
    /**
     * Constructor vacío. Asigna un ID único y activa la cuenta por defecto.
     */
    public JugadorDTO() {
        this.cuentaActiva = true; // La cuenta es activa por defecto
    }

    /**
     * Constructor parametrizado que permite definir nombre, fecha de nacimiento y correo electrónico.
     *
     * @param nombreApellidos   Nombre completo del jugador.
     * @param fechaNacimiento   Fecha de nacimiento del jugador.
     * @param correoElectronico Correo electrónico del jugador.
     */
    public JugadorDTO(String nombreApellidos, Date fechaNacimiento, String correoElectronico) {
        this();
        this.nombreApellidos = nombreApellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.correoElectronico = correoElectronico;
    }

    // Métodos getter y setter

    /**
     * Obtiene el ID del jugador.
     *
     * @return ID único del jugador.
     */
    public int getIdJugador() {
        return idJugador;
    }

    /**
     * Define el ID del jugador.
     *
     * @param idJugador ID único del jugador.
     */
    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    /**
     * Verifica si la cuenta del jugador está activa.
     *
     * @return true si la cuenta está activa, false en caso contrario.
     */
    public boolean isCuentaActiva() {
        return cuentaActiva;
    }

    /**
     * Activa o desactiva la cuenta del jugador.
     *
     * @param cuentaActiva Estado de la cuenta del jugador.
     */
    public void setCuentaActiva(boolean cuentaActiva) {
        this.cuentaActiva = cuentaActiva;
    }

    /**
     * Obtiene el nombre completo del jugador.
     *
     * @return Nombre completo del jugador.
     */
    public String getNombreApellidos() {
        return nombreApellidos;
    }

    /**
     * Define el nombre completo del jugador.
     *
     * @param nombreApellidos Nombre completo del jugador.
     */
    public void setNombreApellidos(String nombreApellidos) {
        this.nombreApellidos = nombreApellidos;
    }

    /**
     * Obtiene la fecha de nacimiento del jugador.
     *
     * @return Fecha de nacimiento del jugador.
     */
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Define la fecha de nacimiento del jugador.
     *
     * @param fechaNacimiento Fecha de nacimiento del jugador.
     */
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Obtiene la fecha de inscripción del jugador.
     *
     * @return Fecha de inscripción del jugador, o null si no está inscrito.
     */
    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    /**
     * Define la fecha de inscripción del jugador.
     *
     * @param fechaInscripcion Fecha de inscripción del jugador.
     */
    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    /**
     * Obtiene el correo electrónico del jugador.
     *
     * @return Correo electrónico del jugador.
     */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    /**
     * Define el correo electrónico del jugador.
     *
     * @param correoElectronico Correo electrónico del jugador.
     */
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    /**
     * Proporciona una representación en formato de cadena de la información del jugador.
     *
     * @return Una cadena con los detalles del jugador.
     */
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "ID: " + idJugador + 
               "\nNombre: " + nombreApellidos +
               "\nFecha de Nacimiento: " + sdf.format(fechaNacimiento) +
               "\nFecha de Inscripción: " + (fechaInscripcion != null ? sdf.format(fechaInscripcion) : "No inscrito") +
               "\nCorreo Electrónico: " + correoElectronico;
    }

    /**
     * Calcula los años de antigüedad del jugador desde su fecha de inscripción.
     * 
     * @return Años de antigüedad, o 0 si el jugador no está inscrito.
     */
    public int calcularAntiguedad() {
        if (fechaInscripcion == null) {
            return 0; // Si no está inscrito, no tiene antigüedad
        }
        Calendar fechaActual = Calendar.getInstance();
        Calendar fechaInscripcionCal = Calendar.getInstance();
        fechaInscripcionCal.setTime(fechaInscripcion);

        int aniosAntiguedad = fechaActual.get(Calendar.YEAR) - fechaInscripcionCal.get(Calendar.YEAR);

        // Si aún no ha pasado la fecha de inscripción en este año, restamos 1
        if (fechaActual.get(Calendar.DAY_OF_YEAR) < fechaInscripcionCal.get(Calendar.DAY_OF_YEAR)) {
            aniosAntiguedad--;
        }

        return aniosAntiguedad;
    }
}
