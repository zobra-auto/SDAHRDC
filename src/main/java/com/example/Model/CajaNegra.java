package com.example.Model;

import java.util.LinkedList;

public class CajaNegra {

    private final LinkedList<ReporteBatalla> registros;

    private final int capacidad;

    private int registrosDescartados;
    private int agrupaciones;

    public CajaNegra(int capacidad) {

        if (capacidad <= 0) throw new IllegalArgumentException("La capacidad de la caja negra debe ser mayor a cero");

        this.registros = new LinkedList<>();
        this.capacidad = capacidad;
        this.registrosDescartados = 0;
        this.agrupaciones = 0;

    }

    public void registrarVictoria(String nombrePokemon, String nombreEnemigo) {

        if (!registros.isEmpty() && registros.getLast().correspondeA(nombrePokemon, nombreEnemigo)) {

            registros.getLast().incrementarDerrotados();
            agrupaciones++;

            return;

        }

        if (registros.size() == capacidad) {

            registros.removeFirst();
            registrosDescartados++;

        }

        registros.addLast(new ReporteBatalla(nombrePokemon, nombreEnemigo));

    }

    public String estadoActual() {

        if (registros.isEmpty()) return "[]";

        String estado = "";

        for (ReporteBatalla reporte : registros) {
            estado += (estado.isEmpty() ? "" : ", ") + reporte.resumen();
        }

        return "[" + estado + "]";

    }

    public ReporteBatalla obtenerRegistro(int posicion) {
        return registros.get(posicion);
    }

    public ReporteBatalla obtenerUltimoRegistro() {
        return registros.isEmpty() ? null : registros.getLast();
    }

    public int cantidadRegistros() {
        return registros.size();
    }

    public boolean estaVacia() {
        return registros.isEmpty();
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getRegistrosDescartados() {
        return registrosDescartados;
    }

    public int getAgrupaciones() {
        return agrupaciones;
    }

    @Override
    public String toString() {
        return "CajaNegra {" +
                "capacidad = " + capacidad +
                ", registros = " + registros.size() +
                ", agrupaciones = " + agrupaciones +
                ", registrosDescartados = " + registrosDescartados +
                ", estado = " + estadoActual() +
                '}';
    }

}
