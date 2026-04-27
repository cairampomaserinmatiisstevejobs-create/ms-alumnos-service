package com.msirildev.alumnos.application;

import com.msirildev.alumnos.domain.exception.AlumnoNotFoundException;
import com.msirildev.alumnos.domain.exception.DocumentoDuplicadoException;
import com.msirildev.alumnos.domain.model.Alumno;
import com.msirildev.alumnos.domain.model.EstadoAlumno;
import com.msirildev.alumnos.domain.model.TipoDocumento;
import com.msirildev.alumnos.domain.port.AlumnoRepositoryPort;
import com.msirildev.alumnos.web.model.AlumnoRequest;
import com.msirildev.alumnos.web.model.AlumnoResponse;
import com.msirildev.alumnos.web.model.AlumnoUpdateRequest;
import com.msirildev.alumnos.web.model.PagedAlumnoResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AlumnoService {

    private final AlumnoRepositoryPort repository;

    public AlumnoService(AlumnoRepositoryPort repository) {
        this.repository = repository;
    }

    public Mono<AlumnoResponse> crear(AlumnoRequest req) {
        return repository.existsByNumeroDocumento(req.getNumeroDocumento())
                .flatMap(existe -> existe
                        ? Mono.error(new DocumentoDuplicadoException(req.getNumeroDocumento()))
                        : repository.save(toNewDomain(req)))
                .map(this::toResponse);
    }

    public Mono<PagedAlumnoResponse> listar(int pagina, int tamanio) {
        return Mono.zip(
                repository.countAll(),
                repository.findAll(pagina, tamanio).map(this::toResponse).collectList()
        ).map(t -> {
            long total = t.getT1();
            List<AlumnoResponse> content = t.getT2();
            int totalPaginas = (int) Math.ceil((double) total / tamanio);
            PagedAlumnoResponse paged = new PagedAlumnoResponse();
            paged.setContent(content);
            paged.setPagina(pagina);
            paged.setTamanio(tamanio);
            paged.setTotal(total);
            paged.setTotalPaginas(totalPaginas);
            return paged;
        });
    }

    public Mono<AlumnoResponse> obtenerPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)))
                .map(this::toResponse);
    }

    public Mono<AlumnoResponse> obtenerPorDocumento(String numero) {
        return repository.findByNumeroDocumento(numero)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(numero)))
                .map(this::toResponse);
    }

    public Mono<AlumnoResponse> actualizar(Long id, AlumnoRequest req) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)))
                .flatMap(existente -> {
                    boolean cambia = !existente.numeroDocumento().equals(req.getNumeroDocumento());
                    if (!cambia) return Mono.just(existente);
                    return repository.existsByNumeroDocumento(req.getNumeroDocumento())
                            .flatMap(dup -> dup
                                    ? Mono.error(new DocumentoDuplicadoException(req.getNumeroDocumento()))
                                    : Mono.just(existente));
                })
                .flatMap(existente -> repository.save(new Alumno(
                        existente.id(),
                        TipoDocumento.valueOf(req.getTipoDocumento().name()),
                        req.getNumeroDocumento(),
                        req.getNombres(),
                        req.getApellidos(),
                        req.getFechaNacimiento(),
                        req.getCorreo(),
                        req.getTelefono(),
                        existente.estado()
                )))
                .map(this::toResponse);
    }

    public Mono<AlumnoResponse> actualizarParcial(Long id, AlumnoUpdateRequest req) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)))
                .flatMap(existente -> {
                    String nuevoDoc = req.getNumeroDocumento() != null ? req.getNumeroDocumento() : existente.numeroDocumento();
                    boolean cambia = !existente.numeroDocumento().equals(nuevoDoc);
                    if (!cambia) return Mono.just(existente);
                    return repository.existsByNumeroDocumento(nuevoDoc)
                            .flatMap(dup -> dup
                                    ? Mono.error(new DocumentoDuplicadoException(nuevoDoc))
                                    : Mono.just(existente));
                })
                .flatMap(existente -> repository.save(new Alumno(
                        existente.id(),
                        req.getTipoDocumento() != null ? TipoDocumento.valueOf(req.getTipoDocumento().name()) : existente.tipoDocumento(),
                        req.getNumeroDocumento() != null ? req.getNumeroDocumento() : existente.numeroDocumento(),
                        req.getNombres() != null ? req.getNombres() : existente.nombres(),
                        req.getApellidos() != null ? req.getApellidos() : existente.apellidos(),
                        req.getFechaNacimiento() != null ? req.getFechaNacimiento() : existente.fechaNacimiento(),
                        req.getCorreo() != null ? req.getCorreo() : existente.correo(),
                        req.getTelefono() != null ? req.getTelefono() : existente.telefono(),
                        existente.estado()
                )))
                .map(this::toResponse);
    }

    public Mono<Void> eliminar(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new AlumnoNotFoundException(id)))
                .flatMap(a -> repository.softDelete(a.id()));
    }

    private Alumno toNewDomain(AlumnoRequest req) {
        return new Alumno(null,
                TipoDocumento.valueOf(req.getTipoDocumento().name()),
                req.getNumeroDocumento(), req.getNombres(), req.getApellidos(),
                req.getFechaNacimiento(), req.getCorreo(), req.getTelefono(),
                EstadoAlumno.ACTIVO);
    }

    private AlumnoResponse toResponse(Alumno a) {
        AlumnoResponse r = new AlumnoResponse();
        r.setId(a.id());
        r.setTipoDocumento(AlumnoResponse.TipoDocumentoEnum.valueOf(a.tipoDocumento().name()));
        r.setNumeroDocumento(a.numeroDocumento());
        r.setNombres(a.nombres());
        r.setApellidos(a.apellidos());
        r.setFechaNacimiento(a.fechaNacimiento());
        r.setCorreo(a.correo());
        r.setTelefono(a.telefono());
        r.setEstado(AlumnoResponse.EstadoEnum.valueOf(a.estado().name()));
        return r;
    }
}
