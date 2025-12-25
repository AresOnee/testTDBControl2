package com.tbd.control2tbd.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
@Data
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    // Por defecto la tarea nace como 'PENDIENTE'
    private String estado = "PENDIENTE";

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"password", "ubicacion"})
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    @JsonIgnoreProperties({"ubicacion"})
    private Sector sector;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}