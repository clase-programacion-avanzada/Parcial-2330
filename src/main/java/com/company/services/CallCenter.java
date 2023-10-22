package com.company.services;

import com.company.model.Cliente;
import com.company.model.Fecha;
import com.company.model.Llamada;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CallCenter {

    public static final int GRAVEDAD_MAXIMA = 7;
    public static final int MAXIMA_CANTIDAD_DE_DIAS = 90;
    List<Llamada> llamadas;
    List<Cliente> clientes;

    public CallCenter() {
        this.clientes = new ArrayList<>();
        this.llamadas = new ArrayList<>();

    }

    public List<Llamada> getLlamadas() {
        return new ArrayList<>(llamadas);
    }

    public float promedioDeGravedadDeLlamadas() {
        float promedio = 0;
        for (Llamada llamada : llamadas) {
            promedio += llamada.getGravedad();
        }
        return promedio / llamadas.size();
    }

    public float promedioDeGravedadDeLlamadasUsandoStreams() {
        float sumaDeGravedades = llamadas.stream()
            .reduce(0, (subtotal, llamada) -> subtotal + llamada.getGravedad(), Integer::sum);
        return sumaDeGravedades / llamadas.size();
    }

    public List<Llamada> llamadasDeCliente(String nombre) {


        List<Llamada> llamadasDeCliente = new ArrayList<>();

        for (Llamada llamada : llamadas) {
            if (estaClienteEnLlamada(llamada, nombre)) {
                llamadasDeCliente.add(llamada);
            }
        }

        return llamadasDeCliente;

    }

    public List<Llamada> llamadasDeClienteUsandoStreams(String nombre) {

        return llamadas.stream()
            .filter(llamada -> estaClienteEnLlamada(llamada, nombre))
            .toList();
    }

    private boolean estaClienteEnLlamada(Llamada llamada,String nombre) {
        return llamada.getCliente().getNombre().equals(nombre);
    }

    private Cliente buscarCliente(String nombre) {
        for (Cliente cliente : clientes) {
            if (cliente.getNombre().equals(nombre)) {
                return cliente;
            }
        }
        throw new RuntimeException("Cliente no encontrado");
    }

    private Optional<Cliente> buscarClienteUsandoStreams(String nombre) {
        return clientes.stream()
            .filter(cliente -> cliente.getNombre().equals(nombre))
            .findFirst();
    }

    public void crearArchivoDeTextoConClientesEnRiesgoDeFuga(String ruta) throws IOException {

        List<String> lineas = new ArrayList<>();

        List<Cliente> clientesEnRiesgoDeFuga = clientesEnRiesgoDeFuga();

        for (Cliente cliente : clientesEnRiesgoDeFuga) {
            List<Llamada> llamadasDelCliente = llamadasDeCliente(cliente.getNombre());
            String linea = getLineaArchivoDeTexto(cliente, llamadasDelCliente);
            lineas.add(linea);
        }

        Files.write(Path.of(ruta), lineas);

    }

    public void crearArchivoDeTextoConClientesEnRiesgoDeFugaUsandoMaps(String ruta) throws IOException {

        Map<Cliente,List<Llamada>> llamadasPorCliente = llamadasPorCliente();

        Map<Cliente, Float> promedioGravedadesPorCliente = promedioGravedadesPorCliente(llamadasPorCliente);

        List<String> lineasArchivo = promedioGravedadesPorCliente.entrySet().stream()
            .map(entry -> getLineaArchivoDeTexto(entry, llamadasPorCliente))
            .toList();

        Files.write(Path.of(ruta), lineasArchivo);

    }

    private Map<Cliente, Float> promedioGravedadesPorCliente(Map<Cliente,List<Llamada>> llamadasPorCliente) {

        return llamadasPorCliente.entrySet().stream()
            .filter(entry -> esClienteEnRiesgoDeFugaUsandoStreams(entry))
            .map(entry -> {
                float gravedadPromedioDelCliente = obtenerGravedadPromedioDeLlamadasDeCLiente(entry.getValue());
                return Map.entry(entry.getKey(), gravedadPromedioDelCliente);
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private float obtenerGravedadPromedioDeLlamadasDeCLiente(List<Llamada> llamadasDelCliente) {
        return llamadasDelCliente.stream()
            .map(llamada -> (float) llamada.getGravedad())
            .reduce(0.0f, Float::sum) / llamadasDelCliente.size();
    }

    private String getLineaArchivoDeTexto(Map.Entry<Cliente, Float> entry, Map<Cliente, List<Llamada>> llamadasPorCliente) {

        Cliente cliente = entry.getKey();
        int llamadasDelCliente = llamadasPorCliente.get(cliente).size();
        float gravedadPromedioDelCliente = entry.getValue();

        return getLineaArchivoDeTexto(cliente, llamadasDelCliente, gravedadPromedioDelCliente);

    }

    private String getLineaArchivoDeTexto(Cliente cliente, List<Llamada> llamadasDelCliente) {

        float gravedadPromedioDelCliente = obtenerGravedadPromedioDeLlamadasDeCLiente(llamadasDelCliente);

        return getLineaArchivoDeTexto(cliente, llamadasDelCliente.size(), gravedadPromedioDelCliente);

    }


    private String getLineaArchivoDeTexto(Cliente cliente, int llamadasDelCliente, float gravedadPromedioDelCliente) {
        return cliente.getNombre() + ", " + llamadasDelCliente + " llamadas, " + gravedadPromedioDelCliente + " de gravedad ";
    }



    private boolean esClienteEnRiesgoDeFugaUsandoStreams(Map.Entry<Cliente, List<Llamada>> entry) {

        return entry.getValue().stream()
            .filter(llamada -> esLlamadaEnRiesgo(llamada))
            .count() > 3;
    }

    private Map<Cliente, List<Llamada>> llamadasPorCliente() {
        return llamadas.stream()
            .collect(Collectors.groupingBy(Llamada::getCliente, Collectors.toList()));
    }



    private List<Cliente> clientesEnRiesgoDeFuga() {

        List<Cliente> clientesEnRiesgoDeFuga = new ArrayList<>();

        for (Cliente cliente : clientes) {
            if (esCLienteEnRiesgoDeFuga(cliente)) {
                clientesEnRiesgoDeFuga.add(cliente);
            }
        }

        return clientesEnRiesgoDeFuga;
    }



    private List<Cliente> clientesEnRiesgoDeFugaUsandoStreams() {
        return clientes.stream()
            .filter(this::esCLienteEnRiesgoDeFuga)
            .toList();
    }

    private boolean esCLienteEnRiesgoDeFuga(Cliente cliente) {
        //Un cliente está en riesgo de fuga si ha hecho más de 3 llamadas con gravedad mayor o igual a 7 en los últimos 90 días
        List<Llamada> llamadasDelCliente = llamadasDeCliente(cliente.getNombre());

        List<Llamada> llamadasEnRiesgo = new ArrayList<>();

        for (Llamada llamada : llamadasDelCliente) {
            if (esLlamadaEnRiesgo(llamada)) {
                llamadasEnRiesgo.add(llamada);
            }
        }

        return llamadasEnRiesgo.size() > 3;
    }

    
    public boolean esClienteEnRiesgoDeFugaUsandoStreams(Cliente cliente) {
        //Un cliente está en riesgo de fuga si ha hecho más de 3 llamadas con gravedad mayor o igual a 7 en los últimos 90 días
        List<Llamada> llamadasDelCliente = llamadasDeCliente(cliente.getNombre());
        
        return llamadasDelCliente.stream()
            .filter(llamada -> esLlamadaEnRiesgo(llamada))
            .count() > 3;
    
    }

    private static boolean esLlamadaEnRiesgo(Llamada llamada) {
        Fecha fechaActual = Fecha.obtenerFechaActual();
        return llamada.getGravedad() >= GRAVEDAD_MAXIMA && llamada.getFechaLlamada().diferencia(fechaActual) <=
            MAXIMA_CANTIDAD_DE_DIAS;
    }


    public void eliminarClienteDelSistema(String nombre) {
        Cliente cliente = buscarCliente(nombre);

        clientes.remove(cliente); //Podemos hacer esto porque si buscarCLiente falla lanza una excepcion


        List<Llamada> llamadasAEliminar = llamadasDeCliente(cliente.getNombre());

        for (Llamada llamada : llamadasAEliminar) {
            llamadas.remove(llamada);
        }

    }

    public void eliminarClienteDelSistemaUsandoStreams(String nombre) {
        Optional<Cliente> clienteOptional = buscarClienteUsandoStreams(nombre);

        clienteOptional.ifPresent(cliente -> clientes.remove(cliente));

        llamadas.stream()
            .filter(llamada -> estaClienteEnLlamada(llamada, nombre))
            .forEach(llamada -> llamadas.remove(llamada));
    }


    public void agregarCliente(String juan) {

        Cliente cliente = new Cliente(juan);
        clientes.add(cliente);
    }

    public void agregarLlamada(String nombreCliente, String resumenLlamada, int gravedad, int dia, int mes, int anho) {

        Cliente cliente = buscarCliente(nombreCliente);
        Llamada llamada = new Llamada(resumenLlamada, gravedad, dia, mes, anho, cliente);
        llamadas.add(llamada);

    }

    public Map<String, List<Llamada>> llamadasHechasPorClientes() {
        return llamadas.stream()
            .collect(Collectors.groupingBy(llamada -> llamada.getCliente().getNombre(), Collectors.toList()));
    }




}
