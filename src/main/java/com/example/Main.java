package com.example;

import com.example.Model.LineaEvolutiva;
import com.example.Model.Pokemon;
import com.example.Model.ResultadoBatalla;
import com.example.Model.ResultadoEntrenamiento;
import com.example.Service.EntrenamientoService;
import com.example.Util.Configuration;
import com.example.Util.PerformanceReporter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Log4j2
public class Main {

    private static final Logger loggerTiempos = LogManager.getLogger("tiempos");

    private static final int CANTIDAD_ENEMIGOS = 100_000;

    public static void main(String[] args) {

        try {

            log.info("===== FASE 1 | ESTRUCTURAS DE DATOS LINEALES Y ANALISIS DE COMPLEJIDAD =====");

            analizarEstructura();
            ejecutarBatallaDeValidacion();
            ejecutarEntrenamientoMasivo();

            log.info("===== SIMULACION FINALIZADA | revise la carpeta 'logs' para el detalle =====");

        }
        catch (Exception e) {
            log.error("Error en el sistema: {}", e.getMessage(), e);
        }

    }

    private static void analizarEstructura() {

        log.info("----- 1. ESTRUCTURA DE DATOS: LINEA EVOLUTIVA -----");

        LineaEvolutiva lineaEvolutiva = Configuration.crearLineaEvolutivaCharmander();

        log.info("Recorrido de la lista enlazada: {}", lineaEvolutiva.recorrerLinea());
        log.info("Estado inicial: {}", lineaEvolutiva);

        PerformanceReporter.medirPesoObjeto(lineaEvolutiva.getFaseActual(), "Nodo Pokemon (Charmander)");
        PerformanceReporter.imprimirFootprint(lineaEvolutiva.getFaseActual(), "Nodo Pokemon (Charmander)");

        PerformanceReporter.medirPesoObjeto(lineaEvolutiva, "LineaEvolutiva completa");
        PerformanceReporter.imprimirFootprint(lineaEvolutiva, "LineaEvolutiva completa");

    }

    private static void ejecutarBatallaDeValidacion() {

        log.info("----- 2. VALIDACION LOGICA: CHARMANDER VS RATTATA -----");

        LineaEvolutiva lineaDemostracion = Configuration.crearLineaEvolutivaCharmander();
        Pokemon rattata = Configuration.crearRattataSalvaje();

        Pokemon charmander = lineaDemostracion.getFaseActual();

        log.info("Dano de {} sobre {}: max(1, {} - {}) = {}",
                charmander.getNombre(), rattata.getNombre(), charmander.getAtaque(), rattata.getDefensa(),
                charmander.calcularDanioContra(rattata));

        log.info("Dano de {} sobre {}: max(1, {} - {}) = {}",
                rattata.getNombre(), charmander.getNombre(), rattata.getAtaque(), charmander.getDefensa(),
                rattata.calcularDanioContra(charmander));

        long inicioTiempoProceso = System.nanoTime();

        ResultadoBatalla resultado = EntrenamientoService.librarBatalla(lineaDemostracion, rattata);

        long nanosBatalla = System.nanoTime() - inicioTiempoProceso;

        if (resultado.victoria()) {
            lineaDemostracion.ganarExperiencia(EntrenamientoService.EXPERIENCIA_POR_VICTORIA);
            lineaDemostracion.intentarEvolucionar();
        }

        log.info("Resultado: {}", resultado);
        log.info("{} termina con {}/{} HP y {} puntos de experiencia",
                lineaDemostracion.getFaseActual().getNombre(), resultado.hpRestanteAliado(),
                charmander.getPuntosDeVidaMaximos(), lineaDemostracion.getExperienciaAcumulada());

        loggerTiempos.info("Batalla unitaria contra {} | turnos: {} | tiempo: {} ns",
                rattata.getNombre(), resultado.turnos(), nanosBatalla);

    }

    private static void ejecutarEntrenamientoMasivo() {

        log.info("----- 3. PRUEBA DE ESTRES: HORDE TRAINING ({} ENEMIGOS) -----", CANTIDAD_ENEMIGOS);

        LineaEvolutiva miPokemon = Configuration.crearLineaEvolutivaCharmander();

        long inicioGeneracion = System.nanoTime();
        Pokemon[] hordaEnemigos = Configuration.generarHordaCaterpie(CANTIDAD_ENEMIGOS);
        long nanosGeneracion = System.nanoTime() - inicioGeneracion;

        loggerTiempos.info("Generacion de la horda | enemigos: {} | tiempo: {} s",
                hordaEnemigos.length,
                String.format(Locale.ROOT, "%.4f", nanosGeneracion / 1_000_000_000.0));

        PerformanceReporter.medirPesoObjeto(hordaEnemigos[0], "Nodo enemigo (Caterpie)");
        PerformanceReporter.medirPesoObjeto(hordaEnemigos, "Horda completa (" + CANTIDAD_ENEMIGOS + " enemigos)");

        long memoriaAntes = PerformanceReporter.capturarMemoriaDisponible();
        PerformanceReporter.reportarMemoriaSistema();

        ResultadoEntrenamiento resultado = EntrenamientoService.iniciarEntrenamientoMasivo(miPokemon, hordaEnemigos);

        long memoriaDespues = PerformanceReporter.capturarMemoriaDisponible();

        PerformanceReporter.compararMemoriaDisponible(memoriaAntes, memoriaDespues,
                "ciclo masivo de " + CANTIDAD_ENEMIGOS + " batallas");
        PerformanceReporter.reportarMemoriaSistema();

        log.info("Estado final de la linea evolutiva: {}", miPokemon);
        log.info("Resumen del entrenamiento: {}", resultado.resumen());

    }

}
