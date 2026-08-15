package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampamentoTest {

    @Test
    void contarDevuelveLaFrecuenciaDeCadaLetra() {
        HashMap<Character, Integer> conteo = Campamento.contar("AABCC");
        assertEquals(2, conteo.get('A'));
        assertEquals(1, conteo.get('B'));
        assertEquals(2, conteo.get('C'));
    }

    @Test
    void tieneMedicamentosValidaCantidadExacta() {
        Campamento c = new Campamento("Test");
        c.getInventario().put('A', 2);
        c.getInventario().put('B', 1);

        assertTrue(c.tieneMedicamentos("AAB"));
        assertFalse(c.tieneMedicamentos("AAAB"));
        assertFalse(c.tieneMedicamentos("AC"));
    }

    @Test
    void consumirMedicamentosDescuentaDelInventario() {
        Campamento c = new Campamento("Test");
        c.getInventario().put('A', 3);
        c.getInventario().put('B', 2);

        c.consumirMedicamentos("AB");

        assertEquals(2, c.getInventario().get('A'));
        assertEquals(1, c.getInventario().get('B'));
    }
}
