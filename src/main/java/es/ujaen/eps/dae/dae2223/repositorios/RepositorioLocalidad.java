package es.ujaen.eps.dae.dae2223.repositorios;

import es.ujaen.eps.dae.dae2223.entidades.Localidad;
import es.ujaen.eps.dae.dae2223.entidades.Usuario;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class RepositorioLocalidad {
    @PersistenceContext
    EntityManager em;

    public void guardar(Localidad localidad) {
        em.persist(localidad);
    }

    public void actualizar(Localidad localidad) {
        em.merge(localidad);
    }

    public Optional<Localidad> buscarLocalidad(String localidad) {
        return Optional.ofNullable((em.find(Localidad.class, localidad)));
    }

}
