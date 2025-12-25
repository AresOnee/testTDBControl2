package com.tbd.control2tbd.DTOs;

import lombok.Data;

@Data
public class SectorDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private double latitud;
    private double longitud;
}
