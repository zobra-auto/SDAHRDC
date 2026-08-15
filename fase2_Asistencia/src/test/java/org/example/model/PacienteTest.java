package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacienteTest {

    @Test
    void equalsYHashCodeSeBasanEnLaCedula() {
        Paciente p1 = new Paciente(10, 0, "AAB");
        Paciente p2 = new Paciente(10, 1, "CCC");
        Paciente p3 = new Paciente(11, 0, "AAB");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertFalse(p1.equals(p3));
    }

    @Test
    void toStringEsLegible() {
        Paciente p = new Paciente(5, 1, "AABCC");
        assertEquals("Paciente[cc=5, racion=1, receta=AABCC, intentos=3]", p.toString());
    }

    @Test
    void restarIntentoHastaMorir() {
        Paciente p = new Paciente(1, 0, "A");
        assertFalse(p.estaMuerto());

        p.restarIntento();
        p.restarIntento();
        assertFalse(p.estaMuerto());

        p.restarIntento();
        assertTrue(p.estaMuerto());
        assertEquals(0, p.getIntentosRestantes());
    }
}
