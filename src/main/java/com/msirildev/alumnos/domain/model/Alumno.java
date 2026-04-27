package com.msirildev.alumnos.domain.model;

import java.time.LocalDate;

public record Alumno(
        Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String correo,
        String telefono,
        EstadoAlumno estado
) {}
