package com.msirildev.alumnos.domain.model;

import java.util.List;

public record AlumnoPagina(List<Alumno> content, long total, int pagina, int tamanio, int totalPaginas) {}
