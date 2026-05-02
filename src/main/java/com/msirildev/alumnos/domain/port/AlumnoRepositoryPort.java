package com.msirildev.alumnos.domain.port;

import com.msirildev.alumnos.domain.model.Alumno;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlumnoRepositoryPort {
    Mono<Alumno> save(Alumno alumno);
    Mono<Alumno> findById(Long id);
    Mono<Alumno> findByNumeroDocumento(String numero);
    Flux<Alumno> findAll(int pagina, int tamanio);
    Mono<Long> countAll();
    Mono<Void> softDelete(Long id);
    Mono<Boolean> existsByNumeroDocumento(String numero);
}
