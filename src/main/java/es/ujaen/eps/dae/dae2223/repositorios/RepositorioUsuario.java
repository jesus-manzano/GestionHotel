package es.ujaen.eps.dae.dae2223.repositorios;

import es.ujaen.eps.dae.dae2223.entidades.Usuario;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class RepositorioUsuario {
    @PersistenceContext
    EntityManager em;

    public void guardar(Usuario usuario) {
        em.persist(usuario);
    }

    public void actualizar(Usuario usuario) { em.merge(usuario); }

    public Optional<Usuario> buscarUsuario(String dni) {
        return Optional.ofNullable((em.find(Usuario.class, dni)));
    }

}
