package es.ujaen.eps.dae.dae2223.rest.dto;

import es.ujaen.eps.dae.dae2223.entidades.TipoUsuario;
import es.ujaen.eps.dae.dae2223.entidades.Usuario;

import java.time.LocalDate;

public record DTOUsuario(
        String dni,
        String nombre,
        String direccion,
        String telefono,
        String email,
        LocalDate fechaNac,
        String password
) {
    public DTOUsuario(Usuario usu) {
        this(usu.getDni(),
             usu.getNombre(),
             usu.getDireccion(),
             usu.getTelefono(),
             usu.getEmail(),
             usu.getFechaNac(),
             "");
    }

    public Usuario aUsuario() {
        return new Usuario(dni, nombre, direccion, telefono, email, fechaNac, password);
    }
}
