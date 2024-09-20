package es.ujaen.eps.dae.dae2223.repositorios;

import es.ujaen.eps.dae.dae2223.entidades.Reserva;
import es.ujaen.eps.dae.dae2223.entidades.Usuario;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class RepositorioReserva {
    @PersistenceContext
    EntityManager em;

    public void guardar(Reserva r) {
        em.persist(r);
    }

    public void actualizar(Reserva r) { em.merge(r); }

    public Optional<Reserva> buscarReserva(String reserva) {
        return Optional.ofNullable((em.find(Reserva.class, reserva)));
    }

}
