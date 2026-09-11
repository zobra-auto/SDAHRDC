package com.example.Util;

import com.example.Model.LineaEvolutiva;
import com.example.Model.Pokemon;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.Queue;

@Log4j2
public class Configuration {

    private static final Pokemon[] FASES_BULBASAUR = {
            new Pokemon("Bulbasaur", 45, 49, 49, 1500),
            new Pokemon("Ivysaur", 60, 62, 63, 5000),
            new Pokemon("Venusaur", 80, 82, 83, Pokemon.SIN_EVOLUCION)
    };

    private static final Pokemon[] FASES_CHARMANDER = {
            new Pokemon("Charmander", 39, 52, 43, 1500),
            new Pokemon("Charmeleon", 58, 64, 58, 5000),
            new Pokemon("Charizard", 78, 84, 78, Pokemon.SIN_EVOLUCION)
    };

    private static final Pokemon[] FASES_SQUIRTLE = {
            new Pokemon("Squirtle", 44, 48, 65, 1500),
            new Pokemon("Wartortle", 59, 63, 80, 5000),
            new Pokemon("Blastoise", 79, 83, 100, Pokemon.SIN_EVOLUCION)
    };

    private static final String NOMBRE_ENEMIGO_HORDA = "Caterpie";
    private static final int CATERPIE_HP = 45;
    private static final int CATERPIE_ATAQUE = 30;
    private static final int CATERPIE_DEFENSA = 35;

    private static final String NOMBRE_ENEMIGO_PRUEBA = "Rattata";
    private static final int RATTATA_HP = 30;
    private static final int RATTATA_ATAQUE = 56;
    private static final int RATTATA_DEFENSA = 35;

    private static final int HP_ALEATORIO_MINIMO = 30;
    private static final int HP_ALEATORIO_MAXIMO = 50;
    private static final int ATAQUE_ALEATORIO_MINIMO = 20;
    private static final int ATAQUE_ALEATORIO_MAXIMO = 35;
    private static final int DEFENSA_ALEATORIA_MINIMA = 25;
    private static final int DEFENSA_ALEATORIA_MAXIMA = 40;

    public static LineaEvolutiva crearLineaEvolutivaBulbasaur() {
        return construirLineaEvolutiva(FASES_BULBASAUR);
    }

    public static LineaEvolutiva crearLineaEvolutivaCharmander() {
        return construirLineaEvolutiva(FASES_CHARMANDER);
    }

    public static LineaEvolutiva crearLineaEvolutivaSquirtle() {
        return construirLineaEvolutiva(FASES_SQUIRTLE);
    }

    public static Queue<LineaEvolutiva> crearEquipoInicial() {

        Queue<LineaEvolutiva> equipo = new LinkedList<>();

        equipo.offer(crearLineaEvolutivaBulbasaur());
        equipo.offer(crearLineaEvolutivaCharmander());
        equipo.offer(crearLineaEvolutivaSquirtle());

        log.info("Equipo encolado | integrantes: {}", equipo.size());

        return equipo;

    }

    private static LineaEvolutiva construirLineaEvolutiva(Pokemon[] fases) {

        LineaEvolutiva lineaEvolutiva = new LineaEvolutiva();

        for (Pokemon fase : fases) {

            Pokemon nodo = new Pokemon(fase.getNombre(), fase.getPuntosDeVidaMaximos(),
                    fase.getAtaque(), fase.getDefensa(), fase.getExperienciaRequerida());

            lineaEvolutiva.agregarFase(nodo);

        }

        log.info("Linea evolutiva creada | fases: {} | fase inicial: {}",
                lineaEvolutiva.getCantidadFases(), lineaEvolutiva.getFaseActual().getNombre());

        return lineaEvolutiva;

    }

    public static Pokemon crearRattataSalvaje() {
        return new Pokemon(NOMBRE_ENEMIGO_PRUEBA, RATTATA_HP, RATTATA_ATAQUE, RATTATA_DEFENSA, Pokemon.SIN_EVOLUCION);
    }

    public static Pokemon[] generarHordaCaterpie(int cantidad) {

        if (cantidad <= 0) throw new IllegalArgumentException("La horda debe tener al menos un enemigo");

        log.info("Generando horda de {} {}", cantidad, NOMBRE_ENEMIGO_HORDA);

        Pokemon[] horda = new Pokemon[cantidad];

        for (int i = 0; i < cantidad; i++) {
            horda[i] = new Pokemon(NOMBRE_ENEMIGO_HORDA, CATERPIE_HP, CATERPIE_ATAQUE, CATERPIE_DEFENSA, Pokemon.SIN_EVOLUCION);
        }

        log.info("Horda generada | enemigos: {}", horda.length);

        return horda;

    }

    public static Pokemon[] generarHordaAleatoria(int cantidad) {

        if (cantidad <= 0) throw new IllegalArgumentException("La horda debe tener al menos un enemigo");

        log.info("Generando horda aleatoria | enemigos: {}", cantidad);

        Pokemon[] horda = new Pokemon[cantidad];

        for (int i = 0; i < cantidad; i++) {

            int hp = generarValorAleatorio(HP_ALEATORIO_MINIMO, HP_ALEATORIO_MAXIMO);
            int ataque = generarValorAleatorio(ATAQUE_ALEATORIO_MINIMO, ATAQUE_ALEATORIO_MAXIMO);
            int defensa = generarValorAleatorio(DEFENSA_ALEATORIA_MINIMA, DEFENSA_ALEATORIA_MAXIMA);

            horda[i] = new Pokemon(NOMBRE_ENEMIGO_HORDA, hp, ataque, defensa, Pokemon.SIN_EVOLUCION);

        }

        log.info("Horda aleatoria generada | enemigos: {}", horda.length);

        return horda;

    }

    private static int generarValorAleatorio(int minimo, int maximo) {
        return (int) (Math.random() * ((maximo - minimo) + 1)) + minimo;
    }

}
