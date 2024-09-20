package es.ujaen.eps.dae.dae2223.entidades;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Reserva realizada por un Usuario
 * @author Jesús Manzano
 */
@Entity
public class Reserva implements Serializable {
    /** Identificador de la reserva */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @NotNull
    private int id;
    /** Usuario que ha realizado la reserva */
    @ManyToOne()
    @NotNull
    private Usuario usu;

    /** Fecha de inicio de la reserva */
    @FutureOrPresent
    private LocalDate fechaInicio;
    /** Fecha fin de la reserva */
    @FutureOrPresent
    private LocalDate fechaFin;

    /** Número de habitaciones simples reservadas */
    @PositiveOrZero
    private int numHabSimple;
    /** Número de habitaciones dobles reservadas */
    @PositiveOrZero
    private int numHabDoble;
    /** Precio total de la reserva */
    @Positive
    private double precio;

    public Usuario getUsu() {
        return usu;
    }

    public void setUsu(Usuario usu) {
        this.usu = usu;
    }

    public Reserva() {}

    public Reserva(Usuario usu, LocalDate fechaInicio, LocalDate fechaFin, int numHabSimple, int numHabDoble, double precio) {
        this.usu = usu;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numHabSimple = numHabSimple;
        this.numHabDoble = numHabDoble;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getNumHabSimple() {
        return numHabSimple;
    }

    public int getNumHabDoble() {
        return numHabDoble;
    }

    public double getPrecio() {
        return precio;
    }
}
