package com.tbd.control2tbd.DTOs;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TareaDTO {
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaVencimiento;
    private Long usuarioId;
    private Long sectorId;
}