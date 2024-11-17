package es.uco.pw.business.pista;

import java.util.ArrayList;
import java.util.List;
import es.uco.pw.business.material.*;

/**
 * Clase que representa una pista deportiva, la cual puede estar en interiores o exteriores y 
 * tiene un tamaño y materiales asociados.
 * Proporciona métodos para gestionar el estado y los materiales de la pista.
 */
public class PistaDTO {

	/**
	 * Identificador único de la pista.
	 */
	private int idPista;

	/**
	 * Nombre de la pista.
	 */
	private String nombrePista;

	/**
	 * Indica si la pista está disponible para reservas.
	 */
	private boolean disponible;

	/**
	 * Indica si la pista es de tipo exterior.
	 */
	private boolean exterior;

	/**
	 * Tamaño de la pista (pequeña, mediana, grande).
	 */
	private TamanoPista pista;

	/**
	 * Número máximo de jugadores permitidos en la pista.
	 */
	private int max_jugadores;

	/**
	 * Lista de materiales asociados a la pista.
	 */
	private List<MaterialDTO> materiales;


    /**
     * Constructor vacío que inicializa una pista con un ID único y una lista vacía de materiales.
     */
    public PistaDTO() {
        this.materiales = new ArrayList<>(); // Inicializar la lista de materiales
    }

    /**
     * Constructor parametrizado para inicializar una pista con sus atributos.
     *
     * @param idPista       ID de la pista.
     * @param nombre        Nombre de la pista.
     * @param disponible    Estado de disponibilidad de la pista.
     * @param exterior      Si la pista es exterior o no.
     * @param tamanoPista   Tamaño de la pista.
     * @param maxJugadores  Número máximo de jugadores permitidos.
     */
    public PistaDTO(int idPista, String nombre, boolean disponible, boolean exterior, TamanoPista tamanoPista, int maxJugadores) {
        this.idPista = idPista;
        this.nombrePista = nombre;
        this.disponible = disponible;
        this.exterior = exterior;
        this.pista = tamanoPista;
        this.max_jugadores = maxJugadores;
        this.materiales = crearListaMateriales();
    }

    /**
     * Constructor parametrizado para inicializar una pista con sus atributos.
     *
     * @param nombrePista   Nombre de la pista.
     * @param disponible    Estado de disponibilidad de la pista.
     * @param exterior      Si la pista es exterior o no.
     * @param pista         Tamaño de la pista.
     * @param max_jugadores Número máximo de jugadores permitidos.
     */
    public PistaDTO(String nombrePista, boolean disponible, boolean exterior, TamanoPista pista, int max_jugadores) {
        this();
        this.nombrePista = nombrePista;
        this.disponible = disponible;
        this.exterior = exterior;
        this.pista = pista;
        this.max_jugadores = max_jugadores;
    }

    /**
     * Obtiene el identificador único de la pista.
     *
     * @return El identificador de la pista.
     */
    public int getIdPista() {
        return idPista;
    }

    /**
     * Establece el ID único de la pista.
     *
     * @param idPista ID a asignar.
     */
    public void setIdPista(int idPista) {
        this.idPista = idPista;
    }

    /**
     * Obtiene el nombre de la pista.
     *
     * @return Nombre de la pista.
     */
    public String getNombrePista() {
        return nombrePista;
    }

    /**
     * Establece el nombre de la pista.
     *
     * @param nombrePista Nombre a asignar.
     */
    public void setNombrePista(String nombrePista) {
        this.nombrePista = nombrePista;
    }

    /**
     * Verifica si la pista está disponible.
     *
     * @return True si la pista está disponible, False en caso contrario.
     */
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Establece el estado de disponibilidad de la pista.
     *
     * @param disponible Estado de disponibilidad a asignar.
     */
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Verifica si la pista es exterior.
     *
     * @return True si la pista es exterior, False en caso contrario.
     */
    public boolean isExterior() {
        return exterior;
    }

    /**
     * Establece si la pista es exterior.
     *
     * @param exterior Estado exterior a asignar.
     */
    public void setExterior(boolean exterior) {
        this.exterior = exterior;
    }

    /**
     * Obtiene el tamaño de la pista.
     *
     * @return Tamaño de la pista.
     */
    public TamanoPista getPista() {
        return pista;
    }

    /**
     * Establece el tamaño de la pista.
     *
     * @param pista Tamaño a asignar.
     */
    public void setPista(TamanoPista pista) {
        this.pista = pista;
    }

    /**
     * Obtiene el número máximo de jugadores permitidos en la pista.
     *
     * @return Número máximo de jugadores.
     */
    public int getMax_jugadores() {
        return max_jugadores;
    }

