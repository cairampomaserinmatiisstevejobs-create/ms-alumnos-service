package com.msirildev.alumnos.domain.exception;

public class AlumnoNotFoundException extends RuntimeException {
    public AlumnoNotFoundException(Long id) {
        super("Alumno no encontrado con id: " + id);
    }
    public AlumnoNotFoundException(String documento) {
        super("Alumno no encontrado con documento: " + documento);
    }
}
