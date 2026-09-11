package com.example.Service;

import com.example.Model.CajaNegra;
import com.example.Model.LineaEvolutiva;
import com.example.Model.Pokemon;
import com.example.Model.ReporteBatalla;
import com.example.Model.ResultadoBatalla;
import com.example.Model.ResultadoSimulacion;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Queue;

@Log4j2
public class SimulacionService {

    private static final Logger loggerTiempos = LogManager.getLogger("tiempos");

    private static final int INTERVALO_REPORTE = 10_000;

    public static ResultadoSimulacion simularHordaConRotacion(Queue<LineaEvolutiva> equipo, Pokemon[] horda,
                                                              int enemigosPorTurno, CajaNegra cajaNegra,
                                                              boolean detallarTurnos) {

        if (equipo == null || equipo.isEmpty()) throw new IllegalArgumentException("El equipo no tiene Pokemon en la cola");
        if (horda == null || horda.length == 0) throw new IllegalArgumentException("La horda de enemigos es obligatoria");
        if (enemigosPorTurno <= 0) throw new IllegalArgumentException("El turno de campo debe cubrir al menos un enemigo");
        if (cajaNegra == null) throw new IllegalArgumentException("La caja negra es obligatoria");

        log.info("Inicio de la rotacion | equipo: {} | enemigos en la horda: {} | turno de campo (K): {} | capacidad de la caja negra (C): {}",
                describirEquipo(equipo), horda.length, enemigosPorTurno, cajaNegra.getCapacidad());

        int enemigosDerrotados = 0;
        int turnosDeCampo = 0;
        int rotacionesDeCola = 0;
        int evoluciones = 0;
        int pokemonRetirados = 0;
        long turnosDeCombate = 0;

        long inicioTiempoProceso = System.nanoTime();

        while (enemigosDerrotados < horda.length && !equipo.isEmpty()) {

            LineaEvolutiva pokemonEnCampo = equipo.poll();

            turnosDeCampo++;

            int derrotadosEnElTurno = 0;
            boolean pokemonDebilitado = false;
            String especieEnfrentada = "";

            while (derrotadosEnElTurno < enemigosPorTurno && enemigosDerrotados < horda.length) {

                Pokemon enemigo = horda[enemigosDerrotados];

                ResultadoBatalla resultado = EntrenamientoService.librarBatalla(pokemonEnCampo, enemigo);

                turnosDeCombate += resultado.turnos();

                if (!resultado.victoria()) {

                    pokemonDebilitado = true;

                    log.warn("{} cae derrotado contra el enemigo #{} ({}) y abandona la cola del equipo",
                            pokemonEnCampo.getFaseActual().getNombre(), enemigosDerrotados + 1, enemigo.getNombre());

                    break;

                }

                enemigosDerrotados++;
                derrotadosEnElTurno++;

                especieEnfrentada = enemigo.getNombre();

                cajaNegra.registrarVictoria(pokemonEnCampo.getNombreLinea(), enemigo.getNombre());

                pokemonEnCampo.ganarExperiencia(EntrenamientoService.EXPERIENCIA_POR_VICTORIA);

                while (pokemonEnCampo.intentarEvolucionar()) {

                    evoluciones++;

                    log.info("EVOLUCION en el turno de campo #{} | linea {} | experiencia: {} | nueva fase: {}",
                            turnosDeCampo, pokemonEnCampo.getNombreLinea(),
                            pokemonEnCampo.getExperienciaAcumulada(), pokemonEnCampo.getFaseActual().getNombre());

                }

                if (enemigosDerrotados % INTERVALO_REPORTE == 0) {

                    log.info("Progreso | enemigos derrotados: {}/{} | en campo: {} | caja negra: {}",
                            enemigosDerrotados, horda.length, pokemonEnCampo.getFaseActual().getNombre(),
                            cajaNegra.estadoActual());

                }

            }

            if (pokemonDebilitado) {

                pokemonRetirados++;

                continue;

            }

            equipo.offer(pokemonEnCampo);

            rotacionesDeCola++;

            if (detallarTurnos) {

                log.info("Turno de campo #{} | sale {} | derrota {} {} | cola rota: {}",
                        turnosDeCampo, pokemonEnCampo.getNombreLinea(), derrotadosEnElTurno,
                        especieEnfrentada, describirEquipo(equipo));

                log.info("Caja Negra: {}", cajaNegra.estadoActual());

            }
            else {

                log.debug("Turno de campo #{} | sale {} | derrota {} enemigos | cola rota: {}",
                        turnosDeCampo, pokemonEnCampo.getNombreLinea(), derrotadosEnElTurno, describirEquipo(equipo));

            }

        }

        long nanosTotales = System.nanoTime() - inicioTiempoProceso;

        ResultadoSimulacion resultadoSimulacion = new ResultadoSimulacion(
                horda.length, enemigosDerrotados, turnosDeCampo, rotacionesDeCola, evoluciones,
                turnosDeCombate, pokemonRetirados, describirEquipo(equipo), cajaNegra.estadoActual(),
                nanosTotales);

        loggerTiempos.info("Rotacion circular | enemigos: {} | turnos de campo: {} | turnos de combate: {} | tiempo: {} s ({} ms) | nanoTime: {} ns",
                enemigosDerrotados, turnosDeCampo, turnosDeCombate,
                String.format(Locale.ROOT, "%.4f", resultadoSimulacion.duracionSegundos()),
                String.format(Locale.ROOT, "%.2f", resultadoSimulacion.milisegundos()),
                nanosTotales);

        loggerTiempos.info("Costo promedio por enemigo derrotado | {} ns",
                enemigosDerrotados == 0 ? 0 : nanosTotales / enemigosDerrotados);

        log.info("Fin de la rotacion | horda derrotada: {} | turnos de campo: {} | evoluciones: {}",
                resultadoSimulacion.hordaDerrotada(), turnosDeCampo, evoluciones);

        imprimirCajaNegra(cajaNegra);

        return resultadoSimulacion;

    }

    public static void imprimirCajaNegra(CajaNegra cajaNegra) {

        if (cajaNegra == null) throw new IllegalArgumentException("La caja negra es obligatoria");

        log.info("----- CAJA NEGRA | capacidad: {} | registros vivos: {} | registros descartados: {} -----",
                cajaNegra.getCapacidad(), cajaNegra.cantidadRegistros(), cajaNegra.getRegistrosDescartados());

        for (int posicion = 0; posicion < cajaNegra.cantidadRegistros(); posicion++) {

            ReporteBatalla reporte = cajaNegra.obtenerRegistro(posicion);

            log.info("Registro {} | {} vs {} | derrotados: {}",
                    posicion + 1, reporte.getNombrePokemon(), reporte.getNombreEnemigo(), reporte.getCantidadDerrotados());

        }

        log.info("Caja Negra FINAL: {}", cajaNegra.estadoActual());

    }

    public static String describirEquipo(Queue<LineaEvolutiva> equipo) {

        if (equipo == null || equipo.isEmpty()) return "[]";

        String descripcion = "";

        for (LineaEvolutiva lineaEvolutiva : equipo) {
            descripcion += (descripcion.isEmpty() ? "" : ", ") + lineaEvolutiva.getNombreLinea();
        }

        return "[" + descripcion + "]";

    }

}