    /**
     * Establece el número máximo de jugadores permitidos en la pista.
     *
     * @param max_jugadores Número máximo de jugadores a asignar.
     */
    public void setMax_jugadores(int max_jugadores) {
        this.max_jugadores = max_jugadores;
    }

    /**
     * Obtiene la lista de materiales asociados a la pista.
     *
     * @return Lista de materiales.
     */
    public List<MaterialDTO> getMateriales() {
        return materiales;
    }

    /**
     * Establece la lista de materiales asociados a la pista.
     *
     * @param materiales Lista de materiales a asignar.
     */
    public void setMateriales(List<MaterialDTO> materiales) {
        this.materiales = materiales;
    }

    /**
     * Representación en formato de texto de la pista, mostrando sus atributos.
     *
     * @return Cadena de texto con los atributos de la pista.
     */
    @Override
    public String toString() {
        return "ID: " + idPista +
               "\nNombre Pista: " + nombrePista + 
               "\nDisponible: " + disponible +
               "\nExterior: " + exterior +
               "\nTamaño Pista: " + pista +
               "\nMax Jugadores: " + max_jugadores +
               "\nMateriales: " + materiales;
    }

    /**
     * Consulta y devuelve una lista de materiales disponibles en la pista.
     *
     * @return Lista de materiales disponibles.
     */
    public List<MaterialDTO> consultarMaterialesDisponibles() {
        List<MaterialDTO> materialesDisponibles = crearListaMateriales(); // Usar el método de fábrica
        for (MaterialDTO materialDTO : materiales) {
            if (materialDTO.getEstado() == EstadoMaterial.DISPONIBLE) {
                materialesDisponibles.add(materialDTO);
            }
        }
        return materialesDisponibles;
    }

    /**
     * Excepción personalizada para indicar incompatibilidad de material con la pista.
     */
    public static class MaterialIncompatibleException extends Exception {
    	/**
    	 * Identificador para la serialización de la clase.
    	 */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor que acepta un mensaje.
         *
         * @param message Mensaje de la excepción.
         */
        public MaterialIncompatibleException(String message) {
            super(message);
        }
    }

    /**
     * Excepción personalizada para indicar que se ha alcanzado el máximo de materiales.
     */
    public static class MaximoMaterialException extends Exception {
    	/**
    	 * Identificador para la serialización de la clase.
    	 */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor que acepta un mensaje.
         *
         * @param message Mensaje de la excepción.
         */
        public MaximoMaterialException(String message) {
            super(message);
        }
    }

    /**
     * Asocia un material a la pista, si cumple las condiciones necesarias.
     *
     * @param materialDTO Material a asociar a la pista.
     * @return True si el material fue añadido exitosamente, False en caso contrario.
     * @throws MaterialIncompatibleException Si el material no es compatible con la pista.
     * @throws MaximoMaterialException Si se ha alcanzado el máximo de materiales de ese tipo.
     */
    public boolean asociarMaterialAPista(MaterialDTO materialDTO) throws MaterialIncompatibleException, MaximoMaterialException {
        if (this.exterior && !materialDTO.isUsoExterior()) {
            throw new MaterialIncompatibleException("El material no puede ser utilizado en una pista exterior.");
        }

        int cantidadPelotas = 0;
        int cantidadCanastas = 0;
        int cantidadConos = 0;

        for (MaterialDTO mat : materiales) {
            switch (mat.getTipo()) {
                case PELOTAS:
                    cantidadPelotas++;
                    break;
                case CANASTAS:
                    cantidadCanastas++;
                    break;
                case CONOS:
                    cantidadConos++;
                    break;
                default:
                    break;
            }
        }

        if (materialDTO.getTipo() == TipoMaterial.PELOTAS && cantidadPelotas >= 12) {
            throw new MaximoMaterialException("No se pueden añadir más de 12 pelotas a la pista.");
        }
        if (materialDTO.getTipo() == TipoMaterial.CANASTAS && cantidadCanastas >= 2) {
            throw new MaximoMaterialException("No se pueden añadir más de 2 canastas a la pista.");
        }
        if (materialDTO.getTipo() == TipoMaterial.CONOS && cantidadConos >= 20) {
            throw new MaximoMaterialException("No se pueden añadir más de 20 conos a la pista.");
        }

        materiales.add(materialDTO);
        return true; // Material añadido exitosamente
    }

    /**
     * Método privado para crear una nueva lista de materiales.
     *
     * @return Lista vacía de materiales.
     */
    private List<MaterialDTO> crearListaMateriales() {
        return new ArrayList<>(); // Aquí puedes cambiar fácilmente la implementación
    }
}
