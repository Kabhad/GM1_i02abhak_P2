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

    private static int idCounter = 1; // Contador estático para generar IDs únicos
    private int idPista;
    private String nombrePista;
    private boolean disponible;
    private boolean exterior;
    private TamanoPista pista;
    private int max_jugadores;
    private List<MaterialDTO> materiales;

    /**
     * Constructor vacío que inicializa una pista con un ID único y una lista vacía de materiales.
     */
    public PistaDTO() {
        this.idPista = idCounter++; // Asignar un ID único
        this.materiales = new ArrayList<>();
    }

    /**
     * Constructor parametrizado para inicializar una pista con sus atributos.
     *
     * @param nombrePista Nombre de la pista.
     * @param disponible  Estado de disponibilidad de la pista.
     * @param exterior    Si la pista es exterior o no.
     * @param pista       Tamaño de la pista.
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
     * Obtiene el ID único de la pista.
     *
     * @return ID de la pista.
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
        List<MaterialDTO> materialesDisponibles = new ArrayList<>();
        for (MaterialDTO materialDTO : materiales) {
            if (materialDTO.getEstado() == EstadoMaterial.DISPONIBLE) {
                materialesDisponibles.add(materialDTO);
            }
        }
        return materialesDisponibles;
    }

    /**
     * Asocia un material a la pista, si cumple las condiciones necesarias.
     * 
     * @param materialDTO Material a asociar a la pista.
     * @return True si el material fue añadido exitosamente, False en caso contrario.
     */
    public boolean asociarMaterialAPista(MaterialDTO materialDTO) {
        if (this.exterior && !materialDTO.isUsoExterior()) {
            System.out.println("El material no puede ser utilizado en una pista exterior.");
            return false;
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
            System.out.println("No se pueden añadir más de 12 pelotas a la pista.");
            return false;
        }
        if (materialDTO.getTipo() == TipoMaterial.CANASTAS && cantidadCanastas >= 2) {
            System.out.println("No se pueden añadir más de 2 canastas a la pista.");
            return false;
        }
        if (materialDTO.getTipo() == TipoMaterial.CONOS && cantidadConos >= 20) {
            System.out.println("No se pueden añadir más de 20 conos a la pista.");
            return false;
        }

        materiales.add(materialDTO);
        return true; // Material añadido exitosamente
    }
}

