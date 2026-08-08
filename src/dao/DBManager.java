package dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config.DBConfig;
import config.DBType;
import entity.Status;
import entity.Tag;
import entity.Game;
import entity.Opinion;
import util.GameQueries;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {
    // Inicializar logger
    private static final Logger kLOGGER = Logger.getLogger(DBManager.class.getName());

    // Conexion a la base de datos
    private static Connection conn = null;

    // Configuracion de mensajes
    private static final String kDB_MSQ_CONN_OK = "CONNECTION ESTABLISHED";
    private static final String kDB_MSQ_CONN_NO = "CONNECTION ERROR";

    // Inicializacion de Gson (para las etiquetas)
    private static final Gson gson = new Gson();

    // Tipo para el Gson
    private static final Type kSET_TAG_TYPE = new TypeToken<Set<Tag>>(){}.getType();

    // Intentar cargar el JDBC driver
    public static boolean loadDriver() {
        try {
            kLOGGER.log(Level.INFO, "Loading driver...");
            Class.forName(DBConfig.defaultConfig(DBType.SQLITE).driver());
            kLOGGER.log(Level.INFO, "OK!");
            return true;
        } catch (ClassNotFoundException e) {
            kLOGGER.log(Level.SEVERE, "Ha habido un problema con la clase del driver.", e);
            return false;
        } catch (Exception e) {
            kLOGGER.log(Level.SEVERE, "Ha habido un problema general cargando el driver.", e);
            return false;
        }
    }

    // Intentar conectar con la base de datos
    public static boolean connect() {
        try {
            kLOGGER.log(Level.INFO, "Conectando a la base de datos...");

            /*conn = DriverManager.getConnection(
                    DBConfig.defaultConfig(DBType.MYSQL).url(),
                    DBConfig.defaultConfig(DBType.MYSQL).user(),
                    DBConfig.defaultConfig(DBType.MYSQL).pass()
            );*/

            conn = DriverManager.getConnection(
                    DBConfig.defaultConfig(DBType.SQLITE).sqliteUrl()
            );

            kLOGGER.log(Level.INFO, kDB_MSQ_CONN_OK);
            return true;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, kDB_MSQ_CONN_NO, e);
            return false;
        }
    }

    // Cerrar conexion con la base de datos
    public static void close() {
        try {
            kLOGGER.log(Level.INFO, "Closing this connection...");
            conn.close();
            kLOGGER.log(Level.INFO, "OK!");
        } catch (SQLException e) {
            kLOGGER.log(Level.WARNING, "There has been an issue closing the database connection.", e);
        }
    }

    // Construir juego
    private static Game buildGame(ResultSet rs) throws SQLException {
        String json = rs.getString("etiquetas");
        Set<Tag> tags = (json != null)
                ? gson.fromJson(json, kSET_TAG_TYPE)
                : new HashSet<>();

        Game tmp = new Game(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("plataforma"),
                Status.valueOf(rs.getString("estado")),
                Opinion.valueOf(rs.getString("valoracion")),
                rs.getString("notas")
        );
        tmp.setTags(tags);

        return tmp;
    }

    // Obtener juegos
    public static List<Game> getGames() throws SQLException {
        List<Game> games = new ArrayList<>();
        String query = GameQueries.kGET_ALL_GAMES;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                games.add(buildGame(rs));
            }

        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "Couldn't get all the games.", e);
        }

        return games;
    }

    // Obtener juegos por nombre
    public static List<Game> selectGameByName(String name) throws SQLException {
        List<Game> games = new ArrayList<>();
        String query = GameQueries.kGET_GAME_BY_NAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                games.add(buildGame(rs));
            }
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "Couldn't get this game.", e);
        }

        return games;
    }

    // Obtener juegos por plataforma
    public static List<Game> selectGameByPlatform(String name) throws SQLException {
        List<Game> games = new ArrayList<>();
        String query = GameQueries.kGET_GAME_BY_PLATFORM;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                games.add(buildGame(rs));
            }
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "Couldn't get this game.", e);
        }

        return games;
    }

    // Insertar nuevo juego
    public static boolean insertGame(Game game) {
        kLOGGER.log(Level.INFO, String.format("Inserting game \n%s...\n", game));
        String query = GameQueries.kINSERT_GAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, game.getName());
            ps.setString(2, game.getPlatform());
            ps.setString(3, game.getStatus().name());
            ps.setString(4, gson.toJson(game.getTags()));
            ps.setString(5, game.getOpinion().name());
            ps.setString(6, game.getNotes());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "Couldn't insert this game.", e);
            return false;
        }
    }

    // Editar un juego
    public static boolean updateGame(Game game) {
        kLOGGER.log(Level.INFO, String.format("Editing game \n%s...\n", game));
        String query = GameQueries.kUPDATE_GAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, game.getName());
            ps.setString(2, game.getPlatform());
            ps.setString(3, game.getStatus().name());
            ps.setString(4, gson.toJson(game.getTags()));
            ps.setString(5, game.getOpinion().name());
            ps.setString(6, game.getNotes());
            ps.setInt(7, game.getId());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "Couldn't edit this game.", e);
            return false;
        }
    }

    // Borrar un juego
    public static boolean deleteGame(Game game) {
        kLOGGER.log(Level.INFO, String.format("Deleting game \n%s...\n", game));
        String query = GameQueries.kDELETE_GAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, game.getId());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "Couldn't delete this game.", e);
            return false;
        }
    }
}
