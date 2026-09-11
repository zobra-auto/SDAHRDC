package com.example.Util;

import lombok.extern.log4j.Log4j2;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import oshi.SystemInfo;

import java.util.Locale;

@Log4j2(topic = "performance")
public class PerformanceReporter {

    private static final SystemInfo si = new SystemInfo();
    private static final long BYTES_PER_MB = 1024L * 1024L;

    public static void medirPesoObjeto(Object objeto, String nombreObjeto) {

        long pesoBytes = ClassLayout.parseInstance(objeto).instanceSize();
        long pesoTotal = GraphLayout.parseInstance(objeto).totalSize();

        log.info("Objeto '{}' | tamano superficial: {} bytes | con referencias: {} bytes ({})",
                nombreObjeto, pesoBytes, pesoTotal, formatearMegabytes(pesoTotal));

    }

    public static void imprimirFootprint(Object objeto, String nombreObjeto) {
        log.info("Memory footprint de '{}':{}{}", nombreObjeto, System.lineSeparator(),
                GraphLayout.parseInstance(objeto).toFootprint());
    }

    public static void reportarMemoriaSistema() {

        Runtime runtime = Runtime.getRuntime();

        long totalHeap = runtime.totalMemory();
        long usedHeap = totalHeap - runtime.freeMemory();
        long maxHeap = runtime.maxMemory();
        double porcentajeUso = totalHeap == 0 ? 0 : (100.0 * usedHeap) / totalHeap;

        long totalFisica = si.getHardware().getMemory().getTotal();
        long disponibleFisica = si.getHardware().getMemory().getAvailable();

        log.info("Memoria JVM | usada: {} | asignada: {} | maxima: {} | uso: {}%",
                formatearMegabytes(usedHeap), formatearMegabytes(totalHeap),
                formatearMegabytes(maxHeap), formatearDecimal(porcentajeUso));

        log.info("Memoria fisica | usada: {} | total: {} | disponible: {}",
                formatearMegabytes(totalFisica - disponibleFisica),
                formatearMegabytes(totalFisica), formatearMegabytes(disponibleFisica));

    }

    public static long capturarMemoriaDisponible() {
        return si.getHardware().getMemory().getAvailable();
    }

    public static void compararMemoriaDisponible(long bytesAntes, long bytesDespues, String etiqueta) {

        long diferencia = bytesAntes - bytesDespues;

        log.info("Proceso medido: {}", etiqueta);
        log.info("RAM disponible ANTES   : {}", formatearMegabytes(bytesAntes));
        log.info("RAM disponible DESPUES : {}", formatearMegabytes(bytesDespues));
        log.info("Impacto sobre la RAM disponible: {} {}",
                diferencia >= 0 ? "se consumieron" : "se liberaron", formatearMegabytes(Math.abs(diferencia)));

    }

    private static String formatearMegabytes(long bytes) {
        return formatearDecimal((double) bytes / BYTES_PER_MB) + " MB";
    }

    private static String formatearDecimal(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }

}
