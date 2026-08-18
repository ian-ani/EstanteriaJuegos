package dao;

import entity.Game;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager {
    // Inicializar logger
    private static final Logger kLOGGER = Logger.getLogger(DataManager.class.getName());

    // Obtener juegos
    public static List<Game> getGames() {
        if (DBManager.loadDriver() && DBManager.connect()) {
            try {
                return DBManager.getGames();
            } catch (SQLException e) {
                kLOGGER.log(Level.WARNING, "An error has been found trying to get all the games.");
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    // Obtener juegos por nombre
    public static List<Game> selectGameByName(String name) {
        if (DBManager.loadDriver() && DBManager.connect()) {
            try {
                return DBManager.selectGameByName(name);
            } catch (SQLException e) {
                kLOGGER.log(Level.WARNING, "An error has been found trying to get a game by that name " +
                        "or it doesn't exist.");
                return new ArrayList<>();
            } finally {
                DBManager.close();
            }
        }

        return new ArrayList<>();
    }

    // Obtener juegos por plataforma
    public static List<Game> selectGameByPlatform(String name) {
        if (DBManager.loadDriver() && DBManager.connect()) {
            try {
                return DBManager.selectGameByPlatform(name);
            } catch (SQLException e) {
                kLOGGER.log(Level.WARNING, "An error has been found trying to get a game by that platform " +
                        "or it doesn't exist.");
                return new ArrayList<>();
            } finally {
                DBManager.close();
            }
        }

        return new ArrayList<>();
    }

    // Obtener juego por nombre y plataforma
    public static Game selectGameByNameAndPlatform(String name, String platform) {
        if (DBManager.loadDriver() && DBManager.connect()) {
            try {
                return DBManager.selectGameByNameAndPlatform(name, platform);
            } catch (SQLException e) {
                kLOGGER.log(Level.WARNING, "An error has been found trying to get a game by that name " +
                        "and platform or it doesn't exist.");
            } finally {
                DBManager.close();
            }
        }

        return null;
    }

    // Obtener juego por nombre, plataforma e id
    public static Game selectGameByNameAndPlatformAndDifferentId(String name, String platform, int id) {
        if (DBManager.loadDriver() && DBManager.connect()) {
            try {
                return DBManager.selectGameByNameAndPlatformAndDifferentId(name, platform, id);
            } catch (SQLException e) {
                kLOGGER.log(Level.WARNING, "An error has been found trying to get a game by that name " +
                        ", platform and id or it doesn't exist.");
            } finally {
                DBManager.close();
            }
        }

        return null;
    }

    // Insertar nuevo juego
    public static boolean insertGame(Game game) {
        if (DBManager.connect() && DBManager.insertGame(game)) {
            DBManager.close();
            return true;
        }

        DBManager.close();
        return false;
    }

    // Editar un juego
    public static boolean updateGame(Game game) {
        if (DBManager.connect() && DBManager.updateGame(game)) {
            DBManager.close();
            return true;
        }

        DBManager.close();
        return false;
    }

    // Borrar un juego
    public static boolean deleteGame(Game game) {
        if (DBManager.connect() && DBManager.deleteGame(game)) {
            DBManager.close();
            return true;
        }

        DBManager.close();
        return false;
    }
}
