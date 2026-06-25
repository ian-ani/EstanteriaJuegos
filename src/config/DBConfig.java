package config;

public record DBConfig(
        String host,
        String port,
        String dbName,
        String user,
        String pass,
        String driver
) {
    public String mySqlUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?serverTimezone=UTC";
    }

    public String sqliteUrl() {
        return "jdbc:sqlite:db/" + dbName;
    }

    public static DBConfig defaultConfig(DBType type) {
        return switch (type) {
            case DBType.MYSQL ->
                new DBConfig(
                        "localhost",
                        "3306",
                        "juegos",
                        "root",
                        "",
                        "com.mysql.cj.jdbc.Driver"
                );
            case DBType.SQLITE ->
                new DBConfig(
                        "",
                        "",
                        "juegos.db",
                        "",
                        "",
                        "org.sqlite.JDBC"
                );
        };
    }
}
