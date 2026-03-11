package com.lucca.usuario.infrastructure.Exceptions;

public class ConflictException extends RuntimeException{ //Exceção não verificada

    public ConflictException(String mensagem){
        super(mensagem);
    }

    public  ConflictException(String mensagem, Throwable throwable){
        super(mensagem);
    }
}
