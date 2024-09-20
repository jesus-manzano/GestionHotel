package es.ujaen.eps.dae.dae2223.servicios;

import es.ujaen.eps.dae.dae2223.entidades.Hotel;
import es.ujaen.eps.dae.dae2223.entidades.Localidad;
import es.ujaen.eps.dae.dae2223.entidades.Usuario;
import es.ujaen.eps.dae.dae2223.excepciones.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author José Guerrero Gallego - jgg00085@red.ujaen.es
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = es.ujaen.eps.dae.dae2223.app.Dae2223Application.class)
@ActiveProfiles(profiles = {"test"})
public class CadenaHotelesTest {
    Usuario usuario = new Usuario("77388841Z","Antonio Sánchez","Fuente Peral 5","667234021","ansan122@red.ujaen.es", LocalDate.of(1996,7,13), "aprobaréDAE");
    Usuario admin = new Usuario("77388844Z","Adminator Gutiérrez","Fuente Peral 15","631765320","adminator@red.ujaen.es", LocalDate.of(1998,9,23), "clave");

    public CadenaHotelesTest() {}

    @Autowired
    CadenaHoteles cadenaHoteles;

    @Test
    void testFuncionamientoServicio() {
        Assertions.assertThat(cadenaHoteles).isNotNull();
    }

