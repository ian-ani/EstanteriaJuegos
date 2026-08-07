package entity;

public enum Estado {
    /* VALORES */

    COMPLETADO,
    JUGANDO,
    PENDIENTE,
    ABANDONADO;

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

    public static boolean esMiembro(String valor) {
        Estado[] estados = Estado.values();

        for (Estado estado: estados) {
            if (estado.name().equalsIgnoreCase(valor)) return true;
        }

        return false;
    }
}
