package com.tbd.control2tbd.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "sectores")
@Data
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String tipo;

    @JsonIgnore
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point ubicacion;
}