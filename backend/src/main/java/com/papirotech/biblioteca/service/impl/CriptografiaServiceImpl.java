package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.service.CriptografiaService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class CriptografiaServiceImpl implements CriptografiaService {

    private final String SALT_FIXO = "$2a$10$C7841VEV7rtDQfSV16fMfu";

    @Override
    public String criptografar(String valor) {
        return BCrypt.hashpw(valor, SALT_FIXO);
    }
    @Override
    public boolean validar(String valorPuro, String valorCriptografado) {
        String hashGerado = criptografar(valorPuro);
        return hashGerado.equals(valorCriptografado);
    }

}
