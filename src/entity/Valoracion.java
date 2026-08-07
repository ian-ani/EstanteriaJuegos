package entity;

public enum Valoracion {
    /* VALORES */

    GUSTADO("Gustado"),
    NO_GUSTADO("No gustado"),
    INDIFERENTE("Indiferente"),
    NO_VALORADO("Sin valoración");

    /* ATRIBUTOS */

    private final String nombre;

    /* CONSTRUCTOR */

    Valoracion(String nombre) {
        this.nombre = nombre;
    }

    /* METODO TOSTRING */

    @Override
    public String toString() {
        return this.nombre;
    }

    /* OTROS METODOS */

    public static boolean esMiembro(String valor) {
        Valoracion[] valoraciones = Valoracion.values();

        for (Valoracion valoracion: valoraciones) {
            if (valoracion.name().equalsIgnoreCase(valor)) return true;
        }

        return false;
    }
}
