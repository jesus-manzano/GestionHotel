package es.ujaen.eps.dae.dae2223.servicios;

import es.ujaen.eps.dae.dae2223.entidades.Hotel;
import es.ujaen.eps.dae.dae2223.entidades.Localidad;
import es.ujaen.eps.dae.dae2223.entidades.TipoUsuario;
import es.ujaen.eps.dae.dae2223.entidades.Usuario;
import es.ujaen.eps.dae.dae2223.excepciones.*;
import es.ujaen.eps.dae.dae2223.repositorios.RepositorioHotel;
import es.ujaen.eps.dae.dae2223.repositorios.RepositorioLocalidad;
import es.ujaen.eps.dae.dae2223.repositorios.RepositorioReserva;
import es.ujaen.eps.dae.dae2223.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
@Validated
@Transactional(propagation= Propagation.SUPPORTS, readOnly=true)
public class CadenaHoteles {

    @Autowired
    RepositorioLocalidad repositorioLocalidad;

    @Autowired
    RepositorioUsuario repositorioUsuario;

    @Autowired
    RepositorioHotel repositorioHotel;

    @Autowired
    RepositorioReserva repositorioReserva;
    /**
     * @author José Guerrero Gallego - jgg00085@red.ujaen.es
     */
    public CadenaHoteles() {
    }

    /**
     * Función que permite añadir al sistema a un nuevo cliente, el cual podrá realizar consultas y reservar
     * @param usuario con todos los datos requeridos rellenos
     * @return true si se ha creado conforme a especificación
     */
    public Usuario altaCliente(@NotNull @Valid Usuario usuario) throws UserAlreadyExistsException {
        if (repositorioUsuario.buscarUsuario(usuario.getDni()).isPresent()) {
           throw new UserAlreadyExistsException();
        }
        repositorioUsuario.guardar(usuario);
        return usuario;
    }

