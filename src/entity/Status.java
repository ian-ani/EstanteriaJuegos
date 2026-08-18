package entity;

public enum Status {
    /* VALORES */

    COMPLETADO,
    JUGANDO,
    PENDIENTE,
    ABANDONADO;

    /* OTROS METODOS */

    public static boolean isMember(String value) {
        Status[] statuses = Status.values();

        for (Status status : statuses) {
            if (status.name().equalsIgnoreCase(value)) return true;
        }

        return false;
    }
}
