package com.example.Model;

import java.util.Locale;

public record ResultadoEntrenamiento(int enemigosEnfrentados, int victorias, int derrotas, long turnosTotales,
                                     int experienciaFinal, int evoluciones, String faseFinal, long nanosTotales) {

    public double duracionSegundos() {
        return nanosTotales / 1_000_000_000.0;
    }

    public double milisegundos() {
        return nanosTotales / 1_000_000.0;
    }

    public String resumen() {
        return String.format(Locale.ROOT,
                "%nEnemigos enfrentados : %d%nVictorias            : %d%nDerrotas             : %d" +
                        "%nTurnos totales       : %d%nExperiencia final    : %d%nEvoluciones          : %d" +
                        "%nFase final           : %s%nTiempo total         : %.4f s (%.2f ms)%n",
                enemigosEnfrentados, victorias, derrotas, turnosTotales,
                experienciaFinal, evoluciones, faseFinal, duracionSegundos(), milisegundos());
    }

    @Override
    public String toString() {
        return "ResultadoEntrenamiento {" +
                "enemigosEnfrentados = " + enemigosEnfrentados +
                ", victorias = " + victorias +
                ", derrotas = " + derrotas +
                ", turnosTotales = " + turnosTotales +
                ", experienciaFinal = " + experienciaFinal +
                ", evoluciones = " + evoluciones +
                ", faseFinal = '" + faseFinal + '\'' +
                ", duracionSegundos = " + duracionSegundos() +
                '}';
    }

}
