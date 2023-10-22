package com.company.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;

public class Fecha {

    private int dia;
    private int mes;
    private int anho;

    public Fecha(int dia, int mes, int anho) {
        validateDateFormat(dia, mes, anho);
        this.dia = dia;
        this.mes = mes;
        this.anho = anho;
    }

    public static Fecha obtenerFechaActual() {
        return new Fecha(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
    }

    private void validateDateFormat(int dia, int mes, int anho) {
        if (dia < 1 || dia > 31) {
            throw new IllegalArgumentException("Dia no valido");
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mes no valido");
        }
        if (anho < 0) {
            throw new IllegalArgumentException("Anho no valido");
        }
    }

    int getDia() {
        return dia;
    }

    int getMes() {
        return mes;
    }

    int getAnho() {
        return anho;
    }

    boolean equals(Fecha fecha) {
        return this.dia == fecha.getDia() && this.mes == fecha.getMes() && this.anho == fecha.getAnho();
    }

    @Override
    public String toString() {
        return "Fecha{" +
            "dia=" + dia +
            ", mes=" + mes +
            ", anho=" + anho +
            '}';
    }

    public int diferencia(Fecha fecha) {
        //Retorna la diferncia en dias entre dos fechas
        LocalDate fecha1 = LocalDate.of(fecha.anho, fecha.mes , fecha.dia);
        LocalDate fecha2 = LocalDate.of(this.anho, this.mes , this.dia);

        //Si la fecha 1 es mayor que la fecha 2, se retorna un numero negativo
        //Si la fecha 2 es mayor que la fecha 1, se retorna un numero positivo

        return (int) Period.between(fecha1, fecha2).getDays();

    }

}
