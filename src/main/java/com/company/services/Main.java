package com.company.services;

import com.company.model.Fecha;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        CallCenter callCenter = new CallCenter();

        callCenter.agregarCliente("Juan");
        callCenter.agregarCliente("Pedro");
        callCenter.agregarCliente("Maria");
        callCenter.agregarCliente("Jose");


        agregarLlamada(callCenter, "Juan", "Problema con el teclado");
        agregarLlamada(callCenter, "Pedro", "Problema con el mouse");
        agregarLlamada(callCenter, "Maria", "Problema con el monitor");
        agregarLlamada(callCenter, "Jose", "Problema con el CPU");
        agregarLlamada(callCenter, "Juan", "Problema con el teclado otra vez 3");
        agregarLlamada(callCenter, "Juan", "Problema con el teclado otra vez 4");
        agregarLlamada(callCenter, "Juan", "Problema con el teclado otra vez");
        agregarLlamada(callCenter, "Juan", "Problema con el teclado otra vez 2");
        agregarLlamada(callCenter, "Juan", "Juan, cambia el teclado");

        System.out.println("llamadas de todos los clientes");
        callCenter.llamadasHechasPorClientes().forEach(
            (cliente, llamadas) -> System.out.println(cliente + " : " + llamadas)
        );

        try {
            callCenter.crearArchivoDeTextoConClientesEnRiesgoDeFuga("clintesEnRiesgoDeFuga.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("llamadas de Juan");
        callCenter.llamadasDeCliente("Juan").forEach(System.out::println);

        callCenter.eliminarClienteDelSistema("Juan");

        System.out.println("Llamadas de todos los clientes despues de eliminar a Juan");
        callCenter.llamadasHechasPorClientes().forEach(
            (cliente, llamadas) -> System.out.println(cliente + " : " + llamadas)
        );

    }

    private static LocalDate obtenerFechaAnteriorANoventaDias() {
        int diaMenoraNoventaDias = new Random().nextInt(90);
        return LocalDate.now().minusDays(diaMenoraNoventaDias);
    }

    private static LocalDate obtenerFechaPosteriorANoventaDias() {
        int diaMayorANoventaDias = new Random().nextInt(90);
        return LocalDate.now().plusDays(diaMayorANoventaDias);
    }

    private static void agregarLlamada(CallCenter callCenter, String nombre, String resumen) {
        try {

            int gravedad = new Random().nextInt(5,10);

            boolean esFechaPosteriorANoventaDias = gravedad > 7;

            LocalDate fecha = esFechaPosteriorANoventaDias
                ? obtenerFechaPosteriorANoventaDias()
                : obtenerFechaAnteriorANoventaDias();

            int dia = fecha.getDayOfMonth();
            int mes = fecha.getMonthValue();
            int anho = fecha.getYear();

            callCenter.agregarLlamada(nombre, resumen, dia, mes, anho, gravedad);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
