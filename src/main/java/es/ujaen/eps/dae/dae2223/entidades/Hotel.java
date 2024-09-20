package es.ujaen.eps.dae.dae2223.entidades;

import es.ujaen.eps.dae.dae2223.excepciones.CouldNotRegisterReservaException;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hotel de nuestra cadena de hoteles
 * @author Jesús Manzano
 */
@Entity
public class Hotel implements Serializable {
    /** Identificador del hotel */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    //@NotNull
    private int id;

    /** Bloqueo optimista */
    @Version
    int version;

    /** Nombre del hotel */
    @NotBlank
    private String nombre;

    /** Dirección donde se encuentra el hotel */
    @NotBlank
    private String direccion;
    /** Localidad donde se encuentra el hotel */
    @ManyToOne
    @JoinColumn(name = "localidad")
    @NotNull
    private Localidad localidad;

    /** Número de habitaciones simples que tiene el hotel */
    @Positive
    private int numHabSimple;
    /** Precio de una habitación simple */
    @Positive
    private double precioHabSimple;
    /** Número de habitaciones dobles que tiene el hotel */
    @Positive
    private int numHabDoble;
    /** Precio de una habitación doble */
    @Positive
    private double precioHabDoble;

    /** Lista de reservas que tiene el hotel (se guardan todas a lo largo del tiempo) */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //@JoinColumn(name = "id")
    //@MapKey(name = "id")
    private List<Reserva> reservas;

    /** lista de reservas antiguas, es decir, aquellas que ya han terminado */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Reserva> reservasAntiguas;

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public Hotel() {}

//    public Hotel(String id, String nombre, String direccion, Localidad localidad, int numHabSimple, double precioHabSimple, int numHabDoble, double precioHabDoble) {
//        this.id = 0;
//        this.nombre = nombre;
//        this.direccion = direccion;
//        this.localidad = localidad;
//        this.numHabSimple = numHabSimple;
//        this.precioHabSimple = precioHabSimple;
//        this.numHabDoble = numHabDoble;
//        this.precioHabDoble = precioHabDoble;
//        reservas = new ArrayList<>();
//        reservasAntiguas = new ArrayList<>();
//    }

