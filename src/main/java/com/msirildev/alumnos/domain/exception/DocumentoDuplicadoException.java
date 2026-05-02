package com.msirildev.alumnos.domain.exception;

public class DocumentoDuplicadoException extends RuntimeException {
    public DocumentoDuplicadoException(String numero) {
        super("Ya existe un alumno con documento: " + numero);
    }
}
