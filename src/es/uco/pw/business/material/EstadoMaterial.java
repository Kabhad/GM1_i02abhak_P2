package es.uco.pw.business.material;

/**
 * Enum que representa los diferentes estados posibles de un material.
 */
public enum EstadoMaterial {
    /** Estado que indica que el material está disponible para uso. */
    DISPONIBLE,

    /** Estado que indica que el material está reservado. */
    RESERVADO,

    /** Estado que indica que el material está en mal estado y no puede ser utilizado. */
    MAL_ESTADO
}
