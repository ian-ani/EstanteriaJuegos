package util;

import entity.Game;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConvertCsv {
    private Path file;
    private List<Game> games;

    public ConvertCsv(Path file, List<Game> games) {
        this.file = file;
        this.games = games;
    }

    public boolean convert() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (Game g: games) {
                bw.write(g.toCsv());
                bw.newLine();
            }
        } catch (IOException exception) {
            return false;
        }

        return true;
    }
}
