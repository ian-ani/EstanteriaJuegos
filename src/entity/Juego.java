package entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Juego {

    /* ATRIBUTOS */

    private int id;
    private String nombre;
    private String plataforma;
    private Estado estado;
    // (hack n slash, etc)
    private Set<Etiqueta> etiquetas;
    private Valoracion valoracion;
    // comentarios extra
    private String notas;

    /* CONSTRUCTOR */

    public Juego(int id, String nombre, String plataforma, Estado estado, Valoracion valoracion, String notas) {
        setId(id);
        setNombre(nombre);
        setPlataforma(plataforma);
        setEstado(estado);
        this.etiquetas = new HashSet<>();
        setValoracion(valoracion);
        setNotas(notas);
    }

    /* GETTERS */

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public Estado getEstado() {
        return estado;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }

    public String getNotas() {
        return notas;
    }

    /* SETTERS */

    private void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El índice del juego no puede ser inferior a 0.");
        }

        this.id = id;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("No puedes registrar un juego sin nombre.");
        }

        this.nombre = nombre;
    }

    public void setPlataforma(String plataforma) {
        if (plataforma == null || plataforma.isBlank()) {
            this.plataforma = "";
        } else {
            this.plataforma = plataforma;
        }
    }

    public void setEstado(Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }

        this.estado = estado;
    }

    public void setEtiquetas(Set<Etiqueta> etiquetas) {
        this.etiquetas = (etiquetas != null)
                ? new HashSet<>(etiquetas)
                : new HashSet<>();
    }

    public void anadirEtiqueta(Etiqueta etiqueta) {
        if (etiqueta == null || etiqueta.getNombre().isBlank()) return;

        etiquetas.add(etiqueta);
    }

    public void setValoracion(Valoracion valoracion) {
        if (valoracion == null) {
            throw new IllegalArgumentException("La valoración no puede ser nulo.");
        }

        this.valoracion = valoracion;
    }

    public void setNotas(String notas) {
        if (notas == null || notas.isBlank()) {
            this.notas = "";
        } else {
            this.notas = notas;
        }
    }

    /* METODO TOSTRING */

    @Override
    public String toString() {
        return String.format("""
                === %s (%d) ===
                Plataforma: %s | Estado: %s | Etiquetas: %s | Valoración: %s | Notas: %s
                """, nombre, id, plataforma, estado, etiquetas, valoracion, notas);
    }

    /* METODO EQUALS */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Juego juego)) return false;
        return Objects.equals(nombre, juego.nombre) &&
                Objects.equals(plataforma, juego.plataforma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, plataforma);
    }
}
