package org.example.model;

import java.util.Objects;

public class Paciente {

    private final int cc;
    private final int preferenciaRacion;
    private final String recetaMedica;
    private int intentosRestantes;

    public Paciente(int cc, int preferenciaRacion, String recetaMedica) {
        this.cc = cc;
        this.preferenciaRacion = preferenciaRacion;
        this.recetaMedica = recetaMedica;
        this.intentosRestantes = 3;
    }

    public int getCc() {
        return cc;
    }

    public int getPreferenciaRacion() {
        return preferenciaRacion;
    }

    public String getRecetaMedica() {
        return recetaMedica;
    }

    public int getIntentosRestantes() {
        return intentosRestantes;
    }

    public void restarIntento() {
        intentosRestantes--;
    }

    public boolean estaMuerto() {
        return intentosRestantes <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paciente)) return false;
        Paciente paciente = (Paciente) o;
        return cc == paciente.cc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cc);
    }

    @Override
    public String toString() {
        return "Paciente[cc=" + cc + ", racion=" + preferenciaRacion
                + ", receta=" + recetaMedica + ", intentos=" + intentosRestantes + "]";
    }
}
