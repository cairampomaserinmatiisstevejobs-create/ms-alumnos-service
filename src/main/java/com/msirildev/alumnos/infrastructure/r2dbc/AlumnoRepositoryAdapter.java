package com.msirildev.alumnos.infrastructure.r2dbc;

import com.msirildev.alumnos.domain.model.Alumno;
import com.msirildev.alumnos.domain.model.EstadoAlumno;
import com.msirildev.alumnos.domain.port.AlumnoRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AlumnoRepositoryAdapter implements AlumnoRepositoryPort {

    private final AlumnoR2dbcRepository repo;

    public AlumnoRepositoryAdapter(AlumnoR2dbcRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Alumno> save(Alumno alumno) {
        return repo.save(AlumnoEntity.from(alumno)).map(AlumnoEntity::toDomain);
    }

    @Override
    public Mono<Alumno> findById(Long id) {
        return repo.findById(id).map(AlumnoEntity::toDomain);
    }

    @Override
    public Mono<Alumno> findByNumeroDocumento(String numero) {
        return repo.findByNumeroDocumento(numero).map(AlumnoEntity::toDomain);
    }

    @Override
    public Flux<Alumno> findAll(int pagina, int tamanio) {
        return repo.findAllBy(PageRequest.of(pagina, tamanio)).map(AlumnoEntity::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return repo.count();
    }

    @Override
    public Mono<Void> softDelete(Long id) {
        return repo.findById(id)
                .flatMap(e -> {
                    e.setEstado(EstadoAlumno.INACTIVO.name());
                    return repo.save(e);
                })
                .then();
    }

    @Override
    public Mono<Boolean> existsByNumeroDocumento(String numero) {
        return repo.existsByNumeroDocumento(numero);
    }
}
