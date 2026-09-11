package com.example.Service;

import com.example.Model.LineaEvolutiva;
import com.example.Model.Pokemon;
import com.example.Model.ResultadoBatalla;
import com.example.Model.ResultadoEntrenamiento;
import com.example.Util.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para el motor de batallas y el entrenamiento masivo")
class EntrenamientoServiceTest {

    private LineaEvolutiva miPokemon;

    @BeforeEach
    void setUp() {
        miPokemon = Configuration.crearLineaEvolutivaCharmander();
    }

    @Test
    @DisplayName("Charmander vence a Rattata en 3 turnos quedando con 26 HP")
    void testBatallaContraRattata() {
        Pokemon rattata = Configuration.crearRattataSalvaje();
        int turnosEsperados = 3;
        int hpEsperadoCharmander = 26;

        ResultadoBatalla resultado = EntrenamientoService.librarBatalla(miPokemon, rattata);

        assertTrue(resultado.victoria(), "Charmander debe ganar la batalla");
        assertEquals(turnosEsperados, resultado.turnos(), "La batalla debe resolverse en 3 ataques");
        assertEquals(hpEsperadoCharmander, resultado.hpRestanteAliado(), "Charmander recibe un solo golpe de 13 de dano");
        assertEquals(0, resultado.hpRestanteEnemigo(), "Rattata debe quedar sin puntos de vida");
    }

    @Test
    @DisplayName("El dano debe seguir la formula max(1, ataque - defensa)")
    void testFormulaDeDanio() {
        Pokemon charmander = miPokemon.getFaseActual();
        Pokemon rattata = Configuration.crearRattataSalvaje();

        int danioCharmander = charmander.calcularDanioContra(rattata);
        int danioRattata = rattata.calcularDanioContra(charmander);

        assertEquals(17, danioCharmander, "52 - 35 = 17");
        assertEquals(13, danioRattata, "56 - 43 = 13");
    }

    @Test
    @DisplayName("El dano nunca debe ser menor a 1 aunque la defensa supere al ataque")
    void testDanioMinimo() {
        Pokemon atacanteDebil = new Pokemon("Magikarp", 20, 10, 55, Pokemon.SIN_EVOLUCION);
        Pokemon defensorBlindado = new Pokemon("Steelix", 75, 85, 200, Pokemon.SIN_EVOLUCION);

        int danio = atacanteDebil.calcularDanioContra(defensorBlindado);

        assertEquals(1, danio, "10 - 200 es negativo, por lo que el dano debe truncarse en 1");
    }

    @Test
    @DisplayName("El Pokemon debe recuperar todos sus HP al iniciar cada batalla")
    void testReinicioDePuntosDeVida() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(2);

        ResultadoBatalla primera = EntrenamientoService.librarBatalla(miPokemon, horda[0]);
        ResultadoBatalla segunda = EntrenamientoService.librarBatalla(miPokemon, horda[1]);

