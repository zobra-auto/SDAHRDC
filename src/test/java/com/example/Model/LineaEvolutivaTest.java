package com.example.Model;

import com.example.Util.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para la lista enlazada LineaEvolutiva")
class LineaEvolutivaTest {

    private LineaEvolutiva lineaEvolutiva;

    @BeforeEach
    void setUp() {
        lineaEvolutiva = Configuration.crearLineaEvolutivaCharmander();
    }

    @Test
    @DisplayName("Debe enlazar las tres fases en el orden Charmander -> Charmeleon -> Charizard")
    void testEnlaceDeFases() {
        int cantidadFasesEsperada = 3;

        Pokemon primeraFase = lineaEvolutiva.getPrimeraFase();
        Pokemon segundaFase = primeraFase.getSiguienteEvolucion();
        Pokemon terceraFase = segundaFase.getSiguienteEvolucion();

        assertEquals(cantidadFasesEsperada, lineaEvolutiva.getCantidadFases(), "La linea debe tener 3 fases");
        assertEquals("Charmander", primeraFase.getNombre(), "La primera fase debe ser Charmander");
        assertEquals("Charmeleon", segundaFase.getNombre(), "La segunda fase debe ser Charmeleon");
        assertEquals("Charizard", terceraFase.getNombre(), "La tercera fase debe ser Charizard");
        assertTrue(terceraFase.getSiguienteEvolucion() == null, "La fase final debe apuntar a null");
    }

    @Test
    @DisplayName("El puntero faseActual debe iniciar en la primera fase")
    void testPunteroInicial() {
        Pokemon faseActual = lineaEvolutiva.getFaseActual();

        assertTrue(lineaEvolutiva.getPrimeraFase() == faseActual, "faseActual debe apuntar a la cabeza de la lista");
        assertEquals("Charmander", faseActual.getNombre(), "El Pokemon inicia como Charmander");
        assertEquals(0, lineaEvolutiva.getExperienciaAcumulada(), "La experiencia inicial debe ser cero");
    }

    @Test
    @DisplayName("No debe evolucionar mientras la experiencia sea menor a la requerida")
    void testNoEvolucionaSinExperienciaSuficiente() {
        lineaEvolutiva.ganarExperiencia(1499);

        boolean evoluciono = lineaEvolutiva.intentarEvolucionar();

        assertFalse(evoluciono, "Con 1499 XP no se alcanza el umbral de 1500");
        assertEquals("Charmander", lineaEvolutiva.getFaseActual().getNombre(), "Debe seguir siendo Charmander");
    }

    @Test
    @DisplayName("Debe avanzar el puntero al alcanzar la experiencia requerida de la fase actual")
    void testEvolucionaConExperienciaSuficiente() {
        lineaEvolutiva.ganarExperiencia(1500);

        boolean evoluciono = lineaEvolutiva.intentarEvolucionar();

        assertTrue(evoluciono, "Con 1500 XP Charmander debe evolucionar");
        assertEquals("Charmeleon", lineaEvolutiva.getFaseActual().getNombre(), "La fase actual debe ser Charmeleon");
        assertEquals(58, lineaEvolutiva.getFaseActual().getPuntosDeVidaMaximos(), "Las estadisticas deben ser las de Charmeleon");
        assertEquals(64, lineaEvolutiva.getFaseActual().getAtaque(), "El ataque debe subir a 64");
    }

    @Test
    @DisplayName("Debe recorrer la linea completa en dos evoluciones encadenadas")
    void testEvolucionHastaLaFaseFinal() {
        lineaEvolutiva.ganarExperiencia(5000);

        int evoluciones = 0;
        while (lineaEvolutiva.intentarEvolucionar()) {
            evoluciones++;
        }

        assertEquals(2, evoluciones, "Con 5000 XP se cruzan los dos umbrales");
        assertEquals("Charizard", lineaEvolutiva.getFaseActual().getNombre(), "La fase final debe ser Charizard");
        assertTrue(lineaEvolutiva.esFaseFinal(), "Charizard es la ultima fase de la linea");
    }

    @Test
    @DisplayName("La fase final no debe evolucionar aunque acumule experiencia infinita")
    void testFaseFinalNoEvoluciona() {
        lineaEvolutiva.ganarExperiencia(1_000_000);
        while (lineaEvolutiva.intentarEvolucionar()) {
        }

        boolean evolucionExtra = lineaEvolutiva.intentarEvolucionar();

        assertFalse(evolucionExtra, "Charizard tiene experienciaRequerida = -1 y no evoluciona");
        assertEquals("Charizard", lineaEvolutiva.getFaseActual().getNombre(), "La fase actual no debe cambiar");
    }

    @Test
    @DisplayName("La lista debe conservar la primera fase aunque el puntero actual avance")
    void testCabezaRealSeConserva() {
        lineaEvolutiva.ganarExperiencia(5000);
        while (lineaEvolutiva.intentarEvolucionar()) {
        }

        String recorrido = lineaEvolutiva.recorrerLinea();

        assertEquals("Charmander", lineaEvolutiva.getPrimeraFase().getNombre(), "La cabeza real no se mueve");
        assertTrue(recorrido.contains("Charmander"), "El recorrido debe incluir la fase base");
        assertTrue(recorrido.contains("Charizard (actual)"), "El recorrido debe marcar la fase activa");
    }

    @Test
    @DisplayName("Una linea evolutiva recien creada debe estar vacia")
    void testListaVacia() {
        LineaEvolutiva lineaVacia = new LineaEvolutiva();

        assertTrue(lineaVacia.estaVacia(), "Sin fases enlazadas la lista esta vacia");
        assertTrue(lineaVacia.getFaseActual() == null, "No hay fase actual");
        assertFalse(lineaVacia.intentarEvolucionar(), "Una lista vacia no puede evolucionar");
    }

    @Test
    @DisplayName("Los nodos deben comparar por estadisticas y no por posicion en la lista")
    void testEqualsHashCodeDelNodo() {
        Pokemon charmanderUno = new Pokemon("Charmander", 39, 52, 43, 1500);
        Pokemon charmanderDos = new Pokemon("Charmander", 39, 52, 43, 1500);
        Pokemon charmeleon = new Pokemon("Charmeleon", 58, 64, 58, 5000);

        charmanderDos.setSiguienteEvolucion(charmeleon);

        assertEquals(charmanderUno, charmanderDos, "Dos nodos con las mismas estadisticas son iguales");
        assertEquals(charmanderUno.hashCode(), charmanderDos.hashCode(), "El hashCode debe ser consistente con equals");
        assertNotEquals(charmanderUno, charmeleon, "Nodos con distintas estadisticas no son iguales");
    }

}
