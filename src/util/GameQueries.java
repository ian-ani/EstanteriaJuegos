package util;

public class GameQueries {
    // CREATE
    public static final String kINSERT_GAME = "INSERT INTO juegos (nombre, plataforma, estado, " +
            "etiquetas, valoracion, notas) VALUES (?, ?, ?, ?, ?, ?)";

    // READ
    public static final String kGET_ALL_GAMES = "SELECT * FROM juegos ORDER BY id DESC";
    public static final String kGET_GAME_BY_NAME = "SELECT * FROM juegos WHERE nombre LIKE ?";
    public static final String kGET_GAME_BY_PLATFORM = "SELECT * FROM juegos WHERE plataforma LIKE ?";

    // UPDATE
    public static final String kUPDATE_GAME = "UPDATE juegos SET nombre = ?, plataforma = ?, estado = ?, " +
            "etiquetas = ?, valoracion = ?, notas = ? WHERE id = ?";

    // DELETE
    public static final String kDELETE_GAME = "DELETE FROM juegos WHERE id = ?";
}
