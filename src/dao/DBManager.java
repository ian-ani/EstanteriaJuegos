package dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config.DBConfig;
import entity.Estado;
import entity.Juego;
import entity.Valoracion;
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
    private static final String kDB_MSQ_CONN_OK = "CONEXIÓN CORRECTA";
    private static final String kDB_MSQ_CONN_NO = "ERROR EN LA CONEXIÓN";

    // Inicializacion de Gson (para las etiquetas)
    private static final Gson gson = new Gson();

    // Tipo para el Gson
    private static final Type kSET_STRING_TYPE = new TypeToken<Set<String>>(){}.getType();

    // Intentar cargar el JDBC driver
    public static boolean loadDriver() {
        try {
            kLOGGER.log(Level.INFO, "Cargando driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            kLOGGER.log(Level.INFO, "OK!");
            return true;
        } catch (ClassNotFoundException e) {
            kLOGGER.log(Level.SEVERE, "Ha habido un problema con la clase del driver.");
            return false;
        } catch (Exception e) {
            kLOGGER.log(Level.SEVERE, "Ha habido un problema general cargando el driver.");
            return false;
        }
    }

    // Intentar conectar con la base de datos
    public static boolean connect() {
        try {
            kLOGGER.log(Level.INFO, "Conectando a la base de datos...");

            conn = DriverManager.getConnection(
                    DBConfig.defaultConfig().url(),
                    DBConfig.defaultConfig().user(),
                    DBConfig.defaultConfig().pass()
            );

            kLOGGER.log(Level.INFO, kDB_MSQ_CONN_OK);
            return true;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, kDB_MSQ_CONN_NO);
            return false;
        }
    }

    // Cerrar conexion con la base de datos
    public static void close() {
        try {
            kLOGGER.log(Level.INFO, "Cerrando la conexión...");
            conn.close();
            kLOGGER.log(Level.INFO, "OK!");
        } catch (SQLException e) {
            kLOGGER.log(Level.WARNING, "Ha habido un problema cerrando la conexión con la base de datos.", e);
        }
    }

    // Construir juego
    private static Juego buildGame(ResultSet rs) throws SQLException {
        String json = rs.getString("etiquetas");
        Set<String> etiquetas = (json != null)
                ? gson.fromJson(json, kSET_STRING_TYPE)
                : new HashSet<>();

        Juego tmp = new Juego(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("plataforma"),
                Estado.valueOf(rs.getString("estado")),
                Valoracion.valueOf(rs.getString("valoracion")),
                rs.getString("comentarios")
        );
        tmp.setEtiquetas(etiquetas);

        return tmp;
    }

    // Obtener juegos
    public static List<Juego> getGames() throws SQLException {
        List<Juego> games = new ArrayList<>();
        String query = GameQueries.kGET_ALL_GAMES;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                games.add(buildGame(rs));
            }

        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "No se han podido obtener los juegos.", e);
        }


        return games;
    }

    // Obtener juegos por nombre
    public static List<Juego> selectGameByName(String name) throws SQLException {
        List<Juego> games = new ArrayList<>();
        String query = GameQueries.kGET_GAME_BY_NAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                games.add(buildGame(rs));
            }
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "No se ha podido obtener el juego.", e);
        }

        return games;
    }

    // Insertar nuevo juego
    public static boolean insertGame(Juego game) {
        kLOGGER.log(Level.INFO, String.format("Insertando juego \n%s...\n", game));
        String query = GameQueries.kINSERT_GAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, game.getNombre());
            ps.setString(2, game.getPlataforma());
            ps.setString(3, game.getEstado().name());
            ps.setString(4, gson.toJson(game.getEtiquetas()));
            ps.setString(5, game.getValoracion().name());
            ps.setString(6, game.getNotas());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "No se ha podido insertar el juego.", e);
            return false;
        }
    }

    // Editar un juego
    public static boolean updateGame(Juego game) {
        kLOGGER.log(Level.INFO, String.format("Editando juego \n%s...\n", game));
        String query = GameQueries.kUPDATE_GAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, game.getNombre());
            ps.setString(2, game.getPlataforma());
            ps.setString(3, game.getEstado().name());
            ps.setString(4, gson.toJson(game.getEtiquetas()));
            ps.setString(5, game.getValoracion().name());
            ps.setString(6, game.getNotas());
            ps.setInt(7, game.getId());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "No se ha podido editar el juego.", e);
            return false;
        }
    }

    // Borrar un juego
    public static boolean deleteGame(Juego game) {
        kLOGGER.log(Level.INFO, String.format("Borrando juego \n%s...\n", game));
        String query = GameQueries.kDELETE_GAME;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, game.getId());

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            kLOGGER.log(Level.SEVERE, "No se ha podido borrar el juego.", e);
            return false;
        }
    }
}

// TODO localizacion de los mensajes