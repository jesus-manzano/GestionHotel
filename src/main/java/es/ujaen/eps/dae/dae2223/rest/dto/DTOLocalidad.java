package es.ujaen.eps.dae.dae2223.rest.dto;

import es.ujaen.eps.dae.dae2223.entidades.Localidad;

/** DTO para recopilación de información de localidad */
public record DTOLocalidad (String localidad){
    public DTOLocalidad(Localidad l){
        this(l.getLocalidad());
    }

    public Localidad aLocalidad(){
        return new Localidad(localidad);
    }
}
