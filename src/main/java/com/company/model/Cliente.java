package com.company.model;

public class Cliente {

    private String nombre;

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede ser nulo o vacio");
        }
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Cliente{" +
            "nombre='" + nombre + '\'' +
            '}';
    }
}
