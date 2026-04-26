public enum Estado {
    COMPLETADO,
    JUGANDO,
    PENDIENTE,
    ABANDONADO;

    public static boolean esMiembro(String valor) {
        Estado[] estados = Estado.values();

        for (Estado estado: estados) {
            if (estado.name().equalsIgnoreCase(valor)) return true;
        }

        return false;
    }
}
