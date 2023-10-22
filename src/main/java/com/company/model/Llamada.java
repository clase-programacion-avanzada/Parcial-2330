package com.company.model;

public class Llamada {

    private Fecha fechaLlamada;

    private Cliente cliente;
    private int gravedad;

    private String resumen;
    public Llamada() {

    }

    //Punto 1:
    public Llamada(String resumen, int dia, int mes, int anho, int gravedad, Cliente cliente) {
        validarGravedad(gravedad);
        this.fechaLlamada = new Fecha(dia, mes, anho);
        this.resumen = resumen;
        this.gravedad = gravedad;
        this.cliente = cliente;
    }

    private void validarGravedad(int gravedad) {
        if (gravedad < 0 || gravedad > 10) {
            throw new IllegalArgumentException("Gravedad no valida");
        }
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
