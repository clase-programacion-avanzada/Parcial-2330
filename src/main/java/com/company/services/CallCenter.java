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

    //region Solución Clase CallCenter()
    //Punto 2.2:
    // Un método que retorne el promedio de gravedad de las llamadas registradas en el sistema
    public float promedioDeGravedadDeLlamadas() {
        float promedio = 0;
        for (Llamada llamada : llamadas) {
            promedio += llamada.getGravedad();
        }
        return promedio / llamadas.size();
    }



    //Punto 2.2:
    // Un método que retorne una lista con las llamadas de un cliente dado su nombre.
    // En caso de que el cliente no tenga llamadas registradas, debe retornar una lista vacía.
    public List<Llamada> llamadasDeCliente(String nombre) {

        List<Llamada> llamadasDeCliente = new ArrayList<>();

        for (Llamada llamada : llamadas) {
            if (llamada.getCliente().getNombre().equals(nombre)) {
                llamadasDeCliente.add(llamada);
            }
        }

        return llamadasDeCliente;

    }

    //Punto 2.2:
    //Un método que cree un archivo de texto (el método recibe como parámetro un String con la ubicación del archivo) con los clientes en riesgo de fuga.
    // Un cliente está en riesgo de fuga si
    // -ha hecho más de 3 llamadas con gravedad mayor o igual a 7 en los últimos 90 días.
    // Cada línea del archivo debe tener el nombre del cliente, la cantidad de llamadas, y la gravedad promedio de las llamadas hechas por el cliente.
    // Cada línea del archivo debe tener el siguiente formato: Pedro Pérez, 4 llamadas, 8.9 de gravedad
    // Es necesario agregar a la firma del método la excepción IOException, ya que se está escribiendo en un archivo.
    public void crearArchivoDeTextoConClientesEnRiesgoDeFuga(String ruta) throws IOException {

        List<String> lineas = new ArrayList<>();
        //1. Obtener los clientes en riesgo de fuga
        List<Cliente> clientesEnRiesgoDeFuga = clientesEnRiesgoDeFuga();

        //2. Iterar sobre los clientes en riesgo de fuga
        for (Cliente cliente : clientesEnRiesgoDeFuga) {
            //2.1 Obtener las llamadas del cliente
            List<Llamada> llamadasDelCliente = llamadasDeCliente(cliente.getNombre());
            //2.2 Crear la línea del archivo de texto
            String linea = obtenerLineaArchivoDeTexto(cliente, llamadasDelCliente);
            //2.3 Agregar la línea al archivo de texto
            lineas.add(linea);
        }
        //3. Escribir las líneas en el archivo de texto
        Files.write(Path.of(ruta), lineas);

    }

    private String obtenerLineaArchivoDeTexto(Cliente cliente, List<Llamada> llamadasDelCliente) {
        //La gravedad promedio se calcula usando la función del punto anterior
        float gravedadPromedioDelCliente = obtenerGravedadPromedioDeLlamadasDeCliente(llamadasDelCliente);

        return obtenerLineaArchivoDeTexto(cliente, llamadasDelCliente.size(), gravedadPromedioDelCliente);

    }

    private float obtenerGravedadPromedioDeLlamadasDeCliente(List<Llamada> llamadasDelCliente) {
        float sumaDeGravedades = 0;
        for (Llamada llamada : llamadasDelCliente) {
            sumaDeGravedades += llamada.getGravedad();
        }
        return sumaDeGravedades / llamadasDelCliente.size();
    }


    private String obtenerLineaArchivoDeTexto(Cliente cliente, int llamadasDelCliente, float gravedadPromedioDelCliente) {
        return cliente.getNombre() + ", " + llamadasDelCliente + " llamadas, " + gravedadPromedioDelCliente + " de gravedad ";
    }

    /*
    * Función que retorna una lista con los clientes en riesgo de fuga
     */
    private List<Cliente> clientesEnRiesgoDeFuga() {

        List<Cliente> clientesEnRiesgoDeFuga = new ArrayList<>();
        //1. Iterar sobre los clientes
        for (Cliente cliente : clientes) {
            //1.1 Verificar si el cliente está en riesgo de fuga
            if (esClienteEnRiesgoDeFuga(cliente)) {
                //1.2 Agregar el cliente a la lista de clientes en riesgo de fuga
                clientesEnRiesgoDeFuga.add(cliente);
            }
        }
        //2. Retornar la lista de clientes en riesgo de fuga
        return clientesEnRiesgoDeFuga;
    }

    private boolean esClienteEnRiesgoDeFuga(Cliente cliente) {
        //Un cliente está en riesgo de fuga si ha hecho más de 3 llamadas con gravedad mayor o igual a 7 en los últimos 90 días
        //1. Obtener las llamadas del cliente
        List<Llamada> llamadasDelCliente = llamadasDeCliente(cliente.getNombre());

        List<Llamada> llamadasEnRiesgo = new ArrayList<>();

        //2. Iterar sobre las llamadas del cliente y obtener las llamadas en riesgo
        for (Llamada llamada : llamadasDelCliente) {
            //2.1 Verificar si la llamada está en riesgo
            if (esLlamadaEnRiesgo(llamada)) {
                //2.2 Agregar la llamada a la lista de llamadas en riesgo
                llamadasEnRiesgo.add(llamada);
            }
        }
        //3. Verificar si el cliente está en riesgo
        return llamadasEnRiesgo.size() > 3;
    }

    private static boolean esLlamadaEnRiesgo(Llamada llamada) {
        //El método obtenerFechaActual() retorna la fecha actual y no lo piden en el exámen,
        // solo deben asumir que ya existe.
        Fecha fechaActual = Fecha.obtenerFechaActual();

        // Una llamada está en riesgo si su gravedad es mayor o igual a 7
        // y si la diferencia entre la fecha de la llamada y la fecha actual es menor o igual a 90 días
        return llamada.getGravedad() >= GRAVEDAD_MAXIMA && llamada.getFechaLlamada().diferencia(fechaActual) <=
            MAXIMA_CANTIDAD_DE_DIAS;
    }

    //Punto 2.2
    //Un método que elimine a un cliente del sistema.
    // Se debe eliminar el registro del cliente y todas las llamadas hechas por ese cliente
    public void eliminarClienteDelSistema(String nombre) {

        Cliente cliente = buscarCliente(nombre);

        // Esto no lo pide el enunciado, pero es una buena práctica
        if(cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        clientes.remove(cliente); //Podemos hacer esto porque si buscarCliente falla lanza una excepcion

        List<Llamada> llamadasAEliminar = llamadasDeCliente(cliente.getNombre());

        for (Llamada llamada : llamadasAEliminar) {
            llamadas.remove(llamada);
        }

    }

    private Cliente buscarCliente(String nombre) {
        for (Cliente cliente : clientes) {
            if (cliente.getNombre().equals(nombre)) {
                return cliente;
            }
        }
        return null;
    }


    //endregion

    //region Solución Clase CallCenter() usando streams()


    public float promedioDeGravedadDeLlamadasUsandoStreams() {
        float sumaDeGravedades = llamadas.stream()
            //https://www.baeldung.com/java-stream-reduce
            .reduce(0, (subtotal, llamada) -> subtotal + llamada.getGravedad(), Integer::sum);
        return sumaDeGravedades / llamadas.size();
    }

    public List<Llamada> llamadasDeClienteUsandoStreams(String nombre) {
        return llamadas.stream()
            .filter(llamada -> llamada.getCliente().getNombre().equals(nombre))
            .toList();
    }

    public void crearArchivoDeTextoConClientesEnRiesgoDeFugaUsandoMaps(String ruta) throws IOException {

        Map<Cliente,List<Llamada>> llamadasPorCliente = llamadasPorCliente();

        Map<Cliente, Float> promedioGravedadesDeClientesEnRiesgo = promedioGravedadesDeClientesEnRiesgo(llamadasPorCliente);

        List<String> lineasArchivo = promedioGravedadesDeClientesEnRiesgo.entrySet().stream()
            .map(entry -> obtenerLineaArchivoDeTexto(entry, llamadasPorCliente))
            .toList();

        Files.write(Path.of(ruta), lineasArchivo);

    }

    /*
    * Este método retorna un Map con los clientes en riesgo de fuga y el promedio de gravedad de sus llamadas
    * |Key (Cliente)|Value (Promedio de gravedad)|
    * |-------------|-----------------------------|
    * |Cliente1     |7.5                   |
    * |Cliente2     |3.0                   |
    * */
    private Map<Cliente, Float> promedioGravedadesDeClientesEnRiesgo(Map<Cliente,List<Llamada>> llamadasPorCliente) {

        return llamadasPorCliente.entrySet().stream()
            .filter(entry -> esClienteEnRiesgoDeFugaUsandoStreams(entry))
            .map(entry -> {
                float gravedadPromedioDelCliente = obtenerGravedadPromedioDeLlamadasDeClienteUsandoStreams(entry.getValue());
                return Map.entry(entry.getKey(), gravedadPromedioDelCliente);
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private boolean esClienteEnRiesgoDeFugaUsandoStreams(Map.Entry<Cliente, List<Llamada>> entry) {

        return entry.getValue().stream()
            .filter(llamada -> esLlamadaEnRiesgo(llamada))
            .count() > 3;
    }

    private float obtenerGravedadPromedioDeLlamadasDeClienteUsandoStreams(List<Llamada> llamadasDelCliente) {
        return llamadasDelCliente.stream()
            .map(llamada -> (float) llamada.getGravedad())
            .reduce(0.0f, Float::sum) / llamadasDelCliente.size();
    }

    private String obtenerLineaArchivoDeTexto(Map.Entry<Cliente, Float> entry, Map<Cliente, List<Llamada>> llamadasPorCliente) {

        Cliente cliente = entry.getKey();
        int llamadasDelCliente = llamadasPorCliente.get(cliente).size();
        float gravedadPromedioDelCliente = entry.getValue();

        return obtenerLineaArchivoDeTexto(cliente, llamadasDelCliente, gravedadPromedioDelCliente);

    }

    private Map<Cliente, List<Llamada>> llamadasPorCliente() {
        return llamadas.stream()
            .collect(Collectors.groupingBy(Llamada::getCliente, Collectors.toList()));
    }

    private List<Cliente> clientesEnRiesgoDeFugaUsandoStreams() {
        return clientes.stream()
            .filter(this::esClienteEnRiesgoDeFuga)
            .toList();
    }


    public void eliminarClienteDelSistemaUsandoStreams(String nombre) {
        Optional<Cliente> clienteOptional = buscarClienteUsandoStreams(nombre);

        clienteOptional.ifPresent(cliente -> {
                clientes.remove(cliente);
                llamadas.stream()
                    .filter(llamada -> estaClienteEnLlamada(llamada, nombre))
                    .forEach(llamada -> llamadas.remove(llamada));
            });
    }

    private boolean estaClienteEnLlamada(Llamada llamada,String nombre) {
        return llamada.getCliente().getNombre().equals(nombre);
    }

    //endregion

    //region no necesario para el examen
    public void agregarCliente(String juan) {

        Cliente cliente = new Cliente(juan);
        clientes.add(cliente);
    }

    public void agregarLlamada(String nombreCliente, String resumenLlamada, int gravedad, int dia, int mes, int anho) {

        Cliente cliente = buscarCliente(nombreCliente);
        Llamada llamada = new Llamada(resumenLlamada, gravedad, dia, mes, anho, cliente);
        llamadas.add(llamada);

    }


    private Optional<Cliente> buscarClienteUsandoStreams(String nombre) {
        return clientes.stream()
            .filter(cliente -> cliente.getNombre().equals(nombre))
            .findFirst();
    }

    public Map<String, List<Llamada>> llamadasHechasPorClientes() {
        return llamadas.stream()
            .collect(Collectors.groupingBy(llamada -> llamada.getCliente().getNombre(), Collectors.toList()));
    }

    //endregion
}
