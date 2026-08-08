package entity;

public enum Opinion {
    /* VALORES */

    LIKED,
    NOT_LIKED,
    INDIFFERENT,
    NOT_RATED;

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
