package es.ujaen.eps.dae.dae2223.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CodificadorPass {

    static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private CodificadorPass() {
    }

    public static String codificar(String cadena) {
        return encoder.encode(cadena);
    }

    public static boolean igual(String password, String passwordCodificado) {
        return encoder.matches(password, passwordCodificado);
    }
}
