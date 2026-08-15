package org.example.service;

import org.example.model.Campamento;
import org.example.model.Paciente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimuladorTriageTest {

    @Test
    void pacienteConRacionYRecetaCorrectasEsSanado() {
        SimuladorTriage sim = new SimuladorTriage(1L);
        Campamento c0 = new Campamento("C0");
        Campamento c1 = new Campamento("C1");
        c0.getRaciones().push(1);
        c0.getInventario().put('A', 2);
        c0.agregarPaciente(new Paciente(1, 1, "AA"));
        sim.setCampamento(0, c0);
        sim.setCampamento(1, c1);

        sim.atender(c0, 0);

        assertEquals(1, sim.getSanados());
        assertTrue(c0.getRaciones().isEmpty());
        assertEquals(0, c0.getInventario().get('A'));
        assertTrue(c0.getPacientes().isEmpty());
    }

    @Test
    void racionDistintaConMedicinasVaAlFinalDeLaColaYSigueVivo() {
        SimuladorTriage sim = new SimuladorTriage(1L);
        Campamento c0 = new Campamento("C0");
        Campamento c1 = new Campamento("C1");
        c0.getRaciones().push(0);
        c0.getInventario().put('A', 2);
        Paciente p = new Paciente(1, 1, "AA");
        c0.agregarPaciente(p);
        sim.setCampamento(0, c0);
        sim.setCampamento(1, c1);

        // Con un solo paciente en cola, intentosFallidos llega a 1 (== size) en la
        // siguiente vuelta y se dispara el bloqueo, así que el paciente termina
        // trasladado al campamento 1 conservando 2 de sus 3 intentos.
        sim.atender(c0, 0);

        assertEquals(0, sim.getSanados());
        assertEquals(1, c1.getPacientes().size());
        assertEquals(2, c1.getPacientes().peek().getIntentosRestantes());
    }

    @Test
    void sinMedicamentosSuficientesElPacienteSeTrasladaConUnIntentoMenos() {
        SimuladorTriage sim = new SimuladorTriage(1L);
        Campamento c0 = new Campamento("C0");
        Campamento c1 = new Campamento("C1");
        c0.getRaciones().push(1);
        c0.agregarPaciente(new Paciente(1, 1, "AA"));
        sim.setCampamento(0, c0);
        sim.setCampamento(1, c1);

        sim.atender(c0, 0);

        assertEquals(0, sim.getMuertos());
        assertEquals(1, c1.getPacientes().size());
        assertEquals(2, c1.getPacientes().peek().getIntentosRestantes());
    }

    @Test
    void pacienteConUnSoloIntentoMuereAlTrasladarse() {
        SimuladorTriage sim = new SimuladorTriage(1L);
        Campamento c0 = new Campamento("C0");
        Campamento c1 = new Campamento("C1");
        c0.getRaciones().push(1);
        Paciente p = new Paciente(1, 1, "AA");
        p.restarIntento();
        p.restarIntento();
        c0.agregarPaciente(p);
        sim.setCampamento(0, c0);
        sim.setCampamento(1, c1);

        sim.atender(c0, 0);

        assertEquals(1, sim.getMuertos());
        assertTrue(c1.getPacientes().isEmpty());
    }

    @Test
    void colaDondeNadieQuiereElTopeGeneraBloqueoYTrasladaATodos() {
        SimuladorTriage sim = new SimuladorTriage(1L);
        Campamento c0 = new Campamento("C0");
        Campamento c1 = new Campamento("C1");
        c0.getRaciones().push(1);
        c0.getInventario().put('A', 10);
        c0.agregarPaciente(new Paciente(1, 0, "A"));
        c0.agregarPaciente(new Paciente(2, 0, "A"));
        sim.setCampamento(0, c0);
        sim.setCampamento(1, c1);

        sim.atender(c0, 0);

        assertTrue(c0.getPacientes().isEmpty());
        assertEquals(2, c1.getPacientes().size());
        assertFalse(c0.getRaciones().isEmpty());
    }
}
