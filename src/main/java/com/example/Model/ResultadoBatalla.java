package com.example.Model;

public record ResultadoBatalla(boolean victoria, int turnos, int hpRestanteAliado, int hpRestanteEnemigo) {

    @Override
    public String toString() {
        return "ResultadoBatalla {" +
                "victoria = " + victoria +
                ", turnos = " + turnos +
                ", hpRestanteAliado = " + hpRestanteAliado +
                ", hpRestanteEnemigo = " + hpRestanteEnemigo +
                '}';
    }

}
