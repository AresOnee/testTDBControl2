package com.tbd.control2tbd.DTOs;
import lombok.Data;

@Data
public class UsuarioRegistroDTO {
    private String username;
    private String password;
    private double latitud;
    private double longitud;
}