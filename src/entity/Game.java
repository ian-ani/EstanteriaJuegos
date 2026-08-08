package entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Game {

    /* ATRIBUTOS */

    private int id;
    private String name;
    private String platform;
    private Status status;
    // (hack n slash, etc)
    private Set<Tag> tags;
    private Opinion opinion;
    // comentarios extra
    private String notes;

    /* CONSTRUCTOR */

    public Game(int id, String name, String platform, Status status, Opinion opinion, String notes) {
        setId(id);
        setName(name);
        setPlatform(platform);
        setStatus(status);
        this.tags = new HashSet<>();
        setOpinion(opinion);
        setNotes(notes);
    }

    /* GETTERS */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public Status getStatus() {
        return status;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Opinion getOpinion() {
        return opinion;
    }

    public String getNotes() {
        return notes;
    }

    /* SETTERS */

    private void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Index game can't be lower than 0.");
        }

        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("You can't register a game without a name.");
        }

        this.name = name;
    }

    public void setPlatform(String platform) {
        if (platform == null || platform.isBlank()) {
            this.platform = "";
        } else {
            this.platform = platform;
        }
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status can't be null.");
        }

        this.status = status;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = (tags != null)
                ? new HashSet<>(tags)
                : new HashSet<>();
    }

    public void addTag(Tag tag) {
        if (tag == null || tag.getName().isBlank()) return;

        tags.add(tag);
    }

    public void setOpinion(Opinion opinion) {
        if (opinion == null) {
            throw new IllegalArgumentException("Opinion can't be null.");
        }

        this.opinion = opinion;
    }

    public void setNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            this.notes = "";
        } else {
            this.notes = notes;
        }
    }

    /* METODO TOSTRING */

    @Override
    public String toString() {
        return String.format("""
                === %s (%d) ===
                Platform: %s | Status: %s | Tags: %s | Opinion: %s | Notes: %s
                """, name, id, platform, status, tags, opinion, notes);
    }

    /* METODO EQUALS */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game game)) return false;
        return Objects.equals(name, game.name) &&
                Objects.equals(platform, game.platform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, platform);
    }

    /* OTROS METODOS */

    public String toCsv() {
        return String.format("%s, %s, %s, %s, %s, %s", name, platform, status, tags, opinion, notes);
    }
}
