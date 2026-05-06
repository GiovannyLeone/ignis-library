package com.papirotech.biblioteca.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token; // "credencial de acesso"

    private String nome; // nome usuario

    private String perfil; //descricao de acl ( adm, cliente, estoquista )
}