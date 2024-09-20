package es.ujaen.eps.dae.dae2223.repositorios;

import es.ujaen.eps.dae.dae2223.entidades.Hotel;
import es.ujaen.eps.dae.dae2223.entidades.Localidad;
import es.ujaen.eps.dae.dae2223.excepciones.EmptyConsultResultException;
import es.ujaen.eps.dae.dae2223.excepciones.ModifiedHotelException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Optional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class RepositorioHotel {
    @PersistenceContext
    EntityManager em;

    public void guardar(Hotel h) {
        em.persist(h);
    }

    public void actualizar(Hotel h) throws ModifiedHotelException {
        try {
            em.merge(h);
            em.flush();
        } catch (Exception e) {
            throw new ModifiedHotelException();
        }

    }

    public Optional<Hotel> buscarHotel(int id) {
        return Optional.ofNullable((em.find(Hotel.class, id)));
    }

    public ArrayList<Hotel> consulta(Localidad l, LocalDate fechaInicio, LocalDate fechaFin, int habSimple, int habDoble) throws EmptyConsultResultException {
        TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h WHERE h.localidad = ?1", Hotel.class);
        query.setParameter(1, l);
        if(query.getResultList().isEmpty())
            throw new EmptyConsultResultException();
        return query.getResultList().stream().filter((h) -> h.consulta(fechaInicio, fechaFin, habSimple, habDoble))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<List<Hotel>> obtenerTodosHoteles() {
        return Optional.ofNullable(em.createQuery("SELECT h FROM Hotel h", Hotel.class).getResultList());
    }
}
