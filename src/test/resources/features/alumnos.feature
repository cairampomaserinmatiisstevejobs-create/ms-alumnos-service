# language: es
Feature: Registro y Gestion de Alumnos
  Como administrador del sistema
  Quiero gestionar el registro de alumnos
  Para mantener un padron actualizado de manera reactiva

  Scenario: Crear alumno exitosamente
    Given que tengo los datos validos de un alumno con DNI "12345678"
    When envio POST /api/v1/alumnos con esos datos
    Then recibo respuesta 201
    And el alumno tiene estado "ACTIVO"
    And el alumno tiene un id generado

  Scenario: Crear alumno con documento duplicado
    Given que ya existe un alumno con DNI "12345678"
    When intento crear otro alumno con el mismo DNI "12345678"
    Then recibo respuesta 409
    And el codigo de error es "DOCUMENTO_DUPLICADO"

  Scenario: Listar alumnos paginado
    Given que existen alumnos registrados en el sistema
    When consulto GET /api/v1/alumnos con pagina=0 y tamanio=10
    Then recibo respuesta 200
    And la respuesta contiene los campos: content, pagina, tamanio, total, totalPaginas

  Scenario: Obtener alumno por ID existente
    Given que existe un alumno con id=1
    When consulto GET /api/v1/alumnos/1
    Then recibo respuesta 200
    And los datos del alumno corresponden al id=1

  Scenario: Obtener alumno por ID no existente
    Given que no existe un alumno con id=99999
    When consulto GET /api/v1/alumnos/99999
    Then recibo respuesta 404
    And el codigo de error es "ALUMNO_NO_ENCONTRADO"

  Scenario: Obtener alumno por numero de documento
    Given que existe un alumno con documento "12345678"
    When consulto GET /api/v1/alumnos/documento/12345678
    Then recibo respuesta 200
    And el numero_documento del alumno es "12345678"

  Scenario: Actualizar parcialmente un alumno (PATCH)
    Given que existe un alumno con id=1 y telefono "987654321"
    When envio PATCH /api/v1/alumnos/1 con telefono "111222333"
    Then recibo respuesta 200
    And el telefono del alumno es "111222333"
    And el resto de campos no cambiaron

  Scenario: Dar de baja logica a un alumno (DELETE)
    Given que existe un alumno con id=1 y estado "ACTIVO"
    When envio DELETE /api/v1/alumnos/1
    Then recibo respuesta 204
    And al consultar GET /api/v1/alumnos/1 el estado es "INACTIVO"

  Scenario: Dar de baja alumno no existente
    Given que no existe un alumno con id=99999
    When envio DELETE /api/v1/alumnos/99999
    Then recibo respuesta 404
    And el codigo de error es "ALUMNO_NO_ENCONTRADO"
