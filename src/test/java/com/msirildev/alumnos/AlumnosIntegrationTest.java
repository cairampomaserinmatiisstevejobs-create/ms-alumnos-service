package com.msirildev.alumnos;

import com.msirildev.alumnos.web.model.AlumnoRequest;
import com.msirildev.alumnos.web.model.AlumnoUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AlumnosIntegrationTest {

    @Autowired
    private WebTestClient client;

    private AlumnoRequest alumnoBase() {
        AlumnoRequest req = new AlumnoRequest();
        req.setTipoDocumento(AlumnoRequest.TipoDocumentoEnum.DNI);
        req.setNumeroDocumento("TEST" + System.nanoTime());
        req.setNombres("Test Nombres");
        req.setApellidos("Test Apellidos");
        req.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        req.setCorreo("test" + System.nanoTime() + "@test.com");
        req.setTelefono("987654321");
        return req;
    }

    @Test
    void crearAlumno_debeRetornar201() {
        client.post().uri("/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(alumnoBase())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.estado").isEqualTo("ACTIVO");
    }

    @Test
    void crearAlumno_documentoDuplicado_debeRetornar409() {
        AlumnoRequest req = alumnoBase();
        client.post().uri("/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated();

        client.post().uri("/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("DOCUMENTO_DUPLICADO");
    }

    @Test
    void listarAlumnos_debeRetornar200ConPaginacion() {
        client.get().uri("/alumnos?pagina=0&tamanio=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.pagina").isEqualTo(0)
                .jsonPath("$.tamanio").isEqualTo(5);
    }

    @Test
    void obtenerPorId_existente_debeRetornar200() {
        AlumnoRequest req = alumnoBase();
        Long[] ids = new Long[1];

        client.post().uri("/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").value(id -> ids[0] = ((Integer) id).longValue());

        client.get().uri("/alumnos/" + ids[0])
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numero_documento").isEqualTo(req.getNumeroDocumento());
    }

    @Test
    void obtenerPorId_noExistente_debeRetornar404() {
        client.get().uri("/alumnos/99999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("ALUMNO_NO_ENCONTRADO");
    }

    @Test
    void obtenerPorDocumento_debeRetornar200() {
        AlumnoRequest req = alumnoBase();
        client.post().uri("/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated();

        client.get().uri("/alumnos/documento/" + req.getNumeroDocumento())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numero_documento").isEqualTo(req.getNumeroDocumento());
    }

    @Test
    void actualizarParcial_debeActualizarSoloCamposEnviados() {
        AlumnoRequest req = alumnoBase();
        Long[] ids = new Long[1];

        client.post().uri("/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").value(id -> ids[0] = ((Integer) id).longValue());

        AlumnoUpdateRequest patch = new AlumnoUpdateRequest();
        patch.setTelefono("111222333");

        client.patch().uri("/alumnos/" + ids[0])
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(patch)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.telefono").isEqualTo("111222333")
                .jsonPath("$.nombres").isEqualTo(req.getNombres());
    }

    @Test
    void eliminarAlumno_debeCambiarEstadoAInactivo() {
        AlumnoRequest req = alumnoBase();
        Long[] ids = new Long[1];

        client.post().uri("/alumnos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").value(id -> ids[0] = ((Integer) id).longValue());

        client.delete().uri("/alumnos/" + ids[0])
                .exchange()
                .expectStatus().isNoContent();

        client.get().uri("/alumnos/" + ids[0])
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("INACTIVO");
    }

    @Test
    void eliminarAlumno_noExistente_debeRetornar404() {
        client.delete().uri("/alumnos/99999")
                .exchange()
                .expectStatus().isNotFound();
    }
}
