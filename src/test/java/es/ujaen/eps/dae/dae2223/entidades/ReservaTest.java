package es.ujaen.eps.dae.dae2223.entidades;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

/**
 * @author Jesús Manzano
 */
public class ReservaTest {
    public ReservaTest() {}

    @Test
    void validacionReserva() {
        Reserva reserva = new Reserva(null, null, null, 0, 0, 0);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Reserva>> violations = validator.validate(reserva);
        Assertions.assertThat(violations).isNotEmpty();

        Usuario usu = new Usuario("20887601J", "Jesus", "Avenida Portillo, 23", "642876175", "user@email.com", LocalDate.of(2002,7,10), "pwd");
        Reserva reserva2 = new Reserva(usu, LocalDate.now(), LocalDate.now().plusDays(10), 50, 60, 60);
        violations = validator.validate(reserva2);
        Assertions.assertThat(violations).isEmpty();
    }
}
