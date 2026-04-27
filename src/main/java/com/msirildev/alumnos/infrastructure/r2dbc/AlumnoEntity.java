package com.msirildev.alumnos.infrastructure.r2dbc;

import com.msirildev.alumnos.domain.model.Alumno;
import com.msirildev.alumnos.domain.model.EstadoAlumno;
import com.msirildev.alumnos.domain.model.TipoDocumento;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("alumnos")
public class AlumnoEntity {

    @Id
    private Long id;
    @Column("tipo_documento")
    private String tipoDocumento;
    @Column("numero_documento")
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;
    private String correo;
    private String telefono;
    private String estado;

    public AlumnoEntity() {}

    public static AlumnoEntity from(Alumno a) {
        AlumnoEntity e = new AlumnoEntity();
        e.id = a.id();
        e.tipoDocumento = a.tipoDocumento().name();
        e.numeroDocumento = a.numeroDocumento();
        e.nombres = a.nombres();
        e.apellidos = a.apellidos();
        e.fechaNacimiento = a.fechaNacimiento();
        e.correo = a.correo();
        e.telefono = a.telefono();
        e.estado = a.estado().name();
        return e;
    }

    public Alumno toDomain() {
        return new Alumno(id,
                TipoDocumento.valueOf(tipoDocumento),
                numeroDocumento, nombres, apellidos,
                fechaNacimiento, correo, telefono,
                EstadoAlumno.valueOf(estado));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
