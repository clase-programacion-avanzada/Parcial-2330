package com.company.model;

public class Llamada {

    private Fecha fechaLlamada;

    private Cliente cliente;
    private int gravedad;

    private String resumen;

    //Punto 2.2:
    //Un constructor por parámetros que reciba el resumen de la llamada,
    // 3 enteros para la fecha (día, mes, y año),
    // la gravedad (entero entre 0 y 10. 0 es la menor gravedad y 10 es la mayor gravedad),
    // y el cliente que realizó la llamada (recibe el objcto tipo Cliente).
    // Este método debe arrojar una excepción si la gravedad no está en el rango [0-10] o si la fecha es inválida
    public Llamada(String resumen, int dia, int mes, int anho, int gravedad, Cliente cliente) {
        if (gravedad < 0 || gravedad > 10) {
            throw new IllegalArgumentException("Gravedad no valida");
        }
        // La fecha no debe ser validada, ya que el constructor de Fecha ya lo hace
        this.fechaLlamada = new Fecha(dia, mes, anho);
        this.resumen = resumen;
        this.gravedad = gravedad;
        this.cliente = cliente;
    }



    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public int  getGravedad() {
        return gravedad;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no puede ser nulo");
        }
        this.cliente = cliente;
    }


    public Fecha getFechaLlamada() {
        return fechaLlamada;
    }

    @Override
    public String toString() {
        return "Llamada{" +
            "fechaLlamada=" + fechaLlamada +
            ", cliente=" + cliente +
            ", gravedad=" + gravedad +
            ", resumen='" + resumen + '\'' +
            '}';
    }
}
