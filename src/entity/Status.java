package entity;

public enum Status {
    /* VALORES */

    COMPLETED,
    PLAYING,
    TO_PLAY,
    DROPPED;

    /* ATRIBUTOS */

    //private final String nombre;

    /* CONSTRUCTOR */

    /*Estado(String nombre) {
        this.nombre = nombre;
    }*/

    /* METODO TOSTRING */

    /*@Override
    public String toString() {
        return this.nombre;
    }*/

    /* OTROS METODOS */

    public static boolean isMember(String value) {
        Status[] statuses = Status.values();

        for (Status status : statuses) {
            if (status.name().equalsIgnoreCase(value)) return true;
        }

        return false;
    }
}
