package config;

public record DBConfig(
        String host,
        String port,
        String dbName,
        String user,
        String pass
) {
    public String url() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?serverTimezone=UTC";
    }

    public static DBConfig defaultConfig() {
        return new DBConfig(
                "localhost",
                "3306",
                "CAMBIAR",
                "root",
                ""
        );
    }
}