    /**
     *
     * @param dni el DNI del cliente, debe ser válido
     * @param password su contraseña
     * @return los datos de USUARIO asociado
     */
    public Usuario login(@NotBlank String dni, @NotBlank String password) throws InvalidCredentialsException {
        Optional<Usuario> u = repositorioUsuario.buscarUsuario(dni);
        if (u.isPresent()){
            Usuario encontrado = u.get();
            if (encontrado.validarClave(password))
                return encontrado;
            else
                throw new InvalidCredentialsException();
        }
        else {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Función que promueve a un usuario como administrador, solo accesible mediante clave en API REST
     * @param dni identificador del usuario a convertir en administrador
     */
    public void makeAdmin(@NotNull String dni) {
        repositorioUsuario.buscarUsuario(dni).ifPresentOrElse(u -> {
            u.promoverComoAdministrador();
            repositorioUsuario.actualizar(u);
        }, () -> new EmptyConsultResultException());
    }

    /**
     * Función para dar de alta las localidades, en las que se encuentran los hoteles
     * @param localidad
     * @throws UserTypeNotAllowedException
     */
    //public void altaLocalidad(@NotNull @Valid Usuario usuario, @NotNull Localidad localidad) throws UserTypeNotAllowedException {
    public void altaLocalidad(@NotNull Localidad localidad) throws UserTypeNotAllowedException, PlaceAlreadyExistsException {
        if (repositorioLocalidad.buscarLocalidad(localidad.getLocalidad()).isPresent())
            throw new PlaceAlreadyExistsException();
        repositorioLocalidad.guardar(localidad);
    }

    /**
     * Función que permite dar de alta un Hotel dentro del sistema
     * @param ho
     * @throws UserTypeNotAllowedException
     */
    public void altaHotel(@NotNull Hotel ho) throws UserTypeNotAllowedException, HotelAlreadyExistsException {
        List<Hotel> hoteles = repositorioHotel.obtenerTodosHoteles().get();
        List<Hotel> hFiltrados = hoteles.stream().filter(h -> h.getLocalidad().getLocalidad() == ho.getLocalidad().getLocalidad() && h.getDireccion() == ho.getDireccion()).toList();
        if (!hFiltrados.isEmpty())
            throw new HotelAlreadyExistsException();
        repositorioHotel.guardar(ho);
    }

    /**
     * Función que permite a un usuario consultar la disponibilidad de hoteles para realizar una reserva dado un rango de fechas, habitaciones y localidad
     * @param destino Localidad donde se desea buscar el hotel
     * @param fechaInicio Fecha, inclusive, cuando comenzaría la estancia
     * @param fechaFin Fecha, inclusive, cuando finalizaría la estancia
     * @param habSimple Número de habitaciones para una persona a reservar
     * @param habDoble Número de habitaciones para dos personas a reservar
     * @return lista con los ids de hoteles, si los hubiera, que tienen habitaciones disponibles según los datos facilitados
     */
    public ArrayList<Hotel> consulta(@NotNull Localidad destino, @FutureOrPresent LocalDate fechaInicio, @Future LocalDate fechaFin, @PositiveOrZero int habSimple, @PositiveOrZero int habDoble) throws EmptyConsultResultException {
        return repositorioHotel.consulta(destino, fechaInicio, fechaFin, habSimple, habDoble);

        /*return hoteles.entrySet().stream().filter(x ->
                        x.getValue().getLocalidad().getLocalidad().equals(destino.getLocalidad())
                                && x.getValue().consulta(fechaInicio, fechaFin, habSimple, habDoble))
                .map(x->x.getKey())
                .collect(Collectors.toCollection(ArrayList::new));*/
    }

    /**
     * Función a usar tras la consulta, permite realizar una reserva en un hotel que se sabe que tiene disponibilidad
     * @param idHotel el hotel a buscar para realizar la reserva
     * @param usuario el usuario para adjuntar sus datos en la reserva
     * @param fechaInicio Fecha, inclusive, cuando comenzaría la estancia
     * @param fechaFin Fecha, inclusive, cuando finalizaría la estancia
     * @param habSimple Número de habitaciones para una persona a reservar
     * @param habDoble Número de habitaciones para dos personas a reservar
     * @return 'True' si se ha realizado la reserva. Podría devolver 'false' si ha dejado de haber disponibilidad entre el tiempo de consulta() y el de reserva
     */
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor=ModifiedHotelException.class, readOnly=false)
    public void reserva(@PositiveOrZero int idHotel, @Valid Usuario usuario, @FutureOrPresent LocalDate fechaInicio, @Future LocalDate fechaFin, @PositiveOrZero int habSimple, @PositiveOrZero int habDoble) throws HotelNotExistException, CouldNotRegisterReservaException, ModifiedHotelException, InternalErrorException {
        boolean reintentar = false;
        do {
            try {
                Optional<Hotel> busqueda = repositorioHotel.buscarHotel(idHotel);
                if (!busqueda.isPresent())
                    throw new HotelNotExistException();
                Hotel h = busqueda.get();
                h.reserva(usuario, fechaInicio, fechaFin, habSimple, habDoble);
                repositorioHotel.actualizar(h);
                reintentar = false;
            } catch (ModifiedHotelException e) {
                reintentar = true;
            } catch (HotelNotExistException e1) {
                throw new HotelNotExistException();
            } catch (CouldNotRegisterReservaException e2) {
                throw new CouldNotRegisterReservaException();
            } catch (Exception e3) {
                Logger.getLogger(CadenaHoteles.class.getName()).log(Level.SEVERE, e3.getMessage());
                throw new InternalErrorException();
            }
        } while (reintentar);
    }

    /**
     * Actualiza todos los datos de los hoteles de forma automática todos los días a las 3 am
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor=ModifiedHotelException.class, readOnly=false)
    public void actualizarDatosHoteles(){
        Optional<List<Hotel>> busqueda = repositorioHotel.obtenerTodosHoteles();
        // Para cada hotel de la base de datos, se actualizan sus reservas antiguas y se actualiza en la BBDD.
        busqueda.ifPresent(hotels -> hotels.forEach((h) -> {
            h.actualizarReservasAntiguas();
            try {
                repositorioHotel.actualizar(h);
            } catch (Exception | ModifiedHotelException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public Optional<Usuario> getCliente(@NotBlank String dni) {
        return repositorioUsuario.buscarUsuario(dni);
    }
}
