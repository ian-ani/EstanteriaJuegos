package dao;

import entity.Juego;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager {
    // Inicializar logger
    private static final Logger kLOGGER = Logger.getLogger(DataManager.class.getName());

    // Obtener juegos
    // TODO no se si esto estara bien
    public static List<Juego> getGames() {
        if (DBManager.loadDriver() && DBManager.connect()) {
            try {
                return DBManager.getGames();
            } catch (SQLException e) {
                kLOGGER.log(Level.WARNING, "Ha habido un error intentando obtener todos los juegos.");
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    // Obtener juegos por nombre
    public static List<Juego> selectGameByName(String name) {
        if (DBManager.loadDriver() && DBManager.connect()) {
            try {
                return DBManager.selectGameByName(name);
            } catch (SQLException e) {
                kLOGGER.log(Level.WARNING, "Ha habido un error intentando obtener un juego con ese nombre.");
                return new ArrayList<>();
            } finally {
                DBManager.close();
            }
        }

        return new ArrayList<>();
    }

    // Insertar nuevo juego
    public static boolean insertGame(Juego game) {
        if (DBManager.connect() && DBManager.insertGame(game)) {
            DBManager.close();
            return true;
        }

        DBManager.close();
        return false;
    }

    // Editar un juego
    public static boolean updateGame(Juego game) {
        if (DBManager.connect() && DBManager.updateGame(game)) {
            DBManager.close();
            return true;
        }

        DBManager.close();
        return false;
    }

    // Borrar un juego
    public static boolean deleteGame(Juego game) {
        if (DBManager.connect() && DBManager.deleteGame(game)) {
            DBManager.close();
            return true;
        }

        DBManager.close();
        return false;
    }
}

// TODO localizacion de los mensajes