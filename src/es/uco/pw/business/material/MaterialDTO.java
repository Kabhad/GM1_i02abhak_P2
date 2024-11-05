package es.uco.pw.business.material;

/**
 * Clase que representa un material que puede ser utilizado en las pistas.
 */
public class MaterialDTO {
    
    private int id;                  // Identificador del material
    private TipoMaterial tipo;       // Tipo de material
    private boolean usoExterior;     // Indica si el material es apto para uso exterior
    private EstadoMaterial estado;    // Estado actual del material
    
    /**
     * Constructor por defecto.
     */
    public MaterialDTO() {
        
    }
    
    /**
     * Constructor que inicializa un material con los parámetros dados.
     *
     * @param id           Identificador del material.
     * @param tipo        Tipo de material.
     * @param usoExterior Indica si el material es apto para uso exterior.
     * @param estado      Estado del material.
     */
    public MaterialDTO(int id, TipoMaterial tipo, boolean usoExterior, EstadoMaterial estado) {
        this.id = id;
        this.tipo = tipo;
        this.usoExterior = usoExterior;
        this.estado = estado;
    }

    /**
     * Obtiene el identificador del material.
     *
     * @return El identificador del material.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador del material.
     *
     * @param id El nuevo identificador del material.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de material.
     *
     * @return El tipo de material.
     */
    public TipoMaterial getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de material.
     *
     * @param tipo El nuevo tipo de material.
     */
    public void setTipo(TipoMaterial tipo) {
        this.tipo = tipo;
    }

    /**
     * Indica si el material es apto para uso exterior.
     *
     * @return true si el material es para uso exterior; false en caso contrario.
     */
    public boolean isUsoExterior() {
        return usoExterior;
    }

    /**
     * Establece si el material es apto para uso exterior.
     *
     * @param usoExterior true si el material es para uso exterior; false en caso contrario.
     */
    public void setUsoExterior(boolean usoExterior) {
        this.usoExterior = usoExterior;
    }

    /**
     * Obtiene el estado del material.
     *
     * @return El estado del material.
     */
    public EstadoMaterial getEstado() {
        return estado;
    }

    /**
     * Establece el estado del material.
     *
     * @param estado El nuevo estado del material.
     */
    public void setEstado(EstadoMaterial estado) {
        this.estado = estado;
    }

    /**
     * Devuelve una representación en forma de cadena del material.
     *
     * @return Una cadena que representa el material.
     */
    @Override
    public String toString() {
        return "Material [id=" + id + ", tipo=" + tipo + ", usoExterior=" + usoExterior + ", estado=" + estado + "]";
    }
}

