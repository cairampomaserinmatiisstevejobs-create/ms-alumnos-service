package com.msirildev.alumnos.web.handler;

import com.msirildev.alumnos.domain.exception.AlumnoNotFoundException;
import com.msirildev.alumnos.domain.exception.DocumentoDuplicadoException;
import com.msirildev.alumnos.web.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AlumnoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(AlumnoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("ALUMNO_NO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(DocumentoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicado(DocumentoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("DOCUMENTO_DUPLICADO", ex.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(WebExchangeBindException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("VALIDACION_FALLIDA", msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenerico(Exception ex) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("ERROR_INTERNO", ex.getMessage()));
    }

    private ErrorResponse error(String codigo, String mensaje) {
        ErrorResponse e = new ErrorResponse();
        e.setCodigo(codigo);
        e.setMensaje(mensaje);
        e.setTimestamp(OffsetDateTime.now());
        return e;
    }
}
