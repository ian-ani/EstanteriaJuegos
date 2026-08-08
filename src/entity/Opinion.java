package entity;

public enum Opinion {
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

    public static boolean isMember(String value) {
        Opinion[] opinions = Opinion.values();

        for (Opinion opinion : opinions) {
            if (opinion.name().equalsIgnoreCase(value)) return true;
        }

        return false;
    }
}
