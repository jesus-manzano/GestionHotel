package es.ujaen.eps.dae.dae2223.seguridad;

import es.ujaen.eps.dae.dae2223.entidades.Usuario;
import es.ujaen.eps.dae.dae2223.excepciones.InvalidCredentialsException;
import es.ujaen.eps.dae.dae2223.servicios.CadenaHoteles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicioDatosUsuario implements UserDetailsService {

    @Autowired
    CadenaHoteles cadenaHoteles;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = cadenaHoteles.getCliente(username).orElseThrow(() -> new UsernameNotFoundException(""));
        String rol = usuario.getTipo().toString();
        return User.withUsername(usuario.getDni()).roles(rol).password(usuario.getPassword()).build();
    }
}
