package org.example;

import org.example.service.SimuladorTriage;

public class Main {
    static void main() {
        SimuladorTriage simulador = new SimuladorTriage(42L);
        simulador.iniciar();
        simulador.ejecutar();
        simulador.imprimirResultados();
    }
}
