package com.msirildev.alumnos.infrastructure.r2dbc;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlumnoR2dbcRepository extends ReactiveCrudRepository<AlumnoEntity, Long> {
    Mono<AlumnoEntity> findByNumeroDocumento(String numero);
    Mono<Boolean> existsByNumeroDocumento(String numero);
    Flux<AlumnoEntity> findAllBy(Pageable pageable);
}
