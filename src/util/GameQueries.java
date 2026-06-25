package util;

public class GameQueries {
    // CREATE
    public static final String kINSERT_GAME = "INSERT INTO juegos (nombre, plataforma, estado, " +
            "etiquetas, valoracion, comentarios) VALUES (?, ?, ?, ?, ?, ?)";

    // READ
    public static final String kGET_ALL_GAMES = "SELECT * FROM juegos ORDER BY id DESC";
    public static final String kGET_GAME_BY_NAME = "SELECT * FROM juegos WHERE nombre LIKE ?";

    // UPDATE
    public static final String kUPDATE_GAME = "UPDATE juegos SET nombre = ?, plataforma = ?, estado = ?, " +
            "etiquetas = ?, valoracion = ?, comentarios = ? WHERE id = ?";

    // DELETE
    public static final String kDELETE_GAME = "DELETE FROM juegos WHERE id = ?";
}
