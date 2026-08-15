package org.example.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Campamento {

    private static final char[] MEDICAMENTOS = {'A', 'B', 'C'};

    private final String nombre;
    private final Queue<Paciente> pacientes = new LinkedList<>();
    private final Stack<Integer> raciones = new Stack<>();
    private final HashMap<Character, Integer> inventario = new HashMap<>();

    public Campamento(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public Queue<Paciente> getPacientes() {
        return pacientes;
    }

    public Stack<Integer> getRaciones() {
        return raciones;
    }

    public HashMap<Character, Integer> getInventario() {
        return inventario;
    }

    public void agregarPaciente(Paciente p) {
        pacientes.add(p);
    }

    public void aprovisionar(Random rnd) {
        int raciones = 5 + rnd.nextInt(6);
        for (int i = 0; i < raciones; i++) {
            this.raciones.push(rnd.nextInt(2));
        }

        int dosis = 10 + rnd.nextInt(11);
        for (int i = 0; i < dosis; i++) {
            char letra = MEDICAMENTOS[rnd.nextInt(MEDICAMENTOS.length)];
            inventario.merge(letra, 1, Integer::sum);
        }
    }

    public static HashMap<Character, Integer> contar(String receta) {
        HashMap<Character, Integer> conteo = new HashMap<>();
        for (char letra : receta.toCharArray()) {
            conteo.merge(letra, 1, Integer::sum);
        }
        return conteo;
    }

    public boolean tieneMedicamentos(String receta) {
        for (Map.Entry<Character, Integer> entrada : contar(receta).entrySet()) {
            int disponible = inventario.getOrDefault(entrada.getKey(), 0);
            if (disponible < entrada.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void consumirMedicamentos(String receta) {
        for (Map.Entry<Character, Integer> entrada : contar(receta).entrySet()) {
            inventario.merge(entrada.getKey(), -entrada.getValue(), Integer::sum);
        }
    }

    @Override
    public String toString() {
        return nombre + "[pacientes=" + pacientes.size() + ", raciones=" + raciones
                + ", inventario=" + inventario + "]";
    }
}
