package com.papirotech.biblioteca.service;

public interface CriptografiaService{
    String criptografar(String valor); //Hash

    boolean validar(String valorPuro, String valorCriptografado);
    //vê se o que foi digitado bate com o que está no bd

}