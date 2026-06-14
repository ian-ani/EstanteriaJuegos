package config;

public record DBConfig(
        String host,
        String port,
        String dbName,
        String user,
        String pass
) {
    public String url() {
        return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" +
                dbName + ";encrypt=true;trustServerCertificate=true";
    }

    public static DBConfig defaultConfig() {
        return new DBConfig(
                "localhost",
                "1433",
                "hospital",
                "sa",
                "Password123"
        );
    }
}
