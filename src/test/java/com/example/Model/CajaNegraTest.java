package com.example.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para la Caja Negra de memoria limitada")
class CajaNegraTest {

    private CajaNegra cajaNegra;

    @BeforeEach
    void setUp() {
        cajaNegra = new CajaNegra(3);
    }

    @Test
    @DisplayName("Regla A: victorias consecutivas del mismo par no crean nodos nuevos")
    void testAgrupacionConsecutiva() {
        int victorias = 50;

        for (int i = 0; i < victorias; i++) {
            cajaNegra.registrarVictoria("Bulbasaur", "Caterpie");
        }

        assertEquals(1, cajaNegra.cantidadRegistros(), "Las 50 victorias iguales deben caber en un solo registro");
        assertEquals(victorias, cajaNegra.obtenerUltimoRegistro().getCantidadDerrotados(), "El contador debe acumular las 50 derrotas");
        assertEquals(victorias - 1, cajaNegra.getAgrupaciones(), "Solo la primera victoria crea el nodo, las otras 49 se agrupan");
    }

    @Test
    @DisplayName("Regla A: cambiar de Pokemon atacante rompe la agrupacion")
    void testCambioDePokemonRompeAgrupacion() {
        cajaNegra.registrarVictoria("Bulbasaur", "Caterpie");
        cajaNegra.registrarVictoria("Charmander", "Caterpie");

        assertEquals(2, cajaNegra.cantidadRegistros(), "Un atacante distinto obliga a crear un registro nuevo");
        assertEquals("Charmander", cajaNegra.obtenerUltimoRegistro().getNombrePokemon(), "El ultimo registro debe ser el de Charmander");
        assertEquals(1, cajaNegra.obtenerRegistro(0).getCantidadDerrotados(), "El registro de Bulbasaur conserva su contador");
    }

    @Test
    @DisplayName("Regla A: cambiar de especie enemiga rompe la agrupacion")
    void testCambioDeEnemigoRompeAgrupacion() {
        cajaNegra.registrarVictoria("Bulbasaur", "Caterpie");
        cajaNegra.registrarVictoria("Bulbasaur", "Rattata");
        cajaNegra.registrarVictoria("Bulbasaur", "Rattata");

        assertEquals(2, cajaNegra.cantidadRegistros(), "Una especie enemiga distinta obliga a crear un registro nuevo");
        assertEquals(2, cajaNegra.obtenerUltimoRegistro().getCantidadDerrotados(), "Las dos victorias contra Rattata se agrupan entre si");
    }

    @Test
    @DisplayName("Regla B: al llegar a la capacidad se elimina el registro mas antiguo")
    void testLimiteFifo() {
        cajaNegra.registrarVictoria("Bulbasaur", "Caterpie");
        cajaNegra.registrarVictoria("Charmander", "Caterpie");
        cajaNegra.registrarVictoria("Squirtle", "Caterpie");
        cajaNegra.registrarVictoria("Pikachu", "Caterpie");

        assertEquals(3, cajaNegra.cantidadRegistros(), "La caja negra nunca puede superar su capacidad");
        assertEquals("Charmander", cajaNegra.obtenerRegistro(0).getNombrePokemon(), "El registro de Bulbasaur es el que debe desaparecer");
        assertEquals("Pikachu", cajaNegra.obtenerUltimoRegistro().getNombrePokemon(), "El registro nuevo entra al final de la lista");
        assertEquals(1, cajaNegra.getRegistrosDescartados(), "Se debe contabilizar un unico descarte");
    }

    @Test
    @DisplayName("Regla B: la caja negra jamas excede la capacidad aunque entren muchos registros")
    void testCapacidadSiempreRespetada() {
        String[] atacantes = {"Bulbasaur", "Charmander", "Squirtle", "Pikachu", "Eevee", "Snorlax", "Gengar"};

        for (String atacante : atacantes) {

            cajaNegra.registrarVictoria(atacante, "Caterpie");

            assertTrue(cajaNegra.cantidadRegistros() <= cajaNegra.getCapacidad(), "El tamano nunca debe superar C = 3");

        }

        assertEquals("[(Eevee vs Caterpie: 1), (Snorlax vs Caterpie: 1), (Gengar vs Caterpie: 1)]",
                cajaNegra.estadoActual(), "Solo deben sobrevivir los tres registros mas recientes");
    }

    @Test
    @DisplayName("Una caja negra recien creada esta vacia y sin descartes")
    void testCajaNegraVacia() {
        assertTrue(cajaNegra.estaVacia(), "La caja negra inicia sin registros");
        assertEquals(0, cajaNegra.cantidadRegistros(), "La cantidad de registros inicial debe ser cero");
        assertEquals("[]", cajaNegra.estadoActual(), "El estado de una caja vacia se imprime como lista vacia");
    }

    @Test
    @DisplayName("La capacidad debe ser mayor a cero")
    void testCapacidadInvalida() {
        boolean lanzoExcepcion = false;

        try {
            new CajaNegra(0);
        } catch (IllegalArgumentException e) {
            lanzoExcepcion = true;
        }

        assertTrue(lanzoExcepcion, "Una caja negra sin capacidad no tiene sentido y debe rechazarse");
    }

}