    public Hotel(String nombre, String direccion, Localidad localidad, int numHabSimple, double precioHabSimple, int numHabDoble, double precioHabDoble) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.localidad = localidad;
        this.numHabSimple = numHabSimple;
        this.precioHabSimple = precioHabSimple;
        this.numHabDoble = numHabDoble;
        this.precioHabDoble = precioHabDoble;
        reservas = new ArrayList<>();
        reservasAntiguas = new ArrayList<>();
    }

    /**
     * Crea la reserva con los valores indicados y la registra en el hotel
     * @param usu el usuario válido para registrar la reserva y que esté asociado a este
     * @param fechaInicio Fecha, inclusive, cuando comenzaría la estancia
     * @param fechaFin Fecha, inclusive, cuando finalizaría la estancia
     * @param habSimple Número de habitaciones para una persona a reservar
     * @param habDoble Número de habitaciones para dos personas a reservar
     * @return Devuelve true si se ha realizado correctamente y false en caso contrario
     */
    public void reserva(Usuario usu, LocalDate fechaInicio, LocalDate fechaFin, int habSimple, int habDoble) throws CouldNotRegisterReservaException {
        int numDias = Period.between(fechaInicio, fechaFin).getDays();
        if (consulta(fechaInicio, fechaFin, habSimple, habDoble))
            reservas.add(new Reserva(usu, fechaInicio, fechaFin, habSimple, habDoble,
                        (numHabSimple * precioHabSimple + numHabDoble * precioHabDoble) * numDias));
        else
            throw new CouldNotRegisterReservaException();
    }

    /**
     * Busca entre todas las reservas aquellas que están dentro de estas restricciones: fecha de inicio, fecha de fin,
     * número de habitaciones simple y dobles
     * @param fechaInicio Fecha, inclusive, cuando comenzaría la estancia
     * @param fechaFin Fecha, inclusive, cuando finalizaría la estancia
     * @param habSimple Número de habitaciones para una persona a reservar
     * @param habDoble Número de habitaciones para dos personas a reservar
     * @return Devuelve verdadero si encuentra reservas que cumplen lo comentado anteriormente y false en caso contrario
     */
    public boolean consulta(LocalDate fechaInicio, LocalDate fechaFin, int habSimple, int habDoble){
        ArrayList<Reserva> reservasEnRango = obtenerReservasEntre(fechaInicio, fechaFin);
        LocalDate diaReco = fechaInicio;
        while (diaReco.isBefore(fechaFin) || diaReco.isEqual(fechaFin)) {
            ArrayList<Integer> numHab = habReservadas(reservasEnRango, diaReco);
            if((numHabSimple - numHab.get(0)) < habSimple || (numHabDoble - numHab.get(1)) < habDoble) return false;
            diaReco = diaReco.plusDays(1);
        }
        return true;
    }

    /**
     * Busca aquellas reservas que ya han terminado y las borra de las reservas activas y las pasa a reservas antiguas
     */
    public boolean actualizarReservasAntiguas(){
        int oldRA = this.reservasAntiguas.size();
        ArrayList<Reserva> aux = new ArrayList<>();
        reservas.forEach((r) -> {
            if(r.getFechaFin().isBefore(LocalDate.now())) {
                aux.add(r);
            }
        });
        aux.forEach((r) -> {
            reservas.remove(r);
            reservasAntiguas.add(r);
        });
        //System.out.println("Rservas trasladadas en este hotel: " + (this.reservasAntiguas.size() - oldRA));
        Logger.getLogger(Hotel.class.getName()).log(Level.INFO, "Rservas trasladadas en este hotel: " + (this.reservasAntiguas.size() - oldRA));
        return true;
    }

    /**
     * Dado un día y un conjunto de reservas concreto, devuelve todas las habitaciones que hay reservadas
     * @param conjReservas el conjunto de reservas a analizar
     * @param dia el día concreto
     * @return Devuelve un vector donde en la posición 0 indica el número de hab. simples y la posición 1 el número de hab. dobles
     */
    private ArrayList<Integer> habReservadas(ArrayList<Reserva> conjReservas, LocalDate dia){
        ArrayList<Integer> resultado = new ArrayList<>();
        int habSimplesOcup = 0, habDoblesOcup = 0;
        for (Reserva r: conjReservas){
            if((r.getFechaInicio().isBefore(dia) || r.getFechaInicio().isEqual(dia)) &&
                    (r.getFechaFin().isAfter(dia) || r.getFechaFin().isEqual(dia))) {
                habSimplesOcup += r.getNumHabSimple();
                habDoblesOcup += r.getNumHabDoble();
            }
        }
        resultado.add(habSimplesOcup); // En la posición 0 del vector guardo el número de habitaciones simples reservadas
        resultado.add(habDoblesOcup); // En la posición 1 del vector guardo el número de habitaciones dobles reservadas
        return resultado;
    }

    /**
     * Recorre todas las reservas para buscar aquellas que están dentro de las fechas establecidas, aunque sea parcialmente
     * @param fechaInicio Fecha, inclusive, cuando comenzaría la estancia
     * @param fechaFin Fecha, inclusive, cuando finalizaría la estancia
     * @return Devuelve todas las reservas que cumplen lo anteriormente comentado
     */
    private ArrayList<Reserva> obtenerReservasEntre(LocalDate fechaInicio, LocalDate fechaFin){
        ArrayList<Reserva> resultados = new ArrayList<>();
        for (Reserva r: reservas) {
            if((r.getFechaInicio().isBefore(fechaFin) || r.getFechaInicio().isEqual(fechaFin)) &&
               (r.getFechaFin().isAfter(fechaInicio) || r.getFechaFin().isEqual(fechaInicio)))
                resultados.add(r);
        }
        return resultados;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public int getNumHabSimple() {
        return numHabSimple;
    }

    public double getPrecioHabSimple() {
        return precioHabSimple;
    }

    public int getNumHabDoble() {
        return numHabDoble;
    }

    public double getPrecioHabDoble() {
        return precioHabDoble;
    }
}
