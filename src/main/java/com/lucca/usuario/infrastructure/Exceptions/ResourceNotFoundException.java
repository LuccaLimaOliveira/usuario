package com.lucca.usuario.infrastructure.Exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException (String mensagem){
        super(mensagem);
    }

    public ResourceNotFoundException (String mensagem, Throwable throwable){
        super(mensagem, throwable);
    }
}
