package com.msirildev.alumnos.web.controller;

import com.msirildev.alumnos.application.AlumnoService;
import com.msirildev.alumnos.domain.model.Alumno;
import com.msirildev.alumnos.domain.model.AlumnoPagina;
import com.msirildev.alumnos.domain.model.AlumnoPatch;
import com.msirildev.alumnos.domain.model.TipoDocumento;
import com.msirildev.alumnos.web.api.AlumnosApi;
import com.msirildev.alumnos.web.model.AlumnoRequest;
import com.msirildev.alumnos.web.model.AlumnoResponse;
import com.msirildev.alumnos.web.model.AlumnoUpdateRequest;
import com.msirildev.alumnos.web.model.PagedAlumnoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class AlumnoController implements AlumnosApi {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> crearAlumno(
            Mono<AlumnoRequest> alumnoRequest, ServerWebExchange exchange) {
        return alumnoRequest
                .map(this::toDomain)
                .flatMap(alumnoService::crear)
                .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(toResponse(a)));
    }

    @Override
    public Mono<ResponseEntity<PagedAlumnoResponse>> listarAlumnos(
            Integer pagina, Integer tamanio, ServerWebExchange exchange) {
        return alumnoService.listar(pagina, tamanio)
                .map(p -> ResponseEntity.ok(toPagedResponse(p)));
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> obtenerAlumnoPorId(
            Long id, ServerWebExchange exchange) {
        return alumnoService.obtenerPorId(id)
                .map(a -> ResponseEntity.ok(toResponse(a)));
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> obtenerAlumnoPorDocumento(
            String numero, ServerWebExchange exchange) {
        return alumnoService.obtenerPorDocumento(numero)
                .map(a -> ResponseEntity.ok(toResponse(a)));
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> actualizarAlumno(
            Long id, Mono<AlumnoRequest> alumnoRequest, ServerWebExchange exchange) {
        return alumnoRequest
                .map(this::toDomain)
                .flatMap(alumno -> alumnoService.actualizar(id, alumno))
                .map(a -> ResponseEntity.ok(toResponse(a)));
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> actualizarAlumnoParcial(
            Long id, Mono<AlumnoUpdateRequest> alumnoUpdateRequest, ServerWebExchange exchange) {
        return alumnoUpdateRequest
                .map(this::toPatch)
                .flatMap(patch -> alumnoService.actualizarParcial(id, patch))
                .map(a -> ResponseEntity.ok(toResponse(a)));
    }

    @Override
    public Mono<ResponseEntity<Void>> eliminarAlumno(
            Long id, ServerWebExchange exchange) {
        return alumnoService.eliminar(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }

    private Alumno toDomain(AlumnoRequest req) {
        return new Alumno(null,
                TipoDocumento.valueOf(req.getTipoDocumento().name()),
                req.getNumeroDocumento(), req.getNombres(), req.getApellidos(),
                req.getFechaNacimiento(), req.getCorreo(), req.getTelefono(), null);
    }

    private AlumnoPatch toPatch(AlumnoUpdateRequest req) {
        return new AlumnoPatch(
                req.getTipoDocumento() != null ? TipoDocumento.valueOf(req.getTipoDocumento().name()) : null,
                req.getNumeroDocumento(), req.getNombres(), req.getApellidos(),
                req.getFechaNacimiento(), req.getCorreo(), req.getTelefono());
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

    private PagedAlumnoResponse toPagedResponse(AlumnoPagina pagina) {
        List<AlumnoResponse> content = pagina.content().stream().map(this::toResponse).toList();
        PagedAlumnoResponse r = new PagedAlumnoResponse();
        r.setContent(content);
        r.setPagina(pagina.pagina());
        r.setTamanio(pagina.tamanio());
        r.setTotal(pagina.total());
        r.setTotalPaginas(pagina.totalPaginas());
        return r;
    }
}
