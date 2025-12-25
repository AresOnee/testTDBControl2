package com.tbd.control2tbd.Controllers;

import com.tbd.control2tbd.DTOs.SectorDTO;
import com.tbd.control2tbd.Entities.Sector;
import com.tbd.control2tbd.Repositories.SectorRepository;
import com.tbd.control2tbd.Services.SectorService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sectores")
@CrossOrigin("*")
public class SectorController {

    @Autowired
    private SectorService sectorService;
    
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @PostMapping
    public ResponseEntity<Sector> crearSector(@RequestBody SectorDTO dto) {
        Sector sectorCreado = sectorService.crearSector(dto);
        return ResponseEntity.ok(sectorCreado);
    }

    @GetMapping
    public ResponseEntity<List<SectorDTO>> obtenerSectores() {
        List<SectorDTO> sectores = sectorService.obtenerSectores();
        return ResponseEntity.ok(sectores);
    }
}