package com.msirildev.alumnos.application;

import com.msirildev.alumnos.domain.exception.AlumnoNotFoundException;
import com.msirildev.alumnos.domain.exception.DocumentoDuplicadoException;
import com.msirildev.alumnos.domain.model.Alumno;
import com.msirildev.alumnos.domain.model.AlumnoPagina;
import com.msirildev.alumnos.domain.model.AlumnoPatch;
import com.msirildev.alumnos.domain.model.EstadoAlumno;
import com.msirildev.alumnos.domain.port.AlumnoRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AlumnoService {

    private final AlumnoRepositoryPort repository;

    public AlumnoService(AlumnoRepositoryPort repository) {
        this.repository = repository;
    }

    public Mono<Alumno> crear(Alumno alumno) {
        return repository.existsByNumeroDocumento(alumno.numeroDocumento())
                .flatMap(existe -> existe
                        ? Mono.error(new DocumentoDuplicadoException(alumno.numeroDocumento()))
                        : repository.save(new Alumno(null, alumno.tipoDocumento(), alumno.numeroDocumento(),
                                alumno.nombres(), alumno.apellidos(), alumno.fechaNacimiento(),
                                alumno.correo(), alumno.telefono(), EstadoAlumno.ACTIVO)));
    }

    public Mono<AlumnoPagina> listar(int pagina, int tamanio) {
        return Mono.zip(
                repository.countAll(),
                repository.findAll(pagina, tamanio).collectList()
        ).map(t -> {
            long total = t.getT1();
            int totalPaginas = (int) Math.ceil((double) total / tamanio);
            return new AlumnoPagina(t.getT2(), total, pagina, tamanio, totalPaginas);
        });
    }

    public Mono<Alumno> obtenerPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)));
    }

    public Mono<Alumno> obtenerPorDocumento(String numero) {
        return repository.findByNumeroDocumento(numero)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(numero)));
    }

    public Mono<Alumno> actualizar(Long id, Alumno alumno) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)))
                .flatMap(existente -> {
                    boolean cambia = !existente.numeroDocumento().equals(alumno.numeroDocumento());
                    if (!cambia) return Mono.just(existente);
                    return repository.existsByNumeroDocumento(alumno.numeroDocumento())
                            .flatMap(dup -> dup
                                    ? Mono.error(new DocumentoDuplicadoException(alumno.numeroDocumento()))
                                    : Mono.just(existente));
                })
                .flatMap(existente -> repository.save(new Alumno(
                        existente.id(), alumno.tipoDocumento(), alumno.numeroDocumento(),
                        alumno.nombres(), alumno.apellidos(), alumno.fechaNacimiento(),
                        alumno.correo(), alumno.telefono(), existente.estado())));
    }

    public Mono<Alumno> actualizarParcial(Long id, AlumnoPatch patch) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)))
                .flatMap(existente -> {
                    String nuevoDoc = patch.numeroDocumento() != null ? patch.numeroDocumento() : existente.numeroDocumento();
                    boolean cambia = !existente.numeroDocumento().equals(nuevoDoc);
                    if (!cambia) return Mono.just(existente);
                    return repository.existsByNumeroDocumento(nuevoDoc)
                            .flatMap(dup -> dup
                                    ? Mono.error(new DocumentoDuplicadoException(nuevoDoc))
                                    : Mono.just(existente));
                })
                .flatMap(existente -> repository.save(new Alumno(
                        existente.id(),
                        patch.tipoDocumento() != null ? patch.tipoDocumento() : existente.tipoDocumento(),
                        patch.numeroDocumento() != null ? patch.numeroDocumento() : existente.numeroDocumento(),
                        patch.nombres() != null ? patch.nombres() : existente.nombres(),
                        patch.apellidos() != null ? patch.apellidos() : existente.apellidos(),
                        patch.fechaNacimiento() != null ? patch.fechaNacimiento() : existente.fechaNacimiento(),
                        patch.correo() != null ? patch.correo() : existente.correo(),
                        patch.telefono() != null ? patch.telefono() : existente.telefono(),
                        existente.estado())));
    }

    public Mono<Void> eliminar(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)))
                .flatMap(a -> repository.softDelete(a.id()));
    }
}
