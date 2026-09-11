package com.example.Model;

import java.util.Objects;

public class Pokemon {

    public static final int SIN_EVOLUCION = -1;

    private static final int DANIO_MINIMO = 1;

    private final String nombre;
    private final int puntosDeVidaMaximos;
    private final int ataque;
    private final int defensa;
    private final int experienciaRequerida;

    private Pokemon siguienteEvolucion;

    public Pokemon(String nombre, int puntosDeVidaMaximos, int ataque, int defensa, int experienciaRequerida) {

        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre del Pokemon es obligatorio");
        if (puntosDeVidaMaximos <= 0) throw new IllegalArgumentException("Los puntos de vida deben ser mayores a cero");
        if (ataque <= 0) throw new IllegalArgumentException("El ataque debe ser mayor a cero");
        if (defensa < 0) throw new IllegalArgumentException("La defensa no puede ser negativa");

        this.nombre = nombre;
        this.puntosDeVidaMaximos = puntosDeVidaMaximos;
        this.ataque = ataque;
        this.defensa = defensa;
        this.experienciaRequerida = experienciaRequerida;
        this.siguienteEvolucion = null;

    }

    public int calcularDanioContra(Pokemon defensor) {
        return Math.max(DANIO_MINIMO, this.ataque - defensor.getDefensa());
    }

    public boolean tieneEvolucionDisponible() {
        return siguienteEvolucion != null && experienciaRequerida != SIN_EVOLUCION;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosDeVidaMaximos() {
        return puntosDeVidaMaximos;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getExperienciaRequerida() {
        return experienciaRequerida;
    }

    public Pokemon getSiguienteEvolucion() {
        return siguienteEvolucion;
    }

    public void setSiguienteEvolucion(Pokemon siguienteEvolucion) {
        this.siguienteEvolucion = siguienteEvolucion;
    }

    @Override
    public String toString() {
        return "Pokemon {" +
                "nombre = '" + nombre + '\'' +
                ", puntosDeVidaMaximos = " + puntosDeVidaMaximos +
                ", ataque = " + ataque +
                ", defensa = " + defensa +
                ", experienciaRequerida = " + experienciaRequerida +
                ", siguienteEvolucion = " + (siguienteEvolucion == null ? "null" : siguienteEvolucion.getNombre()) +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Pokemon pokemon)) return false;
        return puntosDeVidaMaximos == pokemon.puntosDeVidaMaximos
                && ataque == pokemon.ataque
                && defensa == pokemon.defensa
                && experienciaRequerida == pokemon.experienciaRequerida
                && Objects.equals(nombre, pokemon.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, puntosDeVidaMaximos, ataque, defensa, experienciaRequerida);
    }

}
