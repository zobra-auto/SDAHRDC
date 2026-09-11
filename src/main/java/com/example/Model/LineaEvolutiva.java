package com.example.Model;

public class LineaEvolutiva {

    private Pokemon primeraFase;

    private Pokemon faseActual;

    private int experienciaAcumulada;
    private int cantidadFases;

    public LineaEvolutiva() {

        this.primeraFase = null;
        this.faseActual = null;
        this.experienciaAcumulada = 0;
        this.cantidadFases = 0;

    }

    public void agregarFase(Pokemon nuevaFase) {

        if (nuevaFase == null) throw new IllegalArgumentException("No se puede enlazar una fase nula");

        if (primeraFase == null) {
            primeraFase = nuevaFase;
            faseActual = nuevaFase;
        }
        else {

            Pokemon referencia = primeraFase;

            while (referencia.getSiguienteEvolucion() != null) {
                referencia = referencia.getSiguienteEvolucion();
            }

            referencia.setSiguienteEvolucion(nuevaFase);

        }

        cantidadFases++;

    }

    public void ganarExperiencia(int experiencia) {
        this.experienciaAcumulada += experiencia;
    }

    public boolean intentarEvolucionar() {

        if (faseActual == null || !faseActual.tieneEvolucionDisponible()) return false;

        if (experienciaAcumulada < faseActual.getExperienciaRequerida()) return false;

        faseActual = faseActual.getSiguienteEvolucion();

        return true;

    }

    public String recorrerLinea() {

        if (primeraFase == null) return "Linea evolutiva vacia";

        String recorrido = "";

        Pokemon referencia = primeraFase;

        while (referencia != null) {

            recorrido += "[" + referencia.getNombre() + (referencia == faseActual ? " (actual)" : "") + "] -> ";

            referencia = referencia.getSiguienteEvolucion();

        }

        return recorrido + "null";

    }

    public boolean estaVacia() {
        return primeraFase == null;
    }

    public boolean esFaseFinal() {
        return faseActual != null && !faseActual.tieneEvolucionDisponible();
    }

    public Pokemon getPrimeraFase() {
        return primeraFase;
    }

    public Pokemon getFaseActual() {
        return faseActual;
    }

    public int getExperienciaAcumulada() {
        return experienciaAcumulada;
    }

    public int getCantidadFases() {
        return cantidadFases;
    }

    @Override
    public String toString() {
        return "LineaEvolutiva {" +
                "faseActual = " + (faseActual == null ? "null" : faseActual.getNombre()) +
                ", experienciaAcumulada = " + experienciaAcumulada +
                ", cantidadFases = " + cantidadFases +
                ", linea = " + recorrerLinea() +
                '}';
    }

}