    @Test
    void testValidacionAltaUsuario() {
        //Usuario usuario = new Usuario("77388841Z","Antonio Sánchez","Fuente Peral 5","667234021","ansan122@red.ujaen.es", LocalDate.of(1996,7,13), "clave");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        Assertions.assertThat(violations).isEmpty();
        Assertions.assertThatCode(() -> {
            cadenaHoteles.altaCliente(usuario);
        }).doesNotThrowAnyException();
        // debe lanzar excepción
        Assertions.assertThatThrownBy(() -> {
            cadenaHoteles.altaCliente(usuario);
        }).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void validacionAltaHotel() throws UserTypeNotAllowedException, PlaceAlreadyExistsException {
        //Usuario admin = new Usuario("77388842Z","Antonio Sánchez","Fuente Peral 5","667234021","ansan122@red.ujaen.es", LocalDate.of(1996,7,13), "clave");
        admin.promoverComoAdministrador();
        Assertions.assertThatCode(() -> cadenaHoteles.altaCliente(admin)).doesNotThrowAnyException();

        Localidad l = new Localidad("Huelva");
        cadenaHoteles.altaLocalidad(l);

        Hotel h1 = new Hotel("Hotelazo Buenérrimo", "Calle El 5 Estrellas, 24", l, 50, 60, 60, 85.0);

        //Usuario usuario = new Usuario("77388843Z","Antonio Sánchez","Fuente Peral 5","667234021","ansan122@red.ujaen.es", LocalDate.of(1996,7,13), "clave");
        Assertions.assertThatCode(() -> cadenaHoteles.altaHotel(h1)).doesNotThrowAnyException();
    }

    @Test
    void validarClaveUsuario() {
        String DNImalo = "74823456A";
        String DNI = usuario.getDni();
        String claveMala = "asdasdada";
        String clave = "aprobaréDAE";

        Assertions.assertThatCode(() -> cadenaHoteles.altaCliente(usuario)).doesNotThrowAnyException();
        Assertions.assertThatCode(() -> cadenaHoteles.login(DNI, clave)).doesNotThrowAnyException();

        Assertions.assertThatThrownBy(() -> {
            cadenaHoteles.login(DNImalo, clave);
        }).isInstanceOf(InvalidCredentialsException.class);
        Assertions.assertThatThrownBy(() -> {
            cadenaHoteles.login(DNI, claveMala);
        }).isInstanceOf(InvalidCredentialsException.class);
        Assertions.assertThatThrownBy(() -> {
            cadenaHoteles.login(DNImalo, claveMala);
        }).isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void testConsulta() throws UserTypeNotAllowedException, PlaceAlreadyExistsException, HotelAlreadyExistsException {
        admin.promoverComoAdministrador();
        Localidad lg = new Localidad("Granada");
        Localidad lm = new Localidad("Málaga");
        cadenaHoteles.altaLocalidad(lg);
        cadenaHoteles.altaLocalidad(lm);
        Hotel h1 = new Hotel("Hotel consulta", "Calle El 5 Estrellas, 24", lg, 50, 60, 60, 85.0);
        Hotel h2 = new Hotel("Hotel consulta2", "Calle El 5 Estrellas, 24", lm, 50, 60, 60, 85.0);
        Hotel h3 = new Hotel("Hotel consulta3", "Calle El 5 Estrellas, 24", lg, 50, 60, 60, 85.0);
        cadenaHoteles.altaHotel(h1);
        cadenaHoteles.altaHotel(h2);
        cadenaHoteles.altaHotel(h3);
        AtomicReference<ArrayList<Hotel>> consulta = new AtomicReference<>();
        Assertions.assertThatCode(()-> consulta.set(cadenaHoteles.consulta(new Localidad("Granada"), LocalDate.now(), LocalDate.now().plusDays(7), 2, 3))).doesNotThrowAnyException();
        Assertions.assertThat(consulta.get()).isNotEmpty();
        Assertions.assertThat(consulta.get()).hasSize(2);
    }

    @Test
    void testReserva() throws UserTypeNotAllowedException, UserAlreadyExistsException, PlaceAlreadyExistsException, HotelAlreadyExistsException {
        cadenaHoteles.altaCliente(usuario);
        admin.promoverComoAdministrador();
        Localidad ls = new Localidad("Sevilla");
        Localidad lc = new Localidad("Córdoba");
        cadenaHoteles.altaLocalidad(ls);
        cadenaHoteles.altaLocalidad(lc);
        Hotel h1 = new Hotel("Hotel reserva", "Calle El 5 Estrellas, 24", ls, 50, 60, 60, 85.0);
        Hotel h2 = new Hotel("Hotel reserva2", "Calle El 5 Estrellas, 24", lc, 50, 60, 60, 85.0);
        Hotel h3 = new Hotel("Hotel reserva3", "Calle El 5 Estrellas, 24", ls, 50, 60, 60, 85.0);
        cadenaHoteles.altaHotel(h1);
        cadenaHoteles.altaHotel(h2);
        cadenaHoteles.altaHotel(h3);
        AtomicReference<ArrayList<Hotel>> consulta = new AtomicReference<>();
        Assertions.assertThatCode(()-> consulta.set(cadenaHoteles.consulta(new Localidad("Sevilla"), LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)) ).doesNotThrowAnyException();
        Assertions.assertThat(consulta.get()).isNotEmpty();
        Assertions.assertThat(consulta.get()).hasSize(2);
        Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).doesNotThrowAnyException();
    }

//    @Test
//    void testReservaConcurrente() throws UserTypeNotAllowedException, UserAlreadyExistsException {
//        cadenaHoteles.altaCliente(usuario);
//        admin.promoverComoAdministrador();
//        Localidad ls = new Localidad("Sevilla");
//        Localidad lc = new Localidad("Córdoba");
//        cadenaHoteles.altaLocalidad(admin, ls);
//        cadenaHoteles.altaLocalidad(admin, lc);
//        cadenaHoteles.altaHotel(admin, "Hotel reserva", "Calle El 5 Estrellas, 24", ls, 2, 3, 60, 85.0);
//        cadenaHoteles.altaHotel(admin, "Hotel reserva2", "Calle El 5 Estrellas, 24", lc, 50, 60, 60, 85.0);
//        AtomicReference<ArrayList<Hotel>> consulta = new AtomicReference<>();
//        Assertions.assertThatCode(()-> consulta.set(cadenaHoteles.consulta(ls, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)) ).doesNotThrowAnyException();
//        Assertions.assertThat(consulta.get()).isNotEmpty();
//        Assertions.assertThat(consulta.get()).hasSize(1);
//        Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).doesNotThrowAnyException();
//        //Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).doesNotThrowAnyException();
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
//        executor.execute(() -> {
//            Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).doesNotThrowAnyException();
//        });
//        executor.submit(() -> {
//            Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).doesNotThrowAnyException();
//        });
//        executor.submit(() -> {
//            Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).doesNotThrowAnyException();
//        });
//    }

    @Test
    void testReservaHotelNoExiste() throws UserTypeNotAllowedException, HotelNotExistException, EmptyConsultResultException, HotelAlreadyExistsException, PlaceAlreadyExistsException {
        Usuario usuario = new Usuario("77388850Z","José Guerrero","Fuente Alamillo 3","620975843","jgg00085@red.ujaen.es", LocalDate.of(1998,9,20), "clave");

        Usuario admin = new Usuario("77388850X","Antonio Sánchez","Fuente Peral 5","667234021","ansan122@red.ujaen.es", LocalDate.of(1996,7,13), "clave");
        admin.promoverComoAdministrador();
        Localidad l = new Localidad("Murcia");
        cadenaHoteles.altaLocalidad(l);
        Hotel h1 = new Hotel("Hotel reserva", "Calle El 5 Estrellas, 24", l, 50, 60, 60, 85.0);
        cadenaHoteles.altaHotel(h1);
        Assertions.assertThatThrownBy(() -> cadenaHoteles.consulta(new Localidad("Sevilla"), LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).isInstanceOf(EmptyConsultResultException.class);
        Assertions.assertThatThrownBy(() -> cadenaHoteles.reserva(-1, usuario, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3)).isInstanceOf(ConstraintViolationException.class);
    }


    /*@Test
    void testLimpiezaReservasAntiguas() throws UserTypeNotAllowedException, CouldNotRegisterReservaException {
        admin.promoverComoAdministrador();
        Localidad valencia = new Localidad("Valencia");
        Localidad alicante = new Localidad("Alicante");
        cadenaHoteles.altaLocalidad(admin, valencia);
        cadenaHoteles.altaLocalidad(admin, alicante);

        cadenaHoteles.altaHotel(admin, "Hotel 1", "Calle El 5 Estrellas, 24", alicante, 50, 60, 60, 85.0);
        cadenaHoteles.altaHotel(admin, "Hotel 2", "Calle El 5 Estrellas, 24", valencia, 50, 60, 60, 85.0);


        AtomicReference<ArrayList<Hotel>> consulta = new AtomicReference<>();
        Assertions.assertThatCode(() -> consulta.set(cadenaHoteles.consulta(valencia, LocalDate.now(), LocalDate.now().plusDays(7), 2, 3))).doesNotThrowAnyException();
        Assertions.assertThat(consulta.get()).isNotEmpty().hasSize(1);
        //No se puede insertar una reserva pasada...
        consulta.get().get(0).reserva(usuario, LocalDate.now().minusDays(7), LocalDate.now().minusDays(3), 2, 3));

        //Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now().minusDays(7), LocalDate.now().minusDays(3), 2, 3)).doesNotThrowAnyException();
        //Assertions.assertThatCode(() -> cadenaHoteles.reserva(consulta.get().get(0).getId(), usuario, LocalDate.now().minusDays(6), LocalDate.now().minusDays(1), 2, 3)).doesNotThrowAnyException();
        Assertions.assertThatCode(() -> cadenaHoteles.actualizarDatosHoteles()).doesNotThrowAnyException();
    }*/

}
