package com.example.Service;

import com.example.Model.CajaNegra;
import com.example.Model.LineaEvolutiva;
import com.example.Model.Pokemon;
import com.example.Model.ResultadoSimulacion;
import com.example.Util.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para la rotacion circular del equipo")
class SimulacionServiceTest {

    private static final int ENEMIGOS_POR_TURNO = 50;

    private Queue<LineaEvolutiva> equipo;
    private CajaNegra cajaNegra;

    @BeforeEach
    void setUp() {
        equipo = Configuration.crearEquipoInicial();
        cajaNegra = new CajaNegra(3);
    }

    @Test
    @DisplayName("El equipo inicial se encola en el orden Bulbasaur, Charmander, Squirtle")
    void testColaInicial() {
        assertEquals(3, equipo.size(), "El equipo del caso de prueba tiene tres integrantes");
        assertEquals("[Bulbasaur, Charmander, Squirtle]", SimulacionService.describirEquipo(equipo),
                "La cola debe respetar el orden de encolado del enunciado");
    }

    @Test
    @DisplayName("Caso del enunciado: 250 Caterpie con K = 50 y C = 3")
    void testCasoDePruebaDelEnunciado() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(250);

        ResultadoSimulacion resultado = SimulacionService.simularHordaConRotacion(
                equipo, horda, ENEMIGOS_POR_TURNO, cajaNegra, false);

        assertTrue(resultado.hordaDerrotada(), "Los 250 Caterpie deben quedar derrotados");
        assertEquals(250, resultado.enemigosDerrotados(), "Se deben contabilizar las 250 victorias");
        assertEquals(5, resultado.turnosDeCampo(), "250 enemigos en bloques de 50 son exactamente 5 turnos de campo");
        assertEquals(0, resultado.pokemonRetirados(), "Ningun integrante del equipo debe caer contra Caterpie");
        assertEquals("[Squirtle, Bulbasaur, Charmander]", SimulacionService.describirEquipo(equipo),
                "Tras cinco rotaciones la cola queda encabezada por Squirtle");
        assertEquals("[(Squirtle vs Caterpie: 50), (Bulbasaur vs Caterpie: 50), (Charmander vs Caterpie: 50)]",
                cajaNegra.estadoActual(), "La caja negra final debe coincidir con la traza del enunciado");
    }

    @Test
    @DisplayName("La cola rota tras cada turno de campo dejando al cansado al final")
    void testRotacionTrasElPrimerTurno() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(ENEMIGOS_POR_TURNO);

        SimulacionService.simularHordaConRotacion(equipo, horda, ENEMIGOS_POR_TURNO, cajaNegra, false);

        assertEquals("[Charmander, Squirtle, Bulbasaur]", SimulacionService.describirEquipo(equipo),
                "Bulbasaur pelea el primer bloque y se va a descansar al final de la cola");
        assertEquals("[(Bulbasaur vs Caterpie: 50)]", cajaNegra.estadoActual(),
                "El primer turno de campo produce un unico registro agrupado");
    }

    @Test
    @DisplayName("El equipo conserva a sus tres integrantes despues de la horda")
    void testEquipoIntactoAlFinal() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(250);

        SimulacionService.simularHordaConRotacion(equipo, horda, ENEMIGOS_POR_TURNO, cajaNegra, false);

        assertEquals(3, equipo.size(), "La cola circular no puede perder integrantes durante la rotacion");
    }

    @Test
    @DisplayName("La logica de evolucion de la Fase 1 sigue activa durante la rotacion")
    void testEvolucionDuranteLaRotacion() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(250);

        ResultadoSimulacion resultado = SimulacionService.simularHordaConRotacion(
                equipo, horda, ENEMIGOS_POR_TURNO, cajaNegra, false);

        LineaEvolutiva primerIntegrante = equipo.peek();

        assertEquals(5, resultado.evoluciones(), "Bulbasaur y Charmander evolucionan dos veces y Squirtle una");
        assertEquals("Squirtle", primerIntegrante.getNombreLinea(), "El primero de la cola es la linea de Squirtle");
        assertEquals("Wartortle", primerIntegrante.getFaseActual().getNombre(), "Squirtle acumula 2500 XP y llega a Wartortle");
        assertEquals(2500, primerIntegrante.getExperienciaAcumulada(), "50 victorias por 50 XP son 2500 puntos");
    }

    @Test
    @DisplayName("El nombre de la linea no cambia al evolucionar y mantiene viva la agrupacion")
    void testAgrupacionSobreviveALaEvolucion() {
        Pokemon[] horda = Configuration.generarHordaCaterpie(ENEMIGOS_POR_TURNO);

        SimulacionService.simularHordaConRotacion(equipo, horda, ENEMIGOS_POR_TURNO, cajaNegra, false);

        assertEquals(1, cajaNegra.cantidadRegistros(), "La evolucion a Ivysaur no debe partir el registro en dos");
        assertEquals("Bulbasaur", cajaNegra.obtenerUltimoRegistro().getNombrePokemon(),
                "La caja negra identifica al integrante por el nombre de su linea evolutiva");
    }

    @Test
    @DisplayName("Un equipo vacio no puede enfrentar una horda")
    void testEquipoVacio() {
        Queue<LineaEvolutiva> equipoVacio = Configuration.crearEquipoInicial();
        equipoVacio.clear();

        boolean lanzoExcepcion = false;

        try {
            SimulacionService.simularHordaConRotacion(equipoVacio, Configuration.generarHordaCaterpie(10),
                    ENEMIGOS_POR_TURNO, cajaNegra, false);
        } catch (IllegalArgumentException e) {
            lanzoExcepcion = true;
        }

        assertTrue(lanzoExcepcion, "Sin Pokemon en la cola la simulacion debe detenerse");
    }

}
