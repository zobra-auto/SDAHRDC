package com.example.Model;

import java.util.Locale;

public record ResultadoSimulacion(int enemigosEnLaHorda, int enemigosDerrotados, int turnosDeCampo,
                                  int rotacionesDeCola, int evoluciones, long turnosDeCombate,
                                  int pokemonRetirados, String equipoFinal, String estadoCajaNegra,
                                  long nanosTotales) {

    public double duracionSegundos() {
        return nanosTotales / 1_000_000_000.0;
    }

    public double milisegundos() {
        return nanosTotales / 1_000_000.0;
    }

    public boolean hordaDerrotada() {
        return enemigosDerrotados == enemigosEnLaHorda;
    }

    public String resumen() {
        return String.format(Locale.ROOT,
                "%nEnemigos en la horda  : %d%nEnemigos derrotados   : %d%nTurnos de campo       : %d" +
                        "%nRotaciones de la cola : %d%nEvoluciones           : %d%nTurnos de combate     : %d" +
                        "%nPokemon retirados     : %d%nEquipo final          : %s%nCaja Negra final      : %s" +
                        "%nTiempo total          : %.4f s (%.2f ms)%n",
                enemigosEnLaHorda, enemigosDerrotados, turnosDeCampo, rotacionesDeCola, evoluciones,
                turnosDeCombate, pokemonRetirados, equipoFinal, estadoCajaNegra,
                duracionSegundos(), milisegundos());
    }

    @Override
    public String toString() {
        return "ResultadoSimulacion {" +
                "enemigosEnLaHorda = " + enemigosEnLaHorda +
                ", enemigosDerrotados = " + enemigosDerrotados +
                ", turnosDeCampo = " + turnosDeCampo +
                ", rotacionesDeCola = " + rotacionesDeCola +
                ", evoluciones = " + evoluciones +
                ", turnosDeCombate = " + turnosDeCombate +
                ", pokemonRetirados = " + pokemonRetirados +
                ", equipoFinal = '" + equipoFinal + '\'' +
                ", estadoCajaNegra = '" + estadoCajaNegra + '\'' +
                ", duracionSegundos = " + duracionSegundos() +
                '}';
    }

}
