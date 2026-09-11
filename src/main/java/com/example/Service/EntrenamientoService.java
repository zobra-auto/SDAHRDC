package com.example.Service;

import com.example.Model.LineaEvolutiva;
import com.example.Model.Pokemon;
import com.example.Model.ResultadoBatalla;
import com.example.Model.ResultadoEntrenamiento;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Log4j2
public class EntrenamientoService {

    private static final Logger loggerTiempos = LogManager.getLogger("tiempos");

    public static final int EXPERIENCIA_POR_VICTORIA = 50;

    private static final int INTERVALO_REPORTE = 10_000;

    public static ResultadoBatalla librarBatalla(LineaEvolutiva miPokemon, Pokemon enemigo) {

        Pokemon aliado = miPokemon.getFaseActual();

        int hpAliado = aliado.getPuntosDeVidaMaximos();
        int hpEnemigo = enemigo.getPuntosDeVidaMaximos();
        int turnos = 0;

        log.debug("Inicia batalla | {} ({} HP) vs {} ({} HP)",
                aliado.getNombre(), hpAliado, enemigo.getNombre(), hpEnemigo);

        while (hpAliado > 0 && hpEnemigo > 0) {

            turnos++;
            int danioAliado = aliado.calcularDanioContra(enemigo);
            hpEnemigo -= danioAliado;

            log.debug("Turno {} | {} ataca | danio: {} | HP de {}: {}",
                    turnos, aliado.getNombre(), danioAliado, enemigo.getNombre(), Math.max(hpEnemigo, 0));

            if (hpEnemigo <= 0) break;

            turnos++;
            int danioEnemigo = enemigo.calcularDanioContra(aliado);
            hpAliado -= danioEnemigo;

            log.debug("Turno {} | {} ataca | danio: {} | HP de {}: {}",
                    turnos, enemigo.getNombre(), danioEnemigo, aliado.getNombre(), Math.max(hpAliado, 0));

        }

        boolean victoria = hpEnemigo <= 0;

        log.debug("Fin de batalla | victoria: {} | turnos: {}", victoria, turnos);

        return new ResultadoBatalla(victoria, turnos, Math.max(hpAliado, 0), Math.max(hpEnemigo, 0));

    }

    public static ResultadoEntrenamiento iniciarEntrenamientoMasivo(LineaEvolutiva miPokemon, Pokemon[] hordaEnemigos) {

        if (miPokemon == null || miPokemon.estaVacia()) throw new IllegalArgumentException("La linea evolutiva no tiene fases");
        if (hordaEnemigos == null) throw new IllegalArgumentException("La horda de enemigos es obligatoria");

        log.info("Inicio de entrenamiento masivo | fase inicial: {} | enemigos en la horda: {}",
                miPokemon.getFaseActual().getNombre(), hordaEnemigos.length);

        int victorias = 0;
        int derrotas = 0;
        int evoluciones = 0;
        long turnosTotales = 0;

        long inicioTiempoProceso = System.nanoTime();

        for (int i = 0; i < hordaEnemigos.length; i++) {

            ResultadoBatalla resultado = librarBatalla(miPokemon, hordaEnemigos[i]);

            turnosTotales += resultado.turnos();

            if (!resultado.victoria()) {

                derrotas++;

                log.warn("Derrota en la batalla #{} contra {} | se detiene el entrenamiento",
                        i + 1, hordaEnemigos[i].getNombre());

                break;

            }

            victorias++;

            miPokemon.ganarExperiencia(EXPERIENCIA_POR_VICTORIA);

            while (miPokemon.intentarEvolucionar()) {

                evoluciones++;

                log.info("EVOLUCION tras la batalla #{} | experiencia acumulada: {} | nueva fase: {}",
                        victorias, miPokemon.getExperienciaAcumulada(), miPokemon.getFaseActual());

            }

            if (victorias % INTERVALO_REPORTE == 0) {

                log.info("Progreso | enemigos derrotados: {} | fase actual: {} | experiencia: {}",
                        victorias, miPokemon.getFaseActual().getNombre(), miPokemon.getExperienciaAcumulada());

            }

        }

        long nanosTotales = System.nanoTime() - inicioTiempoProceso;

        ResultadoEntrenamiento resultadoEntrenamiento = new ResultadoEntrenamiento(
                hordaEnemigos.length, victorias, derrotas, turnosTotales,
                miPokemon.getExperienciaAcumulada(), evoluciones,
                miPokemon.getFaseActual().getNombre(), nanosTotales);

        loggerTiempos.info("Entrenamiento masivo | enemigos: {} | turnos: {} | tiempo: {} s ({} ms) | nanoTime: {} ns",
                hordaEnemigos.length, turnosTotales,
                String.format(Locale.ROOT, "%.4f", resultadoEntrenamiento.duracionSegundos()),
                String.format(Locale.ROOT, "%.2f", resultadoEntrenamiento.milisegundos()),
                nanosTotales);

        loggerTiempos.info("Costo promedio por batalla | {} ns",
                victorias == 0 ? 0 : nanosTotales / victorias);

        log.info("Fin de entrenamiento masivo | victorias: {} | evoluciones: {} | fase final: {}",
                victorias, evoluciones, miPokemon.getFaseActual().getNombre());

        return resultadoEntrenamiento;

    }

}
