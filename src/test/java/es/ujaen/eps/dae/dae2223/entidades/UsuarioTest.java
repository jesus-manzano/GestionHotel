package es.ujaen.eps.dae.dae2223.entidades;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

/**
 * @author José Guerrero Gallego - jgg00085@red.ujaen.es
 */
public class UsuarioTest {
    public UsuarioTest() {}

    @Test
    void validacionUsuario() {
        Usuario usuario = new Usuario("","","","","", LocalDate.now(), "");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        Assertions.assertThat(violations).isNotEmpty();
    }

    @Test
    void validacionClaveUsuario() {
        String claveMala = "suspenderéDAE";
        String claveBuena = "aprobaréDAE";
        Usuario usuario = new Usuario("77388840G","José Guerrero","Fuente Alamillo 3","620975843","jgg00085@red.ujaen.es", LocalDate.of(1998,9,20), claveBuena);

        Assertions.assertThat(usuario.validarClave(claveMala)).isFalse();
        Assertions.assertThat(usuario.validarClave(claveBuena)).isTrue();
    }

    @Test
    void validarTipoUsuario() {
        Usuario usuario = new Usuario("77388840G","José Guerrero","Fuente Alamillo 3","620975843","jgg00085@red.ujaen.es", LocalDate.of(1998,9,20), "clave");

        Assertions.assertThat(usuario.getTipo()).isNotNull();
        Assertions.assertThat(usuario.getTipo()).isEqualTo(TipoUsuario.USUARIO);
        Assertions.assertThat(usuario.getTipo()).isNotEqualTo(TipoUsuario.ADMINISTRADOR);
    }

    @Test
    void promoverUsuarioComoAdmin() {
        Usuario usuario = new Usuario("77388840G","José Guerrero","Fuente Alamillo 3","620975843","jgg00085@red.ujaen.es", LocalDate.of(1998,9,20), "clave");

        Assertions.assertThat(usuario.getTipo()).isNotNull();
        Assertions.assertThat(usuario.getTipo()).isEqualTo(TipoUsuario.USUARIO);
        Assertions.assertThat(usuario.getTipo()).isNotEqualTo(TipoUsuario.ADMINISTRADOR);

        usuario.promoverComoAdministrador();

        Assertions.assertThat(usuario.getTipo()).isNotNull();
        Assertions.assertThat(usuario.getTipo()).isEqualTo(TipoUsuario.ADMINISTRADOR);
        Assertions.assertThat(usuario.getTipo()).isNotEqualTo(TipoUsuario.USUARIO);
    }
}
