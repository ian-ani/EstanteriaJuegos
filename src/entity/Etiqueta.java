package entity;

import java.util.Objects;

public class Etiqueta {
    private String nombre;

    public Etiqueta(String nombre) {
        setNombre(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        // TODO validar
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Etiqueta etiqueta)) return false;
        return Objects.equals(nombre, etiqueta.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombre);
    }
}