        assertEquals(primera.hpRestanteAliado(), segunda.hpRestanteAliado(),
                "Ambas batallas deben partir desde los puntos de vida maximos");
        assertTrue(primera.victoria() && segunda.victoria(), "Charmander debe ganar las dos batallas");
    }

    @Test
    @DisplayName("Charmander debe evolucionar exactamente al derrotar al Caterpie numero 30")
    void testEvolucionEnLaBatallaTreinta() {
        Pokemon[] hordaParcial = Configuration.generarHordaCaterpie(29);
        Pokemon[] hordaCompleta = Configuration.generarHordaCaterpie(30);

        LineaEvolutiva pokemonConVeintinueve = Configuration.crearLineaEvolutivaCharmander();
        LineaEvolutiva pokemonConTreinta = Configuration.crearLineaEvolutivaCharmander();

        EntrenamientoService.iniciarEntrenamientoMasivo(pokemonConVeintinueve, hordaParcial);
        ResultadoEntrenamiento resultadoTreinta = EntrenamientoService.iniciarEntrenamientoMasivo(pokemonConTreinta, hordaCompleta);

        assertEquals("Charmander", pokemonConVeintinueve.getFaseActual().getNombre(),
                "Con 29 victorias solo lleva 1450 XP y no evoluciona");
        assertEquals("Charmeleon", pokemonConTreinta.getFaseActual().getNombre(),
                "Con 30 victorias alcanza 1500 XP y evoluciona");
        assertEquals(1, resultadoTreinta.evoluciones(), "Debe registrarse una unica evolucion");
    }

    @Test
    @DisplayName("Charmeleon debe evolucionar a Charizard al derrotar al Caterpie numero 100")
    void testEvolucionEnLaBatallaCien() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(100);

        ResultadoEntrenamiento resultado = EntrenamientoService.iniciarEntrenamientoMasivo(miPokemon, horda);

        assertEquals(5000, resultado.experienciaFinal(), "100 victorias equivalen a 5000 XP");
        assertEquals(2, resultado.evoluciones(), "Deben ocurrir dos evoluciones");
        assertEquals("Charizard", resultado.faseFinal(), "La fase final debe ser Charizard");
    }

    @Test
    @DisplayName("El entrenamiento masivo debe procesar la horda completa sin derrotas")
    void testEntrenamientoMasivo() {
        int cantidadEnemigos = 100_000;
        Pokemon[] horda = Configuration.generarHordaCaterpie(cantidadEnemigos);

        ResultadoEntrenamiento resultado = EntrenamientoService.iniciarEntrenamientoMasivo(miPokemon, horda);

        assertEquals(cantidadEnemigos, resultado.victorias(), "Debe derrotar a los 100.000 enemigos");
        assertEquals(0, resultado.derrotas(), "No debe perder ninguna batalla");
        assertEquals(cantidadEnemigos * EntrenamientoService.EXPERIENCIA_POR_VICTORIA, resultado.experienciaFinal(),
                "La experiencia final debe ser 50 XP por victoria");
        assertEquals("Charizard", resultado.faseFinal(), "Debe terminar la simulacion como Charizard");
        assertTrue(resultado.nanosTotales() > 0, "Debe medirse el tiempo de ejecucion con nanoTime");
    }

    @Test
    @DisplayName("Charizard debe librar las batallas restantes con sus propias estadisticas")
    void testBatallasPosterioresUsanLaFaseEvolucionada() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(100);
        EntrenamientoService.iniciarEntrenamientoMasivo(miPokemon, horda);
        Pokemon caterpie = Configuration.generarHordaCaterpie(1)[0];

        ResultadoBatalla resultado = EntrenamientoService.librarBatalla(miPokemon, caterpie);

        assertEquals("Charizard", miPokemon.getFaseActual().getNombre(), "La fase activa debe ser Charizard");
        assertEquals(78, resultado.hpRestanteAliado(), "Charizard entra con 78 HP y Caterpie cae antes de responder");
        assertEquals(1, resultado.turnos(), "Con 49 de dano Charizard derrota a Caterpie en un solo turno");
    }

    @Test
    @DisplayName("Debe soportar hordas aleatorias de estadisticas bajas")
    void testHordaAleatoria() {
        Pokemon[] horda = Configuration.generarHordaAleatoria(1_000);

        ResultadoEntrenamiento resultado = EntrenamientoService.iniciarEntrenamientoMasivo(miPokemon, horda);

        assertEquals(1_000, resultado.victorias(), "Debe vencer a toda la horda aleatoria");
        assertEquals(0, resultado.derrotas(), "Las estadisticas bajas no deben derrotar al jugador");
    }

    @Test
    @DisplayName("Debe rechazar una linea evolutiva vacia")
    void testEntrenamientoConLineaVacia() {
        LineaEvolutiva lineaVacia = new LineaEvolutiva();
        Pokemon[] horda = Configuration.generarHordaCaterpie(1);

        boolean lanzoExcepcion = false;

        try {
            EntrenamientoService.iniciarEntrenamientoMasivo(lineaVacia, horda);
        } catch (IllegalArgumentException e) {
            lanzoExcepcion = true;
        }

        assertTrue(lanzoExcepcion, "Sin fases enlazadas no se puede entrenar");
    }

}
