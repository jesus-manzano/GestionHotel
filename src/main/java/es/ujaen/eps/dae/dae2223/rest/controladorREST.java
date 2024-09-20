package es.ujaen.eps.dae.dae2223.rest;

import es.ujaen.eps.dae.dae2223.entidades.Hotel;
import es.ujaen.eps.dae.dae2223.entidades.Localidad;
import es.ujaen.eps.dae.dae2223.entidades.Usuario;
import es.ujaen.eps.dae.dae2223.excepciones.*;
import es.ujaen.eps.dae.dae2223.rest.dto.DTOHotel;
import es.ujaen.eps.dae.dae2223.rest.dto.DTOLocalidad;
import es.ujaen.eps.dae.dae2223.rest.dto.DTOUsuario;
import es.ujaen.eps.dae.dae2223.servicios.CadenaHoteles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Controlador REST para los servicios de cadenaHoteles */
@RestController
@RequestMapping("/cadenaHoteles")
public class controladorREST {
    @Autowired
    CadenaHoteles servicios;

    /** Creación de las localidades */
    @PostMapping("/localidades")
    ResponseEntity<Void> altaLocalidad(@RequestBody DTOLocalidad localidad) {
        try {
            servicios.altaLocalidad(localidad.aLocalidad());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserTypeNotAllowedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (PlaceAlreadyExistsException e1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Endpoint que permite convertir a un usuario en administrador dado un token hardcodeado aquí
     * @param dni
     * @param token
     * @return
     */
    @RequestMapping(value = "/makeAdmin/{dni}/{token}", method = RequestMethod.GET)
    ResponseEntity<Void> makeAdmin(@PathVariable String dni, @PathVariable String token) {
        final String tokenAdmin = "DAEn22-23";
        if (token != null && token.equals(tokenAdmin)) {
            servicios.makeAdmin(dni);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/clientes/{dni}")
    ResponseEntity<DTOUsuario> obtenerDetallesUsuario(@PathVariable String dni) {
        Optional<Usuario> u = servicios.getCliente(dni);
        return u.map(usu -> ResponseEntity.ok(new DTOUsuario(usu))).orElse(ResponseEntity.notFound().build());
    }

    /** Creación de los clientes */
    @PostMapping("/clientes")
    ResponseEntity<Void> altaCliente(@RequestBody DTOUsuario usuario){
        try {
            servicios.altaCliente(usuario.aUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /** Creación de los hoteles */
    @PostMapping("/hoteles")
    ResponseEntity<Void> altaHotel(@RequestBody DTOHotel hotel){
        try {
            servicios.altaHotel(hotel.aHotel());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserTypeNotAllowedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (HotelAlreadyExistsException e1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /** Obtener hoteles disponibles dada una localidad, fechas de inicio/fin y nº de hab. simples/dobles */
    @GetMapping("/hoteles")
    ResponseEntity<List<DTOHotel>> consulta(
            @RequestParam DTOLocalidad localidad,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam int habSimple,
            @RequestParam int habDoble) {

        try {
            List<Hotel> hoteles = servicios.consulta(localidad.aLocalidad(), fechaInicio, fechaFin, habSimple, habDoble);
            List<DTOHotel> aux = new ArrayList<>();
            hoteles.forEach(hotel -> aux.add(new DTOHotel(hotel)));
            return ResponseEntity.ok(aux);
        } catch (EmptyConsultResultException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /** Añadir una reserva nueva al hotel indicado */
    @PostMapping("/hoteles/{hotel}/reservas")
    ResponseEntity<Void> reserva(
            @PathVariable int hotel,
            @RequestBody DTOUsuario usuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam int habSimple,
            @RequestParam int habDoble
    ){
        try {
            servicios.reserva(hotel, usuario.aUsuario(), fechaInicio, fechaFin, habSimple, habDoble);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CouldNotRegisterReservaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InternalErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (HotelNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ModifiedHotelException e) {
            return ResponseEntity.status(HttpStatus.IM_USED).build();
        }
    }
}
