import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Juego {

    /* ATRIBUTOS */

    private String nombre;
    private String plataforma;
    private Estado estado;
    // (hack n slash, etc)
    private Set<String> etiquetas;
    private Valoracion valoracion;
    // comentarios extra
    private String notas;

    /* CONSTRUCTOR */

    public Juego(String nombre, String plataforma, Estado estado, Valoracion valoracion, String notas) {
        setNombre(nombre);
        setPlataforma(plataforma);
        setEstado(estado);
        this.etiquetas = new HashSet<>();
        setValoracion(valoracion);
        setNotas(notas);
    }

    /* GETTERS */

    public String getNombre() {
        return nombre;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public Estado getEstado() {
        return estado;
    }

    public Set<String> getEtiquetas() {
        return etiquetas;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }

    public String getNotas() {
        return notas;
    }

    /* SETTERS */

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

    public void anadirEtiqueta(String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) return;

        etiquetas.add(etiqueta.trim());
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
                === %s ===
                Plataforma: %s | Estado: %s | Etiquetas: %s | Valoración: %s | Notas: %s
                """, nombre, plataforma, estado, etiquetas, valoracion, notas);
    }

    /* METODO EQUALS */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Juego juego)) return false;
        return Objects.equals(nombre, juego.nombre) &&
                Objects.equals(plataforma, juego.plataforma) &&
                Objects.equals(estado, juego.estado) &&
                Objects.equals(etiquetas, juego.etiquetas) &&
                Objects.equals(valoracion, juego.valoracion) &&
                Objects.equals(notas, juego.notas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, plataforma, estado, etiquetas, valoracion, notas);
    }

    /* OTROS METODOS */

    // TODO metodos de busqueda?
}
