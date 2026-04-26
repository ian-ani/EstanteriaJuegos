public enum Valoracion {
    GUSTADO,
    NO_GUSTADO,
    INDIFERENTE;

    public static boolean esMiembro(String valor) {
        Valoracion[] valoraciones = Valoracion.values();

        for (Valoracion valoracion: valoraciones) {
            if (valoracion.name().equalsIgnoreCase(valor)) return true;
        }

        return false;
    }
}
