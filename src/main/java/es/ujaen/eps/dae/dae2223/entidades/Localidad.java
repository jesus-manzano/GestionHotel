package es.ujaen.eps.dae.dae2223.entidades;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * Almacena el nombre de la ciudad o pueblo (localidad)
 */
@Entity
public class Localidad implements Serializable {
    /** Almacena el nombre de la localidad */
    @Id
    @NotNull
    private String localidad;

    public Localidad(){
        this.localidad = "";
    }

    public Localidad(String localidad){
        this.localidad = localidad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {this.localidad = localidad;}
}
