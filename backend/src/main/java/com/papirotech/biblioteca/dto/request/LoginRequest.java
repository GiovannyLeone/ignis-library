package com.papirotech.biblioteca.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    //pode receber tanto cpf quanto código de acesso

    private String login;

    private String senha;
}