package com.papirotech.biblioteca.service;

import com.papirotech.biblioteca.dto.request.LoginRequest;
import com.papirotech.biblioteca.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse autenticar(LoginRequest request);

}