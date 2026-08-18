package entity;

public enum Opinion {
    /* VALORES */

    GUSTADO,
    NO_GUSTADO,
    INDIFERENTE,
    NO_VALORADO;

    /* OTROS METODOS */

    public static boolean isMember(String value) {
        Opinion[] opinions = Opinion.values();

        for (Opinion opinion : opinions) {
            if (opinion.name().equalsIgnoreCase(value)) return true;
        }

        return false;
    }
}
