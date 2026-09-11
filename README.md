# Fase 1 | Estructuras de Datos Lineales y Analisis de Complejidad

Sistema de evolucion Pokemon modelado con una **lista enlazada simple implementada a mano**, sometido a
una simulacion masiva de 100.000 batallas por turnos para perfilar su eficiencia algoritmica y espacial.

## Como ejecutar

```bash
./gradlew run      # simulacion completa (estructura + validacion + prueba de estres)
./gradlew test     # 19 pruebas unitarias
```

Los logs quedan en consola y en la carpeta `logs/`:

| Archivo | Contenido |
|---|---|
| `logs/Pokemon.log` | Traza general de eventos (creacion de la linea, evoluciones, progreso) |
| `logs/Pokemon.tiempos.log` | Tiempos de ejecucion medidos con `System.nanoTime()` |
| `logs/Pokemon.performance.log` | Footprint de objetos (JOL) y memoria del sistema (OSHI) |

El detalle turno a turno de cada batalla se emite en nivel `debug`. Se deja desactivado porque con
100.000 batallas genera cientos de miles de lineas; para auditar un combate basta con cambiar
`level="info"` a `level="debug"` en el logger `com.example` de `src/main/resources/log4j2.xml`.

## Estructura del proyecto

```
src/main/java/com/example/
├── Main.java                        Orquesta los tres escenarios
├── Model/
│   ├── Pokemon.java                 EL NODO: estadisticas + puntero siguienteEvolucion
│   ├── LineaEvolutiva.java          LA LISTA: cabeza real, cabeza virtual y mecanismo de evolucion
│   ├── ResultadoBatalla.java        Marcador de una batalla individual
│   └── ResultadoEntrenamiento.java  Consolidado de la simulacion masiva
├── Service/
│   └── EntrenamientoService.java    Motor de combate por turnos y Horde Training
└── Util/
    ├── Configuration.java           Mock data del enunciado y generacion de hordas
    └── PerformanceReporter.java     Instrumentacion con JOL y OSHI
```

No se utiliza `LinkedList` ni `ArrayList` de la API de Java en ningun punto del proyecto: la lista es
manual y la horda es un arreglo `Pokemon[]`.

## Parte A: la estructura de datos

`Pokemon` es el nodo. Guarda `nombre`, `puntosDeVidaMaximos`, `ataque`, `defensa`,
`experienciaRequerida` y el puntero `siguienteEvolucion`.

`LineaEvolutiva` es la lista y mantiene **dos punteros**:

- `primeraFase`: cabeza real, nunca se mueve. Permite recorrer e imprimir la linea completa aunque el
  Pokemon ya haya evolucionado.
- `faseActual`: cabeza virtual del enunciado. Avanza un nodo en cada evolucion, y con ella cambian
  automaticamente las estadisticas usadas en combate.

El metodo `intentarEvolucionar()` valida `experienciaAcumulada >= faseActual.experienciaRequerida` y,
si se cumple, mueve el puntero. Una fase final se marca con `experienciaRequerida = -1`
(constante `Pokemon.SIN_EVOLUCION`).

El servicio lo invoca dentro de un `while` y no de un `if`, para cubrir el caso en que una sola victoria
cruce el umbral de varias fases encadenadas.

## Parte B: el motor de simulacion

- El Pokemon del jugador siempre ataca primero.
- Dano = `max(1, Ataque_Atacante - Defensa_Defensor)`.
- Cada turno equivale a un ataque, siguiendo la numeracion del enunciado.
- El aliado entra a cada batalla con sus `puntosDeVidaMaximos` restaurados.
- Cada enemigo derrotado otorga 50 XP y dispara de inmediato la validacion de evolucion.

## Parte C: analisis de rendimiento

### Complejidad algoritmica

| Operacion | Complejidad | Justificacion |
|---|---|---|
| `agregarFase` | O(n) | Recorre los punteros hasta el ultimo nodo. Con n = 3 fases es despreciable. |
| `intentarEvolucionar` | O(1) | Una comparacion y una reasignacion de puntero, sin recorrer la lista. |
| `librarBatalla` | O(t) | t = turnos hasta que un contendiente llega a 0 HP; depende de HP y dano, no del tamano de la horda. |
| `iniciarEntrenamientoMasivo` | **O(n)** | Una pasada sobre la horda. El costo por batalla es constante, por lo que el tiempo crece linealmente con el numero de enemigos. |

La clave del rendimiento es que evolucionar **no** implica reconstruir ni recorrer la estructura: es un
salto de puntero en O(1). Por eso el ciclo masivo se mantiene lineal aunque el Pokemon cambie de fase
en plena ejecucion.

### Complejidad espacial

La memoria es O(n) respecto a la horda y O(1) respecto a la linea evolutiva: las 3 fases se crean una
sola vez y las evoluciones no asignan objetos nuevos.

### Mediciones obtenidas (JDK 25, Apple Silicon, 16 GB RAM)

| Metrica | Valor |
|---|---|
| Nodo `Pokemon` (tamano superficial) | 40 bytes |
| Nodo `Pokemon` + cadena de referencias | 288 bytes |
| `LineaEvolutiva` (tamano superficial) | 32 bytes |
| `LineaEvolutiva` con toda su cadena | 320 bytes |
| Horda de 100.000 enemigos | ~4,2 MB |
| 100.000 batallas (100.260 turnos) | ~12 ms |
| Costo promedio por batalla | ~120 ns |

Sobre la medicion de memoria: OSHI reporta la RAM disponible de **todo el sistema operativo**, por lo que
su valor fluctua con los demas procesos del equipo y puede incluso subir entre las dos capturas. Para
aislar el costo de la simulacion se imprime ademas la linea `Memoria JVM` antes y despues del ciclo: el
heap crece unos 4 MB porque cada batalla instancia un `ResultadoBatalla`, mientras que la lista enlazada
permanece constante en 320 bytes sin importar cuantas evoluciones ocurran.

El nodo pesa 40 bytes superficiales pero 288 bytes "con referencias" porque `GraphLayout` recorre el
puntero `siguienteEvolucion` y termina midiendo las tres fases junto con sus `String`.

Los valores exactos varian entre ejecuciones y equipos; los reportados corresponden a una corrida de
`./gradlew run`.

## Casos de prueba

La suite (`./gradlew test`) cubre el escenario exacto del enunciado:

- Charmander vence a Rattata en **3 turnos** quedando con **26 HP**.
- El dano nunca baja de 1, aunque la defensa supere al ataque.
- El HP se restaura al inicio de cada batalla.
- Con 29 victorias (1450 XP) **no** evoluciona; con 30 victorias (1500 XP) pasa a **Charmeleon**.
- Con 100 victorias (5000 XP) pasa a **Charizard**.
- Las 100.000 batallas se procesan sin derrotas y la fase final es Charizard.

## Dependencias

| Libreria | Uso |
|---|---|
| Log4j2 + Lombok (`@Log4j2`) | Logs reales separados por responsabilidad (general, tiempos, performance) |
| JOL (`jol-core`) | Memory footprint de los objetos en bytes |
| OSHI (`oshi-core`) | RAM fisica del equipo antes y despues del ciclo masivo |
| JUnit 5 | Pruebas unitarias |
