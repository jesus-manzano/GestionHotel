package es.ujaen.eps.dae.dae2223.entidades;

import es.ujaen.eps.dae.dae2223.utils.CodificadorPass;
import es.ujaen.eps.dae.dae2223.utils.RegEx;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Clase que representa a la Entidad Usuario
 * @author José Guerrero Gallego - jgg00085@red.ujaen.es
 */
@Entity
public class Usuario implements Serializable {

    /** DNI del usuario, sirve como identificador único del mismo. */
    @Id
    @NotBlank
    private String dni;

    /** El tipo de usuario garantiza una serie de operaciones u otras al usuario */
    @NotNull
    private TipoUsuario tipo;

    @NotBlank
    private String nombre;

    @NotBlank
    private String direccion;

    @Pattern(regexp = RegEx.TELEFONO)
    private String telefono;

    @Email
    private String email;

    @Past
    private LocalDate fechaNac;

    /** TODO: La contraseña debe estar codificada. */
    @NotBlank
    private String password;

    public Usuario(String dni, String nombre, String direccion, String telefono, String email, LocalDate fechaNac, String password) {
        this.dni = dni;
        this.tipo = TipoUsuario.USUARIO;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.fechaNac = fechaNac;

        //this.password = password;
        this.password = (password != null ? CodificadorPass.codificar(password) : null);
    }

    public Usuario() {

    }

    public String getDni() {
        return dni;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() { return password; }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    /**
     * TODO:  Hashear la clave entrante con el algoritmo elegido
     * @param clave codificada
     * @return true si la clave coincide
     */
    public boolean validarClave(String clave) {
        return CodificadorPass.igual(clave, this.password);
    }

    /**
     * TODO: El método debe ser solo accesible por un administrador
     * Método de administración (solo accesible por esta) para que un usuario tenga funciones de administración
     * (por defecto los usuarios creados en el sistema no lo son)
     * @return true si el método es accedido por un administrador y el usuario a cambiar el tipo se cambia correctamente.
     */
    public boolean promoverComoAdministrador() {
        tipo = TipoUsuario.ADMINISTRADOR;
        return true;
    }
}

