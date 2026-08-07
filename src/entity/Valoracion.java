package entity;

public enum Valoracion {
    /* VALORES */

    GUSTADO,
    NO_GUSTADO,
    INDIFERENTE,
    NO_VALORADO;

    /* ATRIBUTOS */

    //private final String nombre;

    /* CONSTRUCTOR */

    /*Valoracion(String nombre) {
        t*/

    /* METODO TOSTRING */

    /*@Override
    public String toString() {
        return this.nombre;
    }*/

    /* OTROS METODOS */

    public static boolean esMiembro(String valor) {
        Valoracion[] valoraciones = Valoracion.values();

        for (Valoracion valoracion: valoraciones) {
            if (valoracion.name().equalsIgnoreCase(valor)) return true;
        }

        return false;
    }
}
