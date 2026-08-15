package org.example.service;

import org.example.model.Campamento;
import org.example.model.Paciente;

import java.util.HashMap;
import java.util.Random;

public class SimuladorTriage {

    private static final int NUM_CAMPAMENTOS = 3;
    private static final int NUM_PACIENTES_INICIALES = 25;
    private static final char[] MEDICAMENTOS = {'A', 'B', 'C'};

    private final Random rnd;
    private final Campamento[] campamentos = new Campamento[NUM_CAMPAMENTOS];
    private final HashMap<Character, Integer>[] inventarioInicial = new HashMap[NUM_CAMPAMENTOS];

    private int sanados = 0;
    private int muertos = 0;

    public SimuladorTriage(long semilla) {
        this.rnd = new Random(semilla);
    }

    Campamento getCampamento(int indice) {
        return campamentos[indice];
    }

    void setCampamento(int indice, Campamento c) {
        campamentos[indice] = c;
    }

    int getSanados() {
        return sanados;
    }

    int getMuertos() {
        return muertos;
    }

    public void iniciar() {
        for (int i = 0; i < NUM_CAMPAMENTOS; i++) {
            campamentos[i] = new Campamento("Campamento" + (i + 1));
            campamentos[i].aprovisionar(rnd);
            inventarioInicial[i] = new HashMap<>(campamentos[i].getInventario());
        }

        for (int cc = 1; cc <= NUM_PACIENTES_INICIALES; cc++) {
            int preferencia = rnd.nextInt(2);
            String receta = generarReceta();
            campamentos[0].agregarPaciente(new Paciente(cc, preferencia, receta));
        }

        System.out.println("=== ESTADO INICIAL ===");
        for (Campamento c : campamentos) {
            System.out.println(c);
        }
        System.out.println();
    }

    private String generarReceta() {
        int longitud = 3 + rnd.nextInt(4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append(MEDICAMENTOS[rnd.nextInt(MEDICAMENTOS.length)]);
        }
        return sb.toString();
    }

    public void ejecutar() {
        boolean huboCambios = true;
        while (huboCambios && !todasLasColasVacias()) {
            huboCambios = false;
            for (int i = 0; i < NUM_CAMPAMENTOS; i++) {
                if (atender(campamentos[i], i) > 0) {
                    huboCambios = true;
                }
            }
        }
    }

    private boolean todasLasColasVacias() {
        for (Campamento c : campamentos) {
            if (!c.getPacientes().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    int atender(Campamento c, int indice) {
        int acciones = 0;
        int intentosFallidos = 0;

        while (!c.getPacientes().isEmpty() && !c.getRaciones().isEmpty()) {

            if (intentosFallidos >= c.getPacientes().size()) {
                System.out.println(c.getNombre() + " BLOQUEO DETECTADO: nadie quiere la racion "
                        + c.getRaciones().peek() + " del tope. Se trasladan todos los pacientes.");
                while (!c.getPacientes().isEmpty()) {
                    trasladar(c.getPacientes().poll(), indice);
                    acciones++;
                }
                break;
            }

            Paciente paciente = c.getPacientes().poll();

            if (!c.tieneMedicamentos(paciente.getRecetaMedica())) {
                System.out.println(c.getNombre() + ": " + paciente + " -> ESCASEZ de medicamentos, se traslada");
                trasladar(paciente, indice);
                acciones++;
                continue;
            }

            if (!c.getRaciones().peek().equals(paciente.getPreferenciaRacion())) {
                c.agregarPaciente(paciente);
                intentosFallidos++;
                System.out.println(c.getNombre() + ": " + paciente + " -> ESPERA, la racion del tope no es la suya");
                acciones++;
                continue;
            }

            c.getRaciones().pop();
            c.consumirMedicamentos(paciente.getRecetaMedica());
            sanados++;
            intentosFallidos = 0;
            System.out.println(c.getNombre() + ": " + paciente + " -> SANADO");
            acciones++;
        }

        return acciones;
    }

    private void trasladar(Paciente paciente, int indiceCampamentoActual) {
        paciente.restarIntento();
        if (paciente.estaMuerto()) {
            muertos++;
            System.out.println("  " + paciente + " -> FALLECIO");
            return;
        }
        int siguiente = (indiceCampamentoActual + 1) % NUM_CAMPAMENTOS;
        campamentos[siguiente].agregarPaciente(paciente);
    }

    public void imprimirResultados() {
        System.out.println();
        System.out.println("=== RESULTADOS FINALES ===");
        System.out.println("Pacientes sanados: " + sanados);
        System.out.println("Pacientes muertos: " + muertos);

        for (int i = 0; i < NUM_CAMPAMENTOS; i++) {
            System.out.println(campamentos[i].getNombre() + " inventario inicial: " + inventarioInicial[i]);
            System.out.println(campamentos[i].getNombre() + " inventario final:   " + campamentos[i].getInventario());
        }
    }
}
