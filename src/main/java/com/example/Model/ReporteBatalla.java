package com.example.Model;

import java.util.Objects;

public class ReporteBatalla {

    private final String nombrePokemon;
    private final String nombreEnemigo;

    private int cantidadDerrotados;

    public ReporteBatalla(String nombrePokemon, String nombreEnemigo) {

        if (nombrePokemon == null || nombrePokemon.trim().isEmpty()) throw new IllegalArgumentException("El nombre del Pokemon atacante es obligatorio");
        if (nombreEnemigo == null || nombreEnemigo.trim().isEmpty()) throw new IllegalArgumentException("El nombre del enemigo es obligatorio");

        this.nombrePokemon = nombrePokemon;
        this.nombreEnemigo = nombreEnemigo;
        this.cantidadDerrotados = 1;

    }

    public boolean correspondeA(String nombrePokemon, String nombreEnemigo) {
        return this.nombrePokemon.equals(nombrePokemon) && this.nombreEnemigo.equals(nombreEnemigo);
    }

    public void incrementarDerrotados() {
        this.cantidadDerrotados++;
    }

    public String resumen() {
        return "(" + nombrePokemon + " vs " + nombreEnemigo + ": " + cantidadDerrotados + ")";
    }

    public String getNombrePokemon() {
        return nombrePokemon;
    }

    public String getNombreEnemigo() {
        return nombreEnemigo;
    }

    public int getCantidadDerrotados() {
        return cantidadDerrotados;
    }

    @Override
    public String toString() {
        return "ReporteBatalla {" +
                "nombrePokemon = '" + nombrePokemon + '\'' +
                ", nombreEnemigo = '" + nombreEnemigo + '\'' +
                ", cantidadDerrotados = " + cantidadDerrotados +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ReporteBatalla reporteBatalla)) return false;
        return cantidadDerrotados == reporteBatalla.cantidadDerrotados
                && Objects.equals(nombrePokemon, reporteBatalla.nombrePokemon)
                && Objects.equals(nombreEnemigo, reporteBatalla.nombreEnemigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombrePokemon, nombreEnemigo, cantidadDerrotados);
    }

}
