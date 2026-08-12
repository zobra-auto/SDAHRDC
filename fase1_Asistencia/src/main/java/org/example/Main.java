package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        try {
            Stack<Integer> alimentos = new Stack<>();

            alimentos.push(0);
            alimentos.push(1);
            alimentos.push(0);
            alimentos.push(1);
            alimentos.push(0);

            Queue<Integer> refugiados = new LinkedList<>();

            refugiados.add(0);
            refugiados.add(0);
            refugiados.add(1);
            refugiados.add(1);
            refugiados.add(1);

            int intentosFallidos = 0;

            while (refugiados.peek() != null && !alimentos.isEmpty() ) {

                if (intentosFallidos >= refugiados.size()) {

                    System.out.println("BLOQUEO DETECTADO: ningun refugiado quiere la comida siguiente"
                            + alimentos.peek() + " Se corta el proceso.");
                    break;

                }

                int persona = refugiados.remove();
                int comida = alimentos.pop();


                if (persona == comida) {

                    System.out.println("El refugiado es: " + persona + " y la comida es: " + comida + " los dos salen");


                    intentosFallidos = 0;

                }
                else {

                    refugiados.add(persona);
                    alimentos.push(comida);

                    System.out.println("El refugiado es: " + persona + " y la comida es: " + comida + " los dos esperan");

                    intentosFallidos++;

                }





            }


        } catch (Exception e) {

            System.out.println("el error es: " + e);
        }
    }
}
