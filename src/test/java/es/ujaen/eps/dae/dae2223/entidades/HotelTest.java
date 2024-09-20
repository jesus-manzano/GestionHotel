package es.ujaen.eps.dae.dae2223.entidades;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author Jesús Manzano
 */
public class HotelTest {
    public HotelTest() {}

    @Test
    void validacionHotel() {
        Hotel hotel = new Hotel("", "", null, 0, 0, 0, 0);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);
        Assertions.assertThat(violations).isNotEmpty();

        Hotel hotel2 = new Hotel("Hotel Espacial", "Avenida Elon Musk, 43", new Localidad("Malaga"), 50, 60, 60, 85.0);
        violations = validator.validate(hotel2);
        Assertions.assertThat(violations).isEmpty();
    }
}
