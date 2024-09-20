package es.ujaen.eps.dae.dae2223.rest;


import es.ujaen.eps.dae.dae2223.entidades.Localidad;
import es.ujaen.eps.dae.dae2223.rest.dto.DTOHotel;
import es.ujaen.eps.dae.dae2223.rest.dto.DTOLocalidad;
import es.ujaen.eps.dae.dae2223.rest.dto.DTOUsuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest(classes = es.ujaen.eps.dae.dae2223.app.Dae2223Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class ControladorRESTTest {

    @LocalServerPort
    int localPort;

    @Autowired
    MappingJackson2HttpMessageConverter springBootJacksonConverter;

    TestRestTemplate restTemplate;

    /**
     * Crear un TestRestTemplate para las pruebas
     */
    @PostConstruct
    void crearRestTemplateBuilder() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort + "/cadenaHoteles")
                .additionalMessageConverters(List.of(springBootJacksonConverter));

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testAltaUsuario() {
        String dni = "12345678G";
        String clave = "clave";
        DTOUsuario usu = new DTOUsuario(dni, "Alumno", "Ben Saprut 1", "601234567",
                "alumno@red.ujaen.es", LocalDate.of(1998, 9, 20), clave);

        ResponseEntity<DTOUsuario> respuestaAlta = restTemplate.postForEntity("/clientes", usu, DTOUsuario.class);
        Assertions.assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<DTOUsuario> respuestaLogin = restTemplate.withBasicAuth(dni, clave).getForEntity("/clientes/" + dni, DTOUsuario.class);
        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);

        DTOUsuario clienteLogin = respuestaLogin.getBody();
        Assertions.assertThat(clienteLogin.dni()).isEqualTo(dni);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testLocalidad() {
        String dni = "12345678G";
        String clave = "clave";
        DTOUsuario usu = new DTOUsuario(dni, "Alumno", "Ben Saprut 1", "601234567",
                "alumno@red.ujaen.es", LocalDate.of(1998, 9, 20), clave);

        ResponseEntity<DTOUsuario> respuestaAlta = restTemplate.postForEntity("/clientes", usu, DTOUsuario.class);
        Assertions.assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<Object> respuestaAdmin = restTemplate.withBasicAuth(dni, clave).getForEntity("/makeAdmin/" + dni + "/DAEn22-23", null);
        Assertions.assertThat(respuestaAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<DTOLocalidad> respuestaLocalidad = restTemplate.withBasicAuth(dni, clave).postForEntity("/localidades/", new Localidad("Sevilla"), DTOLocalidad.class);
        Assertions.assertThat(respuestaLocalidad.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    /**
     * Este test es integral, es decir, contiene lo siguiente:
     * - Creación de usuario
     * - Creación de usuario2
     * - Promoción de usuario2 como admin
     * - Creación de Localidad (admin)
     * - Creación de Hotel (admin)
     * - Consulta que no devuelve nada por demasiadas habitaciones pedidas (usuario)
     * - Consulta para una localidad y fechas determinadas (usuario)
     * - Reserva (usuario)
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testIntegral() {
        String dni = "12345678G";
        String clave = "clave";
        DTOUsuario usu = new DTOUsuario(dni, "Alumno", "Ben Saprut 1", "601234567",
                "alumno@red.ujaen.es", LocalDate.of(1998, 9, 20), clave);

        String dni2 = "12345679G";
        String clave2 = "admin";
        DTOUsuario admin = new DTOUsuario(dni2, "Alumno", "Ben Saprut 1", "601234567",
                "alumno@red.ujaen.es", LocalDate.of(1998, 9, 20), clave2);

        ResponseEntity<DTOUsuario> respuestaAlta = restTemplate.postForEntity("/clientes", usu, DTOUsuario.class);
        Assertions.assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<DTOUsuario> respuestaAlta2 = restTemplate.postForEntity("/clientes", admin, DTOUsuario.class);
        Assertions.assertThat(respuestaAlta2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<Object> respuestaAdmin = restTemplate.withBasicAuth(dni2, clave2).getForEntity("/makeAdmin/" + dni2 + "/DAEn22-23", null);
        Assertions.assertThat(respuestaAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<DTOLocalidad> respuestaLocalidad = restTemplate.withBasicAuth(dni2, clave2).postForEntity("/localidades/", new Localidad("Granada"), DTOLocalidad.class);
        Assertions.assertThat(respuestaLocalidad.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DTOHotel hotel = new DTOHotel(0, "hotelazo", "Calle turista", new Localidad("Granada"), 15, 50, 25, 70);
        ResponseEntity<DTOHotel> respuestaAltaHotel = restTemplate.withBasicAuth(dni2, clave2).postForEntity("/hoteles", hotel, DTOHotel.class);
        Assertions.assertThat(respuestaAltaHotel.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        //Terminada preparación, realizar consulta propiamente dicha
        ResponseEntity<DTOHotel[]> respuestaConsultaNoPosible = restTemplate.withBasicAuth(dni, clave).
                getForEntity("/hoteles?localidad=Granada&fechaInicio=" +
                        LocalDate.now().format(DateTimeFormatter.ISO_DATE) +
                        "&fechaFin=" + LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_DATE) +
                        "&habSimple=50&habDoble=2", DTOHotel[].class);
        Assertions.assertThat(respuestaConsultaNoPosible.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaConsultaNoPosible.getBody().length == 0).isTrue();


        ResponseEntity<DTOHotel[]> respuestaConsulta = restTemplate.withBasicAuth(dni, clave).
                getForEntity("/hoteles?localidad=Granada&fechaInicio=" +
                        LocalDate.now().format(DateTimeFormatter.ISO_DATE) +
                        "&fechaFin=" + LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_DATE) +
                        "&habSimple=1&habDoble=2", DTOHotel[].class);
        Assertions.assertThat(respuestaConsulta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaConsulta.getBody().length > 0).isTrue();


        ResponseEntity<DTOUsuario> respuestaReserva = restTemplate.withBasicAuth(dni, clave).
                postForEntity("/hoteles/" + respuestaConsulta.getBody()[0].id() +
                        "/reservas?fechaInicio=" +
                        LocalDate.now().format(DateTimeFormatter.ISO_DATE) +
                        "&fechaFin=" + LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_DATE) +
                        "&habSimple=1&habDoble=2", usu, DTOUsuario.class);
        Assertions.assertThat(respuestaReserva.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
