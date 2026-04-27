package com.msirildev.alumnos.web.controller;

import com.msirildev.alumnos.application.AlumnoService;
import com.msirildev.alumnos.web.api.AlumnosApi;
import com.msirildev.alumnos.web.model.AlumnoRequest;
import com.msirildev.alumnos.web.model.AlumnoResponse;
import com.msirildev.alumnos.web.model.AlumnoUpdateRequest;
import com.msirildev.alumnos.web.model.PagedAlumnoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AlumnoController implements AlumnosApi {

    private final AlumnoService alumnoService;

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> crearAlumno(
            Mono<AlumnoRequest> alumnoRequest, ServerWebExchange exchange) {
        return alumnoRequest
                .flatMap(alumnoService::crear)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @Override
    public Mono<ResponseEntity<PagedAlumnoResponse>> listarAlumnos(
            Integer pagina, Integer tamanio, ServerWebExchange exchange) {
        return alumnoService.listar(pagina, tamanio)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> obtenerAlumnoPorId(
            Long id, ServerWebExchange exchange) {
        return alumnoService.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> obtenerAlumnoPorDocumento(
            String numero, ServerWebExchange exchange) {
        return alumnoService.obtenerPorDocumento(numero)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> actualizarAlumno(
            Long id, Mono<AlumnoRequest> alumnoRequest, ServerWebExchange exchange) {
        return alumnoRequest
                .flatMap(req -> alumnoService.actualizar(id, req))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AlumnoResponse>> actualizarAlumnoParcial(
            Long id, Mono<AlumnoUpdateRequest> alumnoUpdateRequest, ServerWebExchange exchange) {
        return alumnoUpdateRequest
                .flatMap(req -> alumnoService.actualizarParcial(id, req))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> eliminarAlumno(
            Long id, ServerWebExchange exchange) {
        return alumnoService.eliminar(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }
}
