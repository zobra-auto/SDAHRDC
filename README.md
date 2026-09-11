# Pokemon Fases 1 y 2

Fase 1: sistema de evolucion con una lista enlazada simple hecha a mano.
Fase 2: rotacion del equipo con una cola circular y un historico con limite de memoria.

# Ejecutar

- ./gradlew run
- ./gradlew test
- Los logs salen en consola y quedan en la carpeta logs

# Fase 1

- Pokemon es el nodo: estadisticas mas el puntero siguienteEvolucion
- LineaEvolutiva es la lista, con primeraFase fija para recorrerla y faseActual que avanza en cada evolucion
- Evolucionar es mover un puntero, no reconstruir nada
- Danio = max(1, ataque - defensa). Cada enemigo derrotado da 50 XP
- La lista es manual y la horda es un arreglo, sin LinkedList ni ArrayList

# Fase 2

- El equipo es una cola. Sale el del frente con poll, pelea contra 50 enemigos seguidos y vuelve al final con offer
- La caja negra es una LinkedList de reportes con capacidad fija
- Regla A: si el atacante y la especie enemiga son los mismos del ultimo registro, solo se incrementa el contador
- Regla B: si toca crear un registro nuevo y la lista esta llena, se borra el primero antes de insertar
- El atacante se identifica por el nombre de su linea y no por la fase actual, para que un bloque de 50 victorias no se parta cuando el Pokemon evoluciona a mitad del turno

# Resultados

- Caso de prueba con 250 Caterpie, K = 50 y C = 3: la caja negra final queda en (Squirtle vs Caterpie: 50), (Bulbasaur vs Caterpie: 50), (Charmander vs Caterpie: 50)
- Horda masiva con 100.000 Caterpie, K = 50 y C = 10: 2.000 turnos de campo en unos 14 ms
- La caja negra termina pesando 752 bytes porque nunca guarda mas de 10 registros
- 33 pruebas con JUnit 5

# Complejidad

- Evolucionar, poll, offer y registrar una victoria: O(1)
- La simulacion completa: O(n) sobre la horda
- La memoria de la caja negra: O(C) y no O(n)
