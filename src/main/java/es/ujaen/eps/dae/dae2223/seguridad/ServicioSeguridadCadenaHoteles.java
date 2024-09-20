package es.ujaen.eps.dae.dae2223.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ServicioSeguridadCadenaHoteles {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.csrf().disable();
        httpSecurity.httpBasic();

        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/cadenaHoteles/clientes").permitAll();

        // Endpoints de ADMIN
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/cadenaHoteles/hoteles").access("hasRole('ADMINISTRADOR')");
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/cadenaHoteles/localidades").access("hasRole('ADMINISTRADOR')");

        return httpSecurity.build();
    }

}
