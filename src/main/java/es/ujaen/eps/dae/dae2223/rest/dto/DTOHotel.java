package es.ujaen.eps.dae.dae2223.rest.dto;

import es.ujaen.eps.dae.dae2223.entidades.Hotel;
import es.ujaen.eps.dae.dae2223.entidades.Localidad;

public record DTOHotel(

        int id,
        String nombre,
        String direccion,
        Localidad localidad,
        int numHabSimple,
        double precioHabSimple,
        int numHabDoble,
        double precioHabDoble
) {
    public DTOHotel(Hotel h){
        this(h.getId(),
             h.getNombre(),
             h.getDireccion(),
             h.getLocalidad(),
             h.getNumHabSimple(),
             h.getPrecioHabSimple(),
             h.getNumHabDoble(),
             h.getPrecioHabDoble());
    }

    public Hotel aHotel() {
        return new Hotel(nombre, direccion, localidad, numHabSimple, precioHabSimple, numHabDoble, precioHabDoble);
    }
}
